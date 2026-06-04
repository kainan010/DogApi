package com.naniak.whatsupdog.presentation.screens.favorites

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.domain.usecase.GetFavoritesUseCase
import com.naniak.whatsupdog.domain.usecase.ToggleFavoriteUseCase
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getFavoritesUseCase = mockk<GetFavoritesUseCase>()
    private val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): FavoritesViewModel {
        return FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)
    }

    @Test
    fun `observeFavorites sets Success state when favorites exist`() = runTest {
        // Arrange
        val favorites = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L),
            FavoriteDog(id = 2, imageUrl = "url2", breed = "poodle", savedAt = 2000L)
        )
        every { getFavoritesUseCase() } returns flowOf(favorites)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is FavoritesUiState.Success)
            assertEquals(favorites, (state as FavoritesUiState.Success).favorites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeFavorites sets Empty state when no favorites`() = runTest {
        // Arrange
        every { getFavoritesUseCase() } returns flowOf(emptyList())

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is FavoritesUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeFavorite calls toggleFavoriteUseCase with DogImage from FavoriteDog`() = runTest {
        // Arrange
        val favorites = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L)
        )
        every { getFavoritesUseCase() } returns flowOf(favorites)
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        val viewModel = createViewModel()

        val favoriteDog = FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L)

        // Act
        viewModel.removeFavorite(favoriteDog)

        // Assert
        val expectedDogImage = DogImage(imageUrl = "url1", breed = "labrador")
        coVerify(exactly = 1) { toggleFavoriteUseCase(expectedDogImage) }
    }

    @Test
    fun `removeFavorite passes null breed correctly`() = runTest {
        // Arrange
        val favorites = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = null, savedAt = 1000L)
        )
        every { getFavoritesUseCase() } returns flowOf(favorites)
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        val viewModel = createViewModel()

        val favoriteDog = FavoriteDog(id = 1, imageUrl = "url1", breed = null, savedAt = 1000L)

        // Act
        viewModel.removeFavorite(favoriteDog)

        // Assert
        val expectedDogImage = DogImage(imageUrl = "url1", breed = null)
        coVerify(exactly = 1) { toggleFavoriteUseCase(expectedDogImage) }
    }

    @Test
    fun `state updates when favorites flow emits new values`() = runTest {
        // Arrange
        val favorites = listOf(
            FavoriteDog(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L),
            FavoriteDog(id = 2, imageUrl = "url2", breed = "poodle", savedAt = 2000L)
        )
        every { getFavoritesUseCase() } returns flowOf(favorites)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is FavoritesUiState.Success)
            assertEquals(2, (state as FavoritesUiState.Success).favorites.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
