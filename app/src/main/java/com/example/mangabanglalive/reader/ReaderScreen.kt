package com.example.mangabanglalive.reader

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mangabanglalive.domain.model.TranslationSegment
import com.example.mangabanglalive.overlay.TranslationOverlay
import com.example.mangabanglalive.reader.components.PdfViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    projectId: String,
    viewModel: ReaderViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var editingSegment by remember { mutableStateOf<TranslationSegment?>(null) }
    var editedText by remember { mutableStateOf("") }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val page = uiState.pages.getOrNull(uiState.currentPageIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.title ?: "Reader") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(androidx.compose.material.icons.Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleOverlay() }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Layers, null)
                    }
                    IconButton(onClick = { viewModel.runOcrAndTranslate(context) }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Translate, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    page?.imageUri != null -> {
                        AsyncImage(
                            model = Uri.parse(page.imageUri),
                            contentDescription = "Manga page",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    uiState.project?.sourceType?.name == "PDF" && uiState.project?.sourceUri != null -> {
                        PdfViewer(uri = Uri.parse(uiState.project?.sourceUri ?: ""))
                    }
                }

                if (uiState.overlayEnabled) {
                    TranslationOverlay(
                        segments = uiState.translations,
                        onEditSegment = { segment ->
                            editingSegment = segment
                            editedText = segment.translatedText
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { viewModel.previousPage() }) { Text("Prev") }
                Text("Page ${uiState.currentPageIndex + 1} / ${uiState.pages.size}")
                Button(onClick = { viewModel.nextPage() }) { Text("Next") }
            }
        }
    }

    if (editingSegment != null) {
        AlertDialog(
            onDismissRequest = { editingSegment = null },
            title = { Text("Edit Translation") },
            text = {
                TextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editingSegment?.let { viewModel.updateTranslation(it.id, editedText) }
                    editingSegment = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingSegment = null }) { Text("Cancel") }
            }
        )
    }
}