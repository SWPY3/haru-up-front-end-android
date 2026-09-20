package com.swyp.haruup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.presentation.navigation.HaruUpNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaruUpTheme {
                HaruUpNavHost()
            }
        }
    }
}
