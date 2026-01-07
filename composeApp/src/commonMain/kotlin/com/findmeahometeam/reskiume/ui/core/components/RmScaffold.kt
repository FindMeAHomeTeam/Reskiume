package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.back_arrow_content_description
import reskiume.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RmScaffold(
    title: String = "",
    onBackPressed: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.background(backgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    RmText(
                        modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                        text = title,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 16.dp).size(32.dp),
                        onClick = onBackPressed
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.back_arrow_content_description),
                            tint = textColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = backgroundColor)
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}