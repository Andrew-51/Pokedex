@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
package it.andreimotan.pokedex.pages.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import it.andreimotan.pokedex.PokedexTheme
import it.andreimotan.pokedex.models.PokemonItemUI
import it.andreimotan.pokedex.shared.Topbar
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
@ExperimentalMaterialApi
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val pokemonList by viewModel.pokemonUI.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val uiError: String? by viewModel.uiError.collectAsState(initial = null)

    // Stato per la query di ricerca
    val searchQuery = remember { mutableStateOf("") }

    //Focus
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Filtro la lista dei Pokémon in base alla query di ricerca
    val filteredPokemonList = pokemonList.filter { pokemon ->
        pokemon.name.contains(searchQuery.value, ignoreCase = true)
    }

    // Se non ci sono risultati nella ricerca, eseguo getPokemonDetailsForSearch
    LaunchedEffect(searchQuery.value) {
        if (filteredPokemonList.isEmpty() && searchQuery.value.isNotEmpty()) {
            viewModel.getPokemonDetailsForSearch(searchQuery.value)
        }

        // Se la ricerca è stata svuotata, resetto l'offset
        if (searchQuery.value.isEmpty()) {
            viewModel.resetOffset()
        }
    }

    PokedexTheme {
        Topbar(
            title = "PokemonBox",
            viewModel = viewModel,
            clickHandler = null
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Campo di ricerca in alto
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.value.isNotEmpty()) {
                                    viewModel.getPokemonDetailsForSearch(searchQuery.value)
                                } else {
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        singleLine = true,
                        label = { Text("Cerca Pokémon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .focusRequester(focusRequester)
                    )

                    // Verifica se la lista filtrata è vuota
                    if (filteredPokemonList.isEmpty() && searchQuery.value.isNotEmpty()) {
                        // Mostra il messaggio se la lista è vuota e la query non è vuota
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(
                                text = uiError ?: "Nessun pokemon trovato",
                                style = MaterialTheme.typography.h6,
                                color = Color.Black
                            )
                        }
                    } else {
                        // Lista di Pokémon filtrata (nome e immagine)
                        LazyColumn {
                            items(filteredPokemonList) { item ->
                                PokemonItem(item)
                            }
                            item {
                                LaunchedEffect(true) {
                                    viewModel.loadNewPokemons()
                                }
                            }
                        }
                    }
                }

                // Mostra il loader sopra la UI solo se è in corso il caricamento
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonItem(pokemon: PokemonItemUI) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mostra l'immagine se presente
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = "${pokemon.name} image",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            // Nome del Pokémon
            Text(
                text = pokemon.name.replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Tipi del Pokémon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                pokemon.types.forEach { type ->
                    AssistChip(
                        onClick = { Log.d("Chip", "Clicked on $type") },
                        label = { Text(type.type.name.replaceFirstChar { it.titlecase() }) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Divider(
            thickness = 1.dp,
            color = Color.Gray,
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp * 0.90f).dp)
        )
    }
}


