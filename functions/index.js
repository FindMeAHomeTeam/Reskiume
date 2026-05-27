/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
const {setGlobalOptions} = require("firebase-functions");
setGlobalOptions({maxInstances: 10});

const admin = require("firebase-admin");
const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {onCall, HttpsError} = require("firebase-functions/https");
admin.initializeApp();

exports.notifyNewRescueEvent =
  onDocumentCreated("rescueEvents/{eventId}", async (event) => {
    // Get the data
    const newEvent = event.data.data();
    const creatorId = newEvent.creatorId;
    const rescueEventId = newEvent.id;
    const country = newEvent.country;
    const city = newEvent.city;
    if (country == null ||
        country == "UNSELECTED" ||
        city == null ||
        city == "UNSELECTED") {
      console.log("No country or city specified, skipping notification.");
      return null;
    }
    const deeplink = `vegan-for-the-animals://reskiu.me/rescueEvent/${creatorId}/${rescueEventId}`;
    // Create the notification payload
    const payload = {
      data: {
        notificationType: "rescueEvent",
        creatorId: creatorId,
        deeplink: deeplink,
      },
      apns: {
        headers: {
          "apns-push-type": "alert",
          "apns-priority": "10",
        },
        payload: {
          aps: {
            "sound": "default",
            "mutable-content": 1,
            "alert": {
              titleLocKey: "NOTIFICATION_RESCUE_EVENT_TITLE",
              locKey: "NOTIFICATION_RESCUE_EVENT_BODY",
            },
          },
        },
      },
      topic: `${country}${city}`,
    };
    // Send the message via Cloud Messaging
    try {
      const response = await admin.messaging().send(payload);
      console.log(
          `Successfully sent deeplink ${payload.data.deeplink} for 
          the topic ${payload.topic} -`,
          response,
      );
    } catch (error) {
      console.error("Error sending message:", error);
    }
    return null;
  });

exports.deleteRemoteChat =
  onCall(async (request) => {
    // Only allow users who create the chat to execute this function.
    if (request.auth.uid !== request.data.uid) {
      throw new HttpsError(
          "permission-denied",
          "Must be the author user to initiate the chat deletion.",
      );
    }
    const path = request.data.path;
    console.log(
        `User ${request.auth.uid} has requested to delete the chat ${path}`,
    );
    const docRef = admin.firestore().doc(path);
    await admin.firestore().recursiveDelete(docRef);
    return {
      path: path,
    };
  });

exports.deleteAllRemoteChatsFromUser =
  onCall(async (request) => {
    // Only allow users who create the chat to execute this function.
    if (request.auth.uid !== request.data.uid) {
      throw new HttpsError(
          "permission-denied",
          "Must be the author user to initiate the chat deletion.",
      );
    }
    const paths = request.data.paths;
    console.log(
        `User ${request.auth.uid} has requested to delete the chats ${paths}`,
    );
    for (const path of paths) {
      const docRef = admin.firestore().doc(path);
      await admin.firestore().recursiveDelete(docRef);
    }
    return {
      paths: paths,
    };
  });
