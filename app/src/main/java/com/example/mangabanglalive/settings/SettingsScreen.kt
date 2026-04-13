package com.example.mangabanglalive.settings

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mangabanglalive.R
import com.example.mangabanglalive.domain.model.TranslationStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var permissionRefresh by remember { mutableStateOf(0) }
    val overlayGranted = remember(permissionRefresh) { Settings.canDrawOverlays(context) }
    val accessibilityEnabled = remember(permissionRefresh) { isTranslationServiceEnabled(context) }
    val notificationsGranted = remember(permissionRefresh) {
        Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionRefresh += 1
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Settings")
                }
            }, navigationIcon = {
                androidx.compose.material3.TextButton(onClick = onBack) { Text("Back") }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Permission Status", style = MaterialTheme.typography.titleMedium)
                    PermissionRow("Overlay", overlayGranted)
                    PermissionRow("Accessibility", accessibilityEnabled)
                    PermissionRow("Notifications", notificationsGranted)
                    Text(
                        "Live translate works best only after Overlay and Accessibility are enabled.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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
                supportingText = { Text("Use the full API base URL, for example https://openrouter.ai/api/v1") },
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

            uiState.saveStatus?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            uiState.testStatus?.let { Text(it) }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Live Translate", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Live Translate")
                Switch(
                    checked = uiState.liveTranslateEnabled,
                    onCheckedChange = {
                        viewModel.updateLiveTranslateEnabled(it)
                        viewModel.saveSettings()
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Original Text")
                Switch(
                    checked = uiState.showOriginalText,
                    onCheckedChange = {
                        viewModel.updateShowOriginalText(it)
                        viewModel.saveSettings()
                    }
                )
            }

            Text("Caption Limit")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val setLimit: (Int) -> Unit = {
                    viewModel.updateCaptionLimit(it)
                    viewModel.saveSettings()
                }
                if (uiState.captionLimit == 100) {
                    FilledTonalButton(onClick = { setLimit(100) }) { Text("100") }
                } else {
                    OutlinedButton(onClick = { setLimit(100) }) { Text("100") }
                }
                if (uiState.captionLimit == 200) {
                    FilledTonalButton(onClick = { setLimit(200) }) { Text("200") }
                } else {
                    OutlinedButton(onClick = { setLimit(200) }) { Text("200") }
                }
                if (uiState.captionLimit == 500) {
                    FilledTonalButton(onClick = { setLimit(500) }) { Text("500") }
                } else {
                    OutlinedButton(onClick = { setLimit(500) }) { Text("500") }
                }
            }

            OutlinedTextField(
                value = uiState.glossary,
                onValueChange = viewModel::updateGlossary,
                label = { Text("Glossary (one term per line)") },
                supportingText = { Text("Use source=target or fixed-term lines to stabilize translations") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                    permissionRefresh += 1
                }) { Text("Overlay Permission") }

                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= 33) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) { Text("Notification Permission") }
            }

            Button(onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
                permissionRefresh += 1
            }) { Text("Accessibility Settings") }

            FilledTonalButton(onClick = { permissionRefresh += 1 }) {
                Text("Refresh Status")
            }
        }
    }
}

@Composable
private fun PermissionRow(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Text(if (enabled) "Ready" else "Missing", color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
    }
}

private fun isTranslationServiceEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
    val enabledServices = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    return enabledServices.any { info ->
        val serviceInfo = info.resolveInfo.serviceInfo
        serviceInfo.packageName == context.packageName &&
            serviceInfo.name == "com.example.mangabanglalive.accessibility.TranslationAccessibilityService"
    }
}
