package com.example.semafix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.semafix.navigation.AppNavHost
import com.example.semafix.ui.theme.SemaFixTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemaFixTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}

