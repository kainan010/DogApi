package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private val repository = mockk<FavoriteRepository>()
    private val useCase = ToggleFavoriteUseCase(repository)

    @Test
    fun `invoke delegates to repository toggleFavorite`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg",
            breed = "labrador"
        )
        coEvery { repository.toggleFavorite(dogImage) } returns Unit

        // Act
        useCase(dogImage)

        // Assert
        coVerify(exactly = 1) { repository.toggleFavorite(dogImage) }
    }

    @Test
    fun `invoke passes DogImage with null breed correctly`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/unknown/img.jpg",
            breed = null
        )
        coEvery { repository.toggleFavorite(dogImage) } returns Unit

        // Act
        useCase(dogImage)

        // Assert
        coVerify(exactly = 1) { repository.toggleFavorite(dogImage) }
    }

    @Test
    fun `invoke propagates exception from repository`() = runTest {
        // Arrange
        val dogImage = DogImage(imageUrl = "url", breed = "poodle")
        coEvery { repository.toggleFavorite(dogImage) } throws RuntimeException("DB error")

        // Act & Assert
        try {
            useCase(dogImage)
            throw AssertionError("Expected exception was not thrown")
        } catch (e: RuntimeException) {
            assert(e.message == "DB error")
        }
    }
}
