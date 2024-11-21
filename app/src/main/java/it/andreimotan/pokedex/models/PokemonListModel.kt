package it.andreimotan.pokedex.models

data class PokemonListDTO(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem?>
)

data class PokemonListItem(
    val name: String?,
    val url: String?
)

data class PokemonItemUI(
    val name: String,
    val imageUrl: String,
    val types: List<PokemonType>
)
