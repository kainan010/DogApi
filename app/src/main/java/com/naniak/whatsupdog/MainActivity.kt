package com.naniak.whatsupdog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.naniak.whatsupdog.presentation.navigation.AppNavigation
import com.naniak.whatsupdog.presentation.theme.WhatsUpDogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsUpDogTheme {
                AppNavigation()
            }
        }
    }
}