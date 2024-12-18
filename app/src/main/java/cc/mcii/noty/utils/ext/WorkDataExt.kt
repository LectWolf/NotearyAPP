package cc.mcii.noty.utils.ext

import androidx.work.Data

fun <T> Data.Builder.putEnum(key: String, value: T) = apply { putString(key, value.toString()) }

inline fun <reified T : Enum<T>> Data.getEnum(key: String): T? {
    val enumValue = getString(key)
    return runCatching { enumValueOf<T>(enumValue!!) }.getOrNull()
}
