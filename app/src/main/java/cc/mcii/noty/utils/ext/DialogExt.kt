package cc.mcii.noty.utils.ext

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Fragment.showDialog(
    title: String? = null,
    message: String? = null,
    positiveActionText: String? = null,
    positiveAction: (dialogInterface: DialogInterface, i: Int) -> Unit = { _, _ -> },
    negativeActionText: String? = null,
    negativeAction: (dialogInterface: DialogInterface, i: Int) -> Unit = { _, _ -> },
    isCancelable: Boolean = true
) {
    val dialog: AlertDialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveActionText) { dialogInterface, i ->
                positiveAction(dialogInterface, i)
            }
            .setNegativeButton(negativeActionText) { dialogInterface, i ->
                negativeAction(dialogInterface, i)
            }
            .setCancelable(isCancelable)
            .create()
    dialog.show()
}
