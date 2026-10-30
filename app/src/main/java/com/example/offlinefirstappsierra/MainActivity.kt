package com.example.offlinefirstappsierra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.offlinefirstappsierra.ui.screens.HomeScreen
import com.example.offlinefirstappsierra.ui.theme.OfflineFirstAppSierraTheme
import com.example.offlinefirstappsierra.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfflineFirstAppSierraTheme {
                val bookViewModel: BookViewModel = viewModel(
                    factory = BookViewModel.Factory(application)
                )
                HomeScreen(viewModel = bookViewModel)
            }
        }
    }
}
