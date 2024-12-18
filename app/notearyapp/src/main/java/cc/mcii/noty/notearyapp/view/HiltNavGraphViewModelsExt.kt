package cc.mcii.noty.notearyapp.view

import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModel
import cc.mcii.noty.notearyapp.R

inline fun <reified T : ViewModel> Fragment.hiltNotyMainNavGraphViewModels() =
    hiltNavGraphViewModels<T>(R.id.nav_graph)
