package cc.mcii.noty.notearyapp.view.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cc.mcii.noty.notearyapp.databinding.LoadingDialogLayoutBinding
import cc.mcii.noty.utils.autoCleaned

class ProgressDialog : DialogFragment() {

    private var binding: LoadingDialogLayoutBinding by autoCleaned()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoadingDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
    }
}
