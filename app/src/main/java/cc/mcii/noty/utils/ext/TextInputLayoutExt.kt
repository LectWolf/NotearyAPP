package cc.mcii.noty.utils.ext

import com.google.android.material.textfield.TextInputLayout

/**
 * Sets the [message] as error if [isError] is true. If it's false, error is cleared
 */
inline fun TextInputLayout.setError(isError: Boolean, message: () -> String) {
    error = if (isError) message() else null
}
