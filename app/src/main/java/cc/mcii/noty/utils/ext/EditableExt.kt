package cc.mcii.noty.utils.ext

import android.text.Editable

fun Editable?.toStringOrEmpty(): String = this?.toString() ?: ""
