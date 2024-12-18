package cc.mcii.noty.notearyapp.view.about

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.mcii.noty.notearyapp.BuildConfig
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.FragmentAboutBinding
import cc.mcii.noty.notearyapp.view.base.BaseFragment
import cc.mcii.noty.notearyapp.view.hiltNotyMainNavGraphViewModels
import cc.mcii.noty.view.state.AboutState
import cc.mcii.noty.view.viewmodel.AboutViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding, AboutState, AboutViewModel>() {
    override val viewModel: AboutViewModel by hiltNotyMainNavGraphViewModels()

    override fun initView() {
        binding.run {
            textAppVersion.text = getString(
                R.string.text_app_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )

            repoCardView.setOnClickListener {
                launchBrowser(URL_REPO)
            }
        }
    }

    override fun render(state: AboutState) {}

    private fun launchBrowser(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
        startActivity(it)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAboutBinding.inflate(inflater, container, false)

    companion object {
        const val URL_REPO = "https://github.com/LectWolf/NotearyAPP"
    }
}
