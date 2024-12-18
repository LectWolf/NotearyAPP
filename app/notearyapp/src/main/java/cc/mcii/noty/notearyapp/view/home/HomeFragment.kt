package cc.mcii.noty.notearyapp.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.HomeFragmentBinding
import cc.mcii.noty.notearyapp.view.base.BaseFragment
import cc.mcii.noty.notearyapp.view.hiltNotyMainNavGraphViewModels
import cc.mcii.noty.view.state.HomeState
import cc.mcii.noty.view.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding, HomeState, HomeViewModel>() {

    override val viewModel: HomeViewModel by hiltNotyMainNavGraphViewModels()

    override fun initView() {}

    override fun render(state: HomeState) {

        val destination = R.id.action_homeFragment_to_notesFragment

        findNavController().navigate(destination)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = HomeFragmentBinding.inflate(inflater, container, false)
}
