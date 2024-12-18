package cc.mcii.noty.notearyapp.view.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import cc.mcii.noty.notearyapp.databinding.AddNoteFragmentBinding
import cc.mcii.noty.notearyapp.view.base.BaseFragment
import cc.mcii.noty.notearyapp.view.hiltNotyMainNavGraphViewModels
import cc.mcii.noty.utils.ext.hideKeyboard
import cc.mcii.noty.utils.ext.toStringOrEmpty
import cc.mcii.noty.view.state.AddNoteState
import cc.mcii.noty.view.viewmodel.AddNoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : BaseFragment<AddNoteFragmentBinding, AddNoteState, AddNoteViewModel>() {

    override val viewModel: AddNoteViewModel by hiltNotyMainNavGraphViewModels()

    override fun initView() {
        binding.run {
            fabSave.setOnClickListener { viewModel.add() }
            noteLayout.run {
                fieldTitle.addTextChangedListener { viewModel.setTitle(it.toStringOrEmpty()) }
                fieldNote.addTextChangedListener { viewModel.setNote(it.toStringOrEmpty()) }
            }
        }
    }

    override fun render(state: AddNoteState) {
        binding.fabSave.isVisible = state.showSave

        showProgressDialog(state.isAdding)

        if (state.added) {
            hideKeyboard()
            findNavController().navigateUp()
        }

        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            showErrorDialog("Failed to add a note", errorMessage)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AddNoteFragmentBinding.inflate(inflater, container, false)


    override fun onDestroyView() {
//        if (viewModel.state.value.showSave && !viewModel.state.value.added){
//            viewModel.add()
//        }
        viewModel.resetState()
        super.onDestroyView()
    }
}
