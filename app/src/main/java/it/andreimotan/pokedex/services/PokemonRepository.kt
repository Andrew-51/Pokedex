package it.andreimotan.pokedex.services
import it.andreimotan.pokedex.models.PokemonDetails
import it.andreimotan.pokedex.models.PokemonListDTO
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val apiService: PokeApiService) {
    suspend fun getPokemonList(offset: Int, limit: Int): PokemonListDTO {
        return apiService.getPokemonList(offset = offset, limit = limit)
    }
    suspend fun getPokemonDetails(name: String): PokemonDetails {
        return apiService.getPokemonDetails(name = name)
    }
}
