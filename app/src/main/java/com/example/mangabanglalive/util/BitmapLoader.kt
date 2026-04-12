package com.example.mangabanglalive.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build

object BitmapLoader {
    fun load(context: Context, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
    }
}