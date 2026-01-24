package com.example.fakeolx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.fakeolx.navigation.NavGraph
import com.example.fakeolx.ui.theme.FakeOLXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FakeOLXTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}