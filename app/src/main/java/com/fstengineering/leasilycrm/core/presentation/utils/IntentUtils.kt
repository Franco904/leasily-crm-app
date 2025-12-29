package com.fstengineering.leasilycrm.core.presentation.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent


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

fun Context.openAddGoogleAccountSettingsApp(onNoSettingsClientsInstalled: (UiEvent) -> Unit) {
    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val uiEvent = UiEvent.ShowSnackBar(R.string.no_app_to_open_intent_error_message)
        onNoSettingsClientsInstalled(uiEvent)
    }
}

fun Context.mailToSupportChannel(onNoEmailClientsInstalled: (UiEvent) -> Unit) {
    val recipient = "fst.engineering3@gmail.com"
    val subject = "Problema inesperado"
    val body = "Olá, estou tendo um problema inesperado na tela X ao realizar a ação Y"

    val mailtoUri = "mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()

    val intent = Intent(Intent.ACTION_SENDTO, mailtoUri)
    val chooser = Intent.createChooser(
        /* target = */ intent,
        /* title = */ getString(R.string.chooser_open_file_title),
    )

    try {
        startActivity(chooser)
    } catch (_: ActivityNotFoundException) {
        val uiEvent = UiEvent.ShowSnackBar(R.string.no_app_to_open_intent_error_message)
        onNoEmailClientsInstalled(uiEvent)
    }
}
