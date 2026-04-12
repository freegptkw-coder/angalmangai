package com.example.mangabanglalive.reader.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView

@Composable
fun PdfViewer(uri: Uri, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PDFView(context, null)
        },
        update = { view ->
            view.fromUri(uri)
                .enableSwipe(true)
                .spacing(8)
                .load()
        }
    )
}