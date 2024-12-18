package cc.mcii.noty.view.viewmodel

import cc.mcii.noty.view.state.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : BaseViewModel<HomeState>() {
    override val state: StateFlow<HomeState> = MutableStateFlow(HomeState).asStateFlow()
}
