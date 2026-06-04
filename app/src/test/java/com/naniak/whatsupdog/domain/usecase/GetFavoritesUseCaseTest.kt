package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetFavoritesUseCaseTest {

    private val repository = mockk<FavoriteRepository>()
    private val useCase = GetFavoritesUseCase(repository)

    @Test
    fun `invoke returns Flow of favorites from repository`() = runTest {
        // Arrange
        val favorites = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L),
            FavoriteDog(id = 2, imageUrl = "url2", breed = "poodle", savedAt = 2000L)
        )
        every { repository.getAllFavorites() } returns flowOf(favorites)

        // Act & Assert
        useCase().test {
            assertEquals(favorites, awaitItem())
            awaitComplete()
        }
        verify(exactly = 1) { repository.getAllFavorites() }
    }

    @Test
    fun `invoke returns empty list when no favorites exist`() = runTest {
        // Arrange
        every { repository.getAllFavorites() } returns flowOf(emptyList())

        // Act & Assert
        useCase().test {
            assertEquals(emptyList<FavoriteDog>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits multiple updates from repository`() = runTest {
        // Arrange
        val firstEmission = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L)
        )
        val secondEmission = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L),
            FavoriteDog(id = 2, imageUrl = "url2", breed = "corgi", savedAt = 2000L)
        )
        every { repository.getAllFavorites() } returns kotlinx.coroutines.flow.flow {
            emit(firstEmission)
            emit(secondEmission)
        }

        // Act & Assert
        useCase().test {
            assertEquals(firstEmission, awaitItem())
            assertEquals(secondEmission, awaitItem())
            awaitComplete()
        }
    }
}
