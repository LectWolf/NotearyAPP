package cc.mcii.noty.view.viewmodel

import cc.mcii.noty.view.state.AboutState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AboutViewModel : BaseViewModel<AboutState>() {
    override val state: StateFlow<AboutState> = MutableStateFlow(AboutState).asStateFlow()
}
