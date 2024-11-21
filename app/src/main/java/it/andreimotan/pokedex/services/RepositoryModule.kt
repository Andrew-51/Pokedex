package it.andreimotan.pokedex.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePokemonRepository(apiService: PokeApiService): PokemonRepository {
        return PokemonRepository(apiService)
    }
}
