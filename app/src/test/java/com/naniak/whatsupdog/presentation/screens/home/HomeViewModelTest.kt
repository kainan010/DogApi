package com.naniak.whatsupdog.presentation.screens.home

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.usecase.GetRandomDogUseCase
import com.naniak.whatsupdog.domain.usecase.IsFavoriteUseCase
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
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getRandomDogUseCase = mockk<GetRandomDogUseCase>()
    private val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()
    private val isFavoriteUseCase = mockk<IsFavoriteUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(getRandomDogUseCase, toggleFavoriteUseCase, isFavoriteUseCase)
    }

    @Test
    fun `loadRandomDog success sets Success state with DogImage`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg",
            breed = "labrador"
        )
        coEvery { getRandomDogUseCase() } returns Result.success(dogImage)
        every { isFavoriteUseCase(any()) } returns flowOf(false)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(dogImage, (state as HomeUiState.Success).dogImage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadRandomDog failure sets Error state`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { getRandomDogUseCase() } returns Result.failure(exception)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals("Network error", (state as HomeUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadRandomDog failure with null message uses default message`() = runTest {
        // Arrange
        val exception = RuntimeException()
        coEvery { getRandomDogUseCase() } returns Result.failure(exception)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals("Failed to load a random dog", (state as HomeUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite calls toggleFavoriteUseCase with current dogImage`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/poodle/img1.jpg",
            breed = "poodle"
        )
        coEvery { getRandomDogUseCase() } returns Result.success(dogImage)
        every { isFavoriteUseCase(any()) } returns flowOf(false)
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        val viewModel = createViewModel()

        // Act
        viewModel.toggleFavorite()

        // Assert
        coVerify(exactly = 1) { toggleFavoriteUseCase(dogImage) }
    }

    @Test
    fun `toggleFavorite does nothing when state is not Success`() = runTest {
        // Arrange
        coEvery { getRandomDogUseCase() } returns Result.failure(RuntimeException("error"))

        val viewModel = createViewModel()

        // Act
        viewModel.toggleFavorite()

        // Assert
        coVerify(exactly = 0) { toggleFavoriteUseCase(any()) }
    }

    @Test
    fun `isFavorite status is observed after successful load`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/husky/img1.jpg",
            breed = "husky"
        )
        coEvery { getRandomDogUseCase() } returns Result.success(dogImage)
        every { isFavoriteUseCase(dogImage.imageUrl) } returns flowOf(true)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertTrue((state as HomeUiState.Success).isFavorite)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
