package cc.mcii.noty.notearyapp.view.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.NoteDetailFragmentBinding
import cc.mcii.noty.notearyapp.view.base.BaseFragment
import cc.mcii.noty.utils.ext.hideKeyboard
import cc.mcii.noty.utils.ext.showDialog
import cc.mcii.noty.utils.ext.toStringOrEmpty
import cc.mcii.noty.utils.saveBitmap
import cc.mcii.noty.utils.share.shareImage
import cc.mcii.noty.utils.share.shareNoteText
import cc.mcii.noty.view.state.NoteDetailState
import cc.mcii.noty.view.viewmodel.NoteDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class NoteDetailFragment :
    BaseFragment<NoteDetailFragmentBinding, NoteDetailState, NoteDetailViewModel>() {

    private val args: NoteDetailFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelAssistedFactory: NoteDetailViewModel.Factory

    private var isNoteLoaded = false

    private var pinMenuItem: MenuItem? = null

    override val viewModel: NoteDetailViewModel by viewModels {
        args.noteId?.let { noteId ->
            NoteDetailViewModel.provideFactory(viewModelAssistedFactory, noteId)
        } ?: throw IllegalStateException("'noteId' shouldn't be null")
    }

    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shareImage()
        } else {
            showErrorDialog(
                title = getString(R.string.dialog_title_failed_image_share),
                message = getString(R.string.dialog_message_failed_image_share)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
    }

    override fun initView() {
        binding.run {
            fabSave.setOnClickListener { viewModel.save() }
            noteLayout.run {
                fieldTitle.addTextChangedListener { viewModel.setTitle(it.toStringOrEmpty()) }
                fieldNote.addTextChangedListener { viewModel.setNote(it.toStringOrEmpty()) }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }
    }

    override fun render(state: NoteDetailState) {
        binding.fabSave.isVisible = state.showSave

        val title = state.title
        val note = state.note
        val time = state.time
        val wordCount = note?.length
        val info =
            time?.let { "${formatNoteTime(it)} | ${wordCount}${getString(R.string.fmt_words)}" }
                ?: ""

        if (title != null && note != null && !isNoteLoaded) {
            isNoteLoaded = true
            binding.noteLayout.fieldTitle.setText(title)
            binding.noteLayout.fieldNote.setText(note)
            binding.noteLayout.fieldInfo.text = info
        }

        if (state.finished) {
            hideKeyboard()
            findNavController().navigateUp()
        }

        val errorMessage = state.error
        if (errorMessage != null) {
            toast("Error: $errorMessage")
        }

        if (!state.isLoading) {
            updatePinnedIcon(state.isPinned)
        }

    }

    private fun formatNoteTime(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

        val isLastYearOrEarlier = Year.now().value - localDateTime.year > 0

        return when {
            isLastYearOrEarlier -> {
                DateTimeFormatter.ofPattern(getString(R.string.fmt_year_date_current))
                    .format(localDateTime)
            }

            else -> {
                DateTimeFormatter.ofPattern(getString(R.string.fmt_date_current))
                    .format(localDateTime)
            }
        }
    }


    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onPrepareMenu(menu: Menu) {
                    pinMenuItem = menu.findItem(R.id.action_pin)

                    super.onPrepareMenu(menu)
                }

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.note_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_delete -> confirmNoteDeletion()
                        R.id.action_pin -> viewModel.togglePin()
                        R.id.action_share_text -> shareText()
                        R.id.action_share_image -> shareImage()
                    }
                    return false
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun updatePinnedIcon(isPinned: Boolean) {
        Log.d("调试", "更新状态:${isPinned}")
        pinMenuItem?.run {
            val icon = if (isPinned) R.drawable.ic_pinned else R.drawable.ic_unpinned
            setIcon(icon)
        }
    }

    private fun shareText() {
        val title = binding.noteLayout.fieldTitle.text.toString()
        val note = binding.noteLayout.fieldNote.text.toString()

        requireContext().shareNoteText(title, note)
    }

    private fun shareImage() {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        val imageUri = binding.noteLayout.noteContentLayout.drawToBitmap().let { bitmap ->
            saveBitmap(requireActivity(), bitmap)
        } ?: run {
            toast("Error occurred!")
            return
        }

        requireContext().shareImage(imageUri)
    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = NoteDetailFragmentBinding.inflate(inflater, container, false)

    private fun confirmNoteDeletion() {
        getString(R.string.text_negative)
        showDialog(
            title = getString(R.string.text_delete),
            message = getString(R.string.text_delete_tip),
            positiveActionText = getString(R.string.text_positive),
            positiveAction = { _, _ ->
                viewModel.delete()
            },
            negativeActionText = getString(R.string.text_negative),
            negativeAction = { dialog, _ ->
                dialog.dismiss()
            }
        )
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.state.value.showSave) {
                viewModel.save()
            }
            findNavController().navigateUp()
        }
    }
}
