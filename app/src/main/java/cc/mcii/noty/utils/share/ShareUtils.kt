package cc.mcii.noty.utils.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import cc.mcii.noty.R

fun Context.shareNoteText(title: String, note: String) {
    val shareMsg = getString(R.string.text_message_share, title, note)

    val intent = ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setText(shareMsg)
        .intent

    startActivity(Intent.createChooser(intent, null))
}

fun Context.shareImage(imageUri: Uri) {
    val intent = ShareCompat.IntentBuilder(this)
        .setType("image/jpeg")
        .setStream(imageUri)
        .intent

    startActivity(Intent.createChooser(intent, null))
}
