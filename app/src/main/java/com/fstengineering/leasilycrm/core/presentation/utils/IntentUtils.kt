package com.fstengineering.leasilycrm.core.presentation.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.R

fun Context.openFileWithChooser(
    uriString: String?,
    onActivityNotFound: () -> Unit,
) {
    val fileUri = uriString?.toUri() ?: return

    val mimeType = contentResolver.getType(fileUri)
        ?: run {
            val ext = fileUri.toString().substringAfterLast('.', "")
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
        ?: "*/*"

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        clipData = ClipData.newUri(contentResolver, "file", fileUri)
    }

    try {
        val chooser = Intent.createChooser(
            /* target = */ intent,
            /* title = */ getString(R.string.chooser_open_file_title)
        )
        startActivity(chooser)
    } catch (_: ActivityNotFoundException) {
        onActivityNotFound()
    }
}

fun Context.openPlayStoreApp() {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_APP_MARKET)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val webUri = "https://play.google.com/store".toUri()
        val webBrowser = Intent(Intent.ACTION_VIEW, webUri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(webBrowser)
    }
}
