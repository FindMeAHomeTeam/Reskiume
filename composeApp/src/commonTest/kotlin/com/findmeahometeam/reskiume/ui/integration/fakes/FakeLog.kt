package com.findmeahometeam.reskiume.ui.integration.fakes

import com.findmeahometeam.reskiume.data.util.log.Log

class FakeLog: Log {
    override fun d(tag: String, message: String) {
        println("DEBUG: [$tag] $message")
    }

    override fun w(tag: String, message: String) {
        println("WARNING: [$tag] $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("ERROR: [$tag] $message ${throwable?.printStackTrace() ?: ""}")
    }
}
