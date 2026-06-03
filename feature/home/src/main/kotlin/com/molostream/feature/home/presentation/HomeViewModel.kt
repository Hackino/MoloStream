package com.molostream.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molostream.feature.home.domain.GetCatalogUseCase
import com.molostream.feature.home.domain.ObserveContinueWatchingUseCase
import com.molostream.core.domain.usecase.ClearContinueWatchingUseCase
import com.molostream.core.domain.usecase.ObserveSubscriptionUseCase
import com.molostream.core.domain.usecase.SetSubscriptionUseCase
import com.molostream.core.model.Orientation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCatalog: GetCatalogUseCase,
    private val observeSubscription: ObserveSubscriptionUseCase,
    private val setSubscription: SetSubscriptionUseCase,
    private val observeContinueWatching: ObserveContinueWatchingUseCase,
    private val clearContinueWatching: ClearContinueWatchingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCatalog()
        observeSubscriptionState()
        observeContinueWatchingState()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            val catalog = getCatalog()
            val vertical = catalog.filter { it.orientation == Orientation.VERTICAL }
            val horizontal = catalog.filter { it.orientation == Orientation.HORIZONTAL && !it.isLive }
            _uiState.update {
                it.copy(
                    loading = false,
                    featured = vertical.firstOrNull() ?: catalog.firstOrNull(),
                    verticalRail = vertical,
                    horizontalRail = horizontal,
                )
            }
        }
    }

    private fun observeSubscriptionState() {
        viewModelScope.launch {
            observeSubscription().collect { subscribed ->
                _uiState.update { it.copy(subscribed = subscribed) }
            }
        }
    }

    private fun observeContinueWatchingState() {
        viewModelScope.launch {
            observeContinueWatching().collect { items ->
                _uiState.update { it.copy(continueWatching = items) }
            }
        }
    }

    fun setSubscribed(value: Boolean) {
        viewModelScope.launch {
            setSubscription(value)
            if (!value) clearContinueWatching()
        }
    }
}
