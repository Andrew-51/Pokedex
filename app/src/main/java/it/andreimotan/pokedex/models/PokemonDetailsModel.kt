package it.andreimotan.pokedex.models

data class PokemonDetails(
    val id: Int,
    val name: String,
    val sprites: PokemonSprite,
    val types: List<PokemonType>
)

data class PokemonSprite(
    val back_default: String?,
    val back_female: String?,
    val back_shiny: String?,
    val back_shiny_female: String?,
    val front_female: String?,
    val front_shiny: String?,
    val front_shiny_female: String?
)

data class PokemonType(
    val slot: Int,
    val type: Type,
)

data class Type(
    val name: String,
    val url: String
)