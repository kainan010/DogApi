package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.DogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetRandomDogUseCaseTest {

    private val repository = mockk<DogRepository>()
    private val useCase = GetRandomDogUseCase(repository)

    @Test
    fun `invoke returns Result success with DogImage when repository succeeds`() = runTest {
        // Arrange
        val expectedDog = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg",
            breed = "labrador"
        )
        coEvery { repository.getRandomDog() } returns Result.success(expectedDog)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedDog, result.getOrNull())
        coVerify(exactly = 1) { repository.getRandomDog() }
    }

    @Test
    fun `invoke returns Result failure when repository throws exception`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { repository.getRandomDog() } returns Result.failure(exception)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.getRandomDog() }
    }

    @Test
    fun `invoke returns DogImage with null breed when repository returns image without breed`() = runTest {
        // Arrange
        val dogWithoutBreed = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/unknown/img.jpg",
            breed = null
        )
        coEvery { repository.getRandomDog() } returns Result.success(dogWithoutBreed)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.breed)
    }
}
