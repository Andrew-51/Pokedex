package it.andreimotan.pokedex.pages.home

import android.annotation.SuppressLint
import android.content.Context
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.andreimotan.pokedex.models.PokemonItemUI
import it.andreimotan.pokedex.models.PokemonListDTO
import it.andreimotan.pokedex.models.PokemonListItem
import it.andreimotan.pokedex.services.PokemonRepository
import it.andreimotan.pokedex.shared.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel() {
    private val _pokemonUI = MutableStateFlow<List<PokemonItemUI>>(emptyList())
    val pokemonUI: StateFlow<List<PokemonItemUI>> = _pokemonUI

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError

    private var currentOffset = 0
    private val itemsPerPage = 20

    init {
        loadPokemon()
    }

    // Funzione per caricare la lista di Pokémon
    private fun loadPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getPokemonList(offset = currentOffset, limit = itemsPerPage)
                val enrichedList = response.results.mapNotNull { baseItem ->
                    baseItem?.let {
                        async {
                            try {
                                val details = repository.getPokemonDetails(it.name.toString())
                                PokemonItemUI(
                                    name = it.name ?: "Unknown",
                                    imageUrl = (details.sprites.front_shiny ?: details.sprites.front_shiny_female).toString(),
                                    types = details.types
                                )
                            } catch (e: Exception) {
                                Log.d("HomeViewModel", "Error fetching details for ${it.name}: $e")
                                null
                            }
                        }
                    }
                }.awaitAll().filterNotNull()

                // Aggiungo i Pokémon alla lista senza duplicare i nomi
                _pokemonUI.value = (_pokemonUI.value + enrichedList).distinctBy { it.name }
                currentOffset += itemsPerPage
            } catch (e: Exception) {
                _uiError.value = e.localizedMessage ?: "Si è verificato un errore."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Metodo per caricare un singolo Pokémon quando non trovato dalla ricerca
    fun getPokemonDetailsForSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val details = repository.getPokemonDetails(query)
                val pokemon = PokemonItemUI(
                    name = details.name ?: "Sconosciuto",
                    imageUrl = (details.sprites.front_shiny ?: details.sprites.front_shiny_female).toString(),
                    types = details.types
                )
                // Se il Pokémon esiste, aggiorna la lista
                _pokemonUI.value = listOf(pokemon)
            } catch (e: Exception) {
                _uiError.value = "Pokémon non trovato o errore nella ricerca."
                _pokemonUI.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carica nuovi Pokémon
    fun loadNewPokemons() {
        loadPokemon()
    }

    // Resetta l'offset e la lista quando la ricerca viene svuotata
    fun resetOffset() {
        currentOffset = 0
        _pokemonUI.value = emptyList()
        loadPokemon()
    }
}

