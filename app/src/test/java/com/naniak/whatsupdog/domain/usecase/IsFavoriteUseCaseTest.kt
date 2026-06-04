package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class IsFavoriteUseCaseTest {

    private val repository = mockk<FavoriteRepository>()
    private val useCase = IsFavoriteUseCase(repository)

    @Test
    fun `invoke returns Flow emitting true when image is favorited`() = runTest {
        // Arrange
        val imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg"
        every { repository.isFavorite(imageUrl) } returns flowOf(true)

        // Act & Assert
        useCase(imageUrl).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
        verify(exactly = 1) { repository.isFavorite(imageUrl) }
    }

    @Test
    fun `invoke returns Flow emitting false when image is not favorited`() = runTest {
        // Arrange
        val imageUrl = "https://images.dog.ceo/breeds/poodle/img1.jpg"
        every { repository.isFavorite(imageUrl) } returns flowOf(false)

        // Act & Assert
        useCase(imageUrl).test {
            assertFalse(awaitItem())
            awaitComplete()
        }
        verify(exactly = 1) { repository.isFavorite(imageUrl) }
    }

    @Test
    fun `invoke passes correct imageUrl to repository`() = runTest {
        // Arrange
        val imageUrl = "https://specific-url.com/dog.jpg"
        every { repository.isFavorite(imageUrl) } returns flowOf(false)

        // Act
        useCase(imageUrl).test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        verify(exactly = 1) { repository.isFavorite("https://specific-url.com/dog.jpg") }
    }

    @Test
    fun `invoke emits updated favorite status`() = runTest {
        // Arrange
        val imageUrl = "https://images.dog.ceo/breeds/husky/img1.jpg"
        every { repository.isFavorite(imageUrl) } returns kotlinx.coroutines.flow.flow {
            emit(false)
            emit(true)
        }

        // Act & Assert
        useCase(imageUrl).test {
            assertFalse(awaitItem())
            assertTrue(awaitItem())
            awaitComplete()
        }
    }
}
