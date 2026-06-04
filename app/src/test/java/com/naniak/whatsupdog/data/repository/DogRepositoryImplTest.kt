package com.naniak.whatsupdog.data.repository

import com.naniak.whatsupdog.data.remote.DogApiService
import com.naniak.whatsupdog.data.remote.dto.BreedImagesDto
import com.naniak.whatsupdog.data.remote.dto.BreedListDto
import com.naniak.whatsupdog.data.remote.dto.DogRandomDto
import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.model.DogImage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class DogRepositoryImplTest {

    private val apiService = mockk<DogApiService>()
    private val repository = DogRepositoryImpl(apiService)

    // --- getRandomDog ---

    @Test
    fun `getRandomDog returns Result success with mapped DogImage`() = runTest {
        // Arrange
        val dto = DogRandomDto(
            message = "https://images.dog.ceo/breeds/labrador/img1.jpg",
            status = "success"
        )
        coEvery { apiService.getRandomDog() } returns dto

        // Act
        val result = repository.getRandomDog()

        // Assert
        assertTrue(result.isSuccess)
        val dogImage = result.getOrNull()!!
        assertEquals("https://images.dog.ceo/breeds/labrador/img1.jpg", dogImage.imageUrl)
        assertEquals("labrador", dogImage.breed)
        coVerify(exactly = 1) { apiService.getRandomDog() }
    }

    @Test
    fun `getRandomDog returns Result failure when API throws exception`() = runTest {
        // Arrange
        coEvery { apiService.getRandomDog() } throws RuntimeException("Network error")

        // Act
        val result = repository.getRandomDog()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRandomDog extracts null breed when URL has no breeds segment`() = runTest {
        // Arrange
        val dto = DogRandomDto(
            message = "https://images.dog.ceo/other/img1.jpg",
            status = "success"
        )
        coEvery { apiService.getRandomDog() } returns dto

        // Act
        val result = repository.getRandomDog()

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.breed)
    }

    // --- getAllBreeds ---

    @Test
    fun `getAllBreeds returns Result success with sorted breed list`() = runTest {
        // Arrange
        val dto = BreedListDto(
            message = mapOf(
                "bulldog" to listOf("boston", "english", "french"),
                "akita" to emptyList(),
                "corgi" to listOf("cardigan")
            ),
            status = "success"
        )
        coEvery { apiService.getAllBreeds() } returns dto

        // Act
        val result = repository.getAllBreeds()

        // Assert
        assertTrue(result.isSuccess)
        val breeds = result.getOrNull()!!
        assertEquals(3, breeds.size)
        // Should be sorted by name
        assertEquals("akita", breeds[0].name)
        assertEquals("bulldog", breeds[1].name)
        assertEquals("corgi", breeds[2].name)
        assertEquals(listOf("boston", "english", "french"), breeds[1].subBreeds)
    }

    @Test
    fun `getAllBreeds returns Result failure when API throws exception`() = runTest {
        // Arrange
        coEvery { apiService.getAllBreeds() } throws RuntimeException("Server error")

        // Act
        val result = repository.getAllBreeds()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Server error", result.exceptionOrNull()?.message)
    }

    // --- getBreedImages ---

    @Test
    fun `getBreedImages returns Result success with mapped images`() = runTest {
        // Arrange
        val breed = "labrador"
        val dto = BreedImagesDto(
            message = listOf(
                "https://images.dog.ceo/breeds/labrador/img1.jpg",
                "https://images.dog.ceo/breeds/labrador/img2.jpg"
            ),
            status = "success"
        )
        coEvery { apiService.getBreedImages(breed) } returns dto

        // Act
        val result = repository.getBreedImages(breed)

        // Assert
        assertTrue(result.isSuccess)
        val images = result.getOrNull()!!
        assertEquals(2, images.size)
        assertEquals("https://images.dog.ceo/breeds/labrador/img1.jpg", images[0].imageUrl)
        assertEquals(breed, images[0].breed)
        assertEquals(breed, images[1].breed)
        coVerify(exactly = 1) { apiService.getBreedImages(breed) }
    }

    @Test
    fun `getBreedImages returns Result failure when API throws exception`() = runTest {
        // Arrange
        coEvery { apiService.getBreedImages(any()) } throws RuntimeException("Timeout")

        // Act
        val result = repository.getBreedImages("husky")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Timeout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBreedImages returns empty list when API returns no images`() = runTest {
        // Arrange
        val dto = BreedImagesDto(message = emptyList(), status = "success")
        coEvery { apiService.getBreedImages("rare") } returns dto

        // Act
        val result = repository.getBreedImages("rare")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }
}
