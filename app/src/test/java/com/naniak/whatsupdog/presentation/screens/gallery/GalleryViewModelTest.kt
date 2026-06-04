package com.naniak.whatsupdog.presentation.screens.gallery

import androidx.lifecycle.SavedStateHandle
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.usecase.GetBreedImagesUseCase
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
class GalleryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val savedStateHandle = mockk<SavedStateHandle>()
    private val getBreedImagesUseCase = mockk<GetBreedImagesUseCase>()
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

    private fun createViewModel(breed: String = "labrador"): GalleryViewModel {
        every { savedStateHandle.get<String>("breed") } returns breed
        return GalleryViewModel(
            savedStateHandle,
            getBreedImagesUseCase,
            toggleFavoriteUseCase,
            isFavoriteUseCase
        )
    }

    @Test
    fun `loadImages success sets Success state with images and breed`() = runTest {
        // Arrange
        val breed = "labrador"
        val images = listOf(
            DogImage(imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg", breed = breed),
            DogImage(imageUrl = "https://images.dog.ceo/breeds/labrador/img2.jpg", breed = breed)
        )
        coEvery { getBreedImagesUseCase(breed) } returns Result.success(images)
        every { isFavoriteUseCase(any()) } returns flowOf(false)

        // Act
        val viewModel = createViewModel(breed)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is GalleryUiState.Success)
            val successState = state as GalleryUiState.Success
            assertEquals(images, successState.images)
            assertEquals(breed, successState.breed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadImages failure sets Error state`() = runTest {
        // Arrange
        val breed = "labrador"
        val exception = RuntimeException("Network error")
        coEvery { getBreedImagesUseCase(breed) } returns Result.failure(exception)

        // Act
        val viewModel = createViewModel(breed)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is GalleryUiState.Error)
            assertEquals("Network error", (state as GalleryUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadImages failure with null message uses default message`() = runTest {
        // Arrange
        val breed = "labrador"
        coEvery { getBreedImagesUseCase(breed) } returns Result.failure(RuntimeException())

        // Act
        val viewModel = createViewModel(breed)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is GalleryUiState.Error)
            assertEquals("Failed to load images for labrador", (state as GalleryUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite calls toggleFavoriteUseCase with given dogImage`() = runTest {
        // Arrange
        val breed = "labrador"
        val images = listOf(
            DogImage(imageUrl = "url1", breed = breed)
        )
        coEvery { getBreedImagesUseCase(breed) } returns Result.success(images)
        every { isFavoriteUseCase(any()) } returns flowOf(false)
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        val viewModel = createViewModel(breed)
        val dogImage = DogImage(imageUrl = "url1", breed = breed)

        // Act
        viewModel.toggleFavorite(dogImage)

        // Assert
        coVerify(exactly = 1) { toggleFavoriteUseCase(dogImage) }
    }

    @Test
    fun `savedStateHandle with empty breed defaults to empty string`() = runTest {
        // Arrange
        every { savedStateHandle.get<String>("breed") } returns null
        coEvery { getBreedImagesUseCase("") } returns Result.success(emptyList())

        // Act
        val viewModel = GalleryViewModel(
            savedStateHandle,
            getBreedImagesUseCase,
            toggleFavoriteUseCase,
            isFavoriteUseCase
        )

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is GalleryUiState.Success)
            assertEquals("", (state as GalleryUiState.Success).breed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favorite statuses are observed for each image after successful load`() = runTest {
        // Arrange
        val breed = "labrador"
        val images = listOf(
            DogImage(imageUrl = "url1", breed = breed),
            DogImage(imageUrl = "url2", breed = breed)
        )
        coEvery { getBreedImagesUseCase(breed) } returns Result.success(images)
        every { isFavoriteUseCase("url1") } returns flowOf(true)
        every { isFavoriteUseCase("url2") } returns flowOf(false)

        // Act
        val viewModel = createViewModel(breed)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is GalleryUiState.Success)
            val successState = state as GalleryUiState.Success
            assertEquals(true, successState.favoriteStatuses["url1"])
            assertEquals(false, successState.favoriteStatuses["url2"])
            cancelAndIgnoreRemainingEvents()
        }
    }
}
