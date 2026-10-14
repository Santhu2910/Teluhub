package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ApiKeyDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.TeluTubeTheme
import com.example.ui.viewmodel.DubViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedText = extractSharedUrl(intent)

        setContent {
            TeluTubeTheme {
                val viewModel: DubViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                if (sharedText != null && savedInstanceState == null) {
                    viewModel.onUrlChanged(sharedText)
                    viewModel.translateVideo(sharedText)
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel = viewModel)

                    ApiKeyDialog(
                        isOpen = uiState.showApiKeyDialog,
                        onDismiss = { viewModel.openApiKeyDialog(false) },
                        onSaveKey = { viewModel.saveCustomApiKey(it) }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun extractSharedUrl(intent: Intent?): String? {
        if (intent == null) return null
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!text.isNullOrBlank()) {
                return text.trim()
            }
        }
        return null
    }
}
