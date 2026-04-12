package com.example.mangabanglalive.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mangabanglalive.domain.model.TranslationStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {
                androidx.compose.material3.TextButton(onClick = onBack) { Text("Back") }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.apiKey,
                onValueChange = viewModel::updateApiKey,
                label = { Text("OpenRouter API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.model,
                onValueChange = viewModel::updateModel,
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.provider,
                onValueChange = viewModel::updateProvider,
                label = { Text("Provider (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.baseUrl,
                onValueChange = viewModel::updateBaseUrl,
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.updateStyle(TranslationStyle.NATURAL_BANGLA) }
                ) { Text("Natural Bangla") }
                Button(
                    onClick = { viewModel.updateStyle(TranslationStyle.MANGA_BANGLA) }
                ) { Text("Manga Bangla") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = viewModel::saveSettings) { Text("Save") }
                Button(onClick = viewModel::testConnection) { Text("Test Connection") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }) { Text("Overlay Permission") }

                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= 33) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) { Text("Notification Permission") }
            }

            uiState.testStatus?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)
            }
        }
    }
}