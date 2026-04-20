package com.openclassrooms.eventorias.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.eventorias.data.model.Event
import com.openclassrooms.eventorias.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Les 3 états possibles de l'écran
sealed class EventListUiState {
    object Loading : EventListUiState()
    data class Success(val events: List<Event>) : EventListUiState()
    data class Error(val message: String) : EventListUiState()
}

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _uiState = MutableStateFlow<EventListUiState>(EventListUiState.Loading)
    val uiState: StateFlow<EventListUiState> = _uiState

    // Liste complète depuis Firestore
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            // Combine les événements et la recherche pour filtrer en temps réel
            combine(
                repository.getEvents(),
                _searchQuery
            ) { events, query ->
                if (query.isBlank()) events
                else events.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true)
                }
            }
                .catch { e ->
                    _uiState.value = EventListUiState.Error(e.message ?: "Erreur inconnue")
                }
                .collect { filtered ->
                    _uiState.value = EventListUiState.Success(filtered)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun retry() {
        _uiState.value = EventListUiState.Loading
        loadEvents()
    }
}