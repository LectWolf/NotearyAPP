package cc.mcii.noty.notearyapp.view.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.mcii.noty.core.model.Note
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.NotesFragmentBinding
import cc.mcii.noty.notearyapp.view.base.BaseFragment
import cc.mcii.noty.notearyapp.view.hiltNotyMainNavGraphViewModels
import cc.mcii.noty.notearyapp.view.notes.adapter.NotesListAdapter
import cc.mcii.noty.utils.autoCleaned
import cc.mcii.noty.utils.ext.hide
import cc.mcii.noty.utils.ext.show
import cc.mcii.noty.view.state.NotesState
import cc.mcii.noty.view.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : BaseFragment<NotesFragmentBinding, NotesState, NotesViewModel>() {

    override val viewModel: NotesViewModel by hiltNotyMainNavGraphViewModels()

    private val notesListAdapter by autoCleaned(initializer = { NotesListAdapter(::onNoteClicked) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
    }

    override fun initView() {
        binding.run {
            notesRecyclerView.adapter = notesListAdapter
            fabNew.setOnClickListener {
                findNavController().navigate(R.id.action_notesFragment_to_addNoteFragment)
            }
            swipeRefreshNotes.apply {
                setColorSchemeColors(
                    ContextCompat.getColor(requireContext(), R.color.secondaryColor),
                    ContextCompat.getColor(requireContext(), R.color.onSecondary)
                )
                setOnRefreshListener { viewModel.syncNotes() }
            }
        }
    }

    override fun render(state: NotesState) {
        binding.swipeRefreshNotes.isRefreshing = state.isLoading
        binding.swipeRefreshNotes.isEnabled = true

        val errorMessage = state.error
        if (errorMessage != null) {
            toast("Error: $errorMessage")
        }

        val notes = state.notes
        if (notes.isNotEmpty()) {
            onNotesLoaded(notes)
        }
    }

    private fun onNotesLoaded(data: List<Note>) {
        binding.emptyStateLayout.run {
            if (data.isEmpty()) show() else hide()
        }
        notesListAdapter.submitList(data)
    }

    private fun onNoteClicked(note: Note) {
        findNavController().navigate(
            NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(note.id)
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = NotesFragmentBinding.inflate(inflater, container, false)


    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {

                override fun onPrepareMenu(menu: Menu) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        when (viewModel.isDarkModeEnabled()) {
                            true -> {
                                menu.findItem(R.id.action_dark_mode).isVisible = false
                            }

                            false -> {
                                menu.findItem(R.id.action_light_mode).isVisible = false
                            }
                        }
                        super.onPrepareMenu(menu)
                    }
                }

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_light_mode -> viewModel.setDarkMode(false)
                        R.id.action_dark_mode -> viewModel.setDarkMode(true)
                        R.id.action_settings ->
                            findNavController().navigate(R.id.action_notesFragment_to_aboutFragment)
                    }
                    return false
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    companion object {
        const val ANIMATION_DURATION = 2000L
    }
}
