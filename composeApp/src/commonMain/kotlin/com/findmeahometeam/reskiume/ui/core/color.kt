package com.findmeahometeam.reskiume.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val backgroundColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) backgroundDark else backgroundLight

val backgroundColorForItems: Color
    @Composable
    get() = if (isSystemInDarkTheme()) backgroundDarkForItems else backgroundLightForItems

val textColor: Color
    @Composable
    get() = if(isSystemInDarkTheme()) Color.White else Color.Black

val secondaryTextColor: Color
    @Composable
    get() = if(isSystemInDarkTheme()) lightGray else gray


val backgroundLight: Color = Color(0xFFF6F8F7)

val backgroundLightForItems: Color = Color(0xFFFFFFFF)
val backgroundDark: Color = Color(0xFF112117)
val backgroundDarkForItems: Color = Color(0xFF1E2C2A)
val lightGray: Color = Color(0xFF8EA0BA)
val gray: Color = Color(0xFF4B5563)
val primaryGreen: Color = Color(0xFF18AC53)
val secondaryGreen: Color = Color(0xFF20DF6C)
val tertiaryGreen: Color = Color(0xFFCAF3DB)
val primaryBlue: Color = Color(0xFF3B82F6)
val secondaryBlue: Color = Color(0xFFD0E0F6)
val primaryRed: Color = Color(0xFFEF4444)
val secondaryRed: Color = Color(0xFFF4D4D3)
val primaryPink: Color = Color(0xFFFF2E51)
