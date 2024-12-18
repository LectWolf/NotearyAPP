package cc.mcii.noty.notearyapp.view.custom

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import cc.mcii.noty.notearyapp.databinding.ErrorDialogLayoutBinding
import cc.mcii.noty.utils.autoCleaned

class ErrorDialog(
    var title: String = "",
    var message: String = "",
    var onDialogDismiss: () -> Unit = {}
) : DialogFragment() {

    private var binding: ErrorDialogLayoutBinding by autoCleaned()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ErrorDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            dialogTitle.text = title
            dialogMessage.text = message
            dialogButtonOk.setOnClickListener {
                onDialogDismiss()
                dialog?.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        onDialogDismiss()
        super.onCancel(dialog)
    }
}
