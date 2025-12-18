package com.chaima.truekeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chaima.truekeo.navigation.AppNavigation
import com.chaima.truekeo.ui.theme.TruekeoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TruekeoTheme {
                AppNavigation()
            }
        }
    }
}