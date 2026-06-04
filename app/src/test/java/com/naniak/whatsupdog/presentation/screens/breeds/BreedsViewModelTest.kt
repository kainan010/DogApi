package com.naniak.whatsupdog.presentation.screens.breeds

import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.usecase.GetAllBreedsUseCase
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BreedsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getAllBreedsUseCase = mockk<GetAllBreedsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): BreedsViewModel {
        return BreedsViewModel(getAllBreedsUseCase)
    }

    @Test
    fun `loadBreeds success sets Success state with breeds`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita", subBreeds = emptyList()),
            Breed(name = "bulldog", subBreeds = listOf("boston", "english", "french")),
            Breed(name = "corgi", subBreeds = listOf("cardigan"))
        )
        coEvery { getAllBreedsUseCase() } returns Result.success(breeds)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Success)
            assertEquals(breeds, (state as BreedsUiState.Success).breeds)
            assertEquals("", state.query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBreeds failure sets Error state`() = runTest {
        // Arrange
        val exception = RuntimeException("API error")
        coEvery { getAllBreedsUseCase() } returns Result.failure(exception)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Error)
            assertEquals("API error", (state as BreedsUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBreeds failure with null message uses default message`() = runTest {
        // Arrange
        coEvery { getAllBreedsUseCase() } returns Result.failure(RuntimeException())

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Error)
            assertEquals("Failed to load breeds", (state as BreedsUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged filters breeds by name`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita"),
            Breed(name = "bulldog", subBreeds = listOf("boston", "english")),
            Breed(name = "beagle")
        )
        coEvery { getAllBreedsUseCase() } returns Result.success(breeds)
        val viewModel = createViewModel()

        // Act
        viewModel.onSearchQueryChanged("b")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Success)
            val successState = state as BreedsUiState.Success
            assertEquals(2, successState.breeds.size)
            assertEquals("bulldog", successState.breeds[0].name)
            assertEquals("beagle", successState.breeds[1].name)
            assertEquals("b", successState.query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged filters breeds by sub-breed name`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita"),
            Breed(name = "bulldog", subBreeds = listOf("boston", "english")),
            Breed(name = "corgi", subBreeds = listOf("cardigan"))
        )
        coEvery { getAllBreedsUseCase() } returns Result.success(breeds)
        val viewModel = createViewModel()

        // Act
        viewModel.onSearchQueryChanged("boston")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Success)
            val successState = state as BreedsUiState.Success
            assertEquals(1, successState.breeds.size)
            assertEquals("bulldog", successState.breeds[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged with blank query returns all breeds`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita"),
            Breed(name = "bulldog"),
            Breed(name = "corgi")
        )
        coEvery { getAllBreedsUseCase() } returns Result.success(breeds)
        val viewModel = createViewModel()

        // Act - first filter, then clear
        viewModel.onSearchQueryChanged("akita")
        viewModel.onSearchQueryChanged("")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Success)
            val successState = state as BreedsUiState.Success
            assertEquals(3, successState.breeds.size)
            assertEquals("", successState.query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged is case insensitive`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita"),
            Breed(name = "bulldog")
        )
        coEvery { getAllBreedsUseCase() } returns Result.success(breeds)
        val viewModel = createViewModel()

        // Act
        viewModel.onSearchQueryChanged("AKITA")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedsUiState.Success)
            assertEquals(1, (state as BreedsUiState.Success).breeds.size)
            assertEquals("akita", state.breeds[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
