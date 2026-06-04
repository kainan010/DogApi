package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.DogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetBreedImagesUseCaseTest {

    private val repository = mockk<DogRepository>()
    private val useCase = GetBreedImagesUseCase(repository)

    @Test
    fun `invoke returns Result success with images for given breed`() = runTest {
        // Arrange
        val breed = "labrador"
        val images = listOf(
            DogImage(imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg", breed = breed),
            DogImage(imageUrl = "https://images.dog.ceo/breeds/labrador/img2.jpg", breed = breed),
            DogImage(imageUrl = "https://images.dog.ceo/breeds/labrador/img3.jpg", breed = breed)
        )
        coEvery { repository.getBreedImages(breed) } returns Result.success(images)

        // Act
        val result = useCase(breed)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(images, result.getOrNull())
        assertEquals(3, result.getOrNull()?.size)
        coVerify(exactly = 1) { repository.getBreedImages(breed) }
    }

    @Test
    fun `invoke returns Result failure when repository fails`() = runTest {
        // Arrange
        val breed = "labrador"
        val exception = RuntimeException("Network error")
        coEvery { repository.getBreedImages(breed) } returns Result.failure(exception)

        // Act
        val result = useCase(breed)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.getBreedImages(breed) }
    }

    @Test
    fun `invoke passes correct breed parameter to repository`() = runTest {
        // Arrange
        val breed = "husky"
        coEvery { repository.getBreedImages(breed) } returns Result.success(emptyList())

        // Act
        useCase(breed)

        // Assert
        coVerify(exactly = 1) { repository.getBreedImages("husky") }
    }
}
