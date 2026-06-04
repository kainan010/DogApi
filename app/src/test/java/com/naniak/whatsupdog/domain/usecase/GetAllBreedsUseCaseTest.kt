package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.repository.DogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetAllBreedsUseCaseTest {

    private val repository = mockk<DogRepository>()
    private val useCase = GetAllBreedsUseCase(repository)

    @Test
    fun `invoke returns Result success with list of breeds`() = runTest {
        // Arrange
        val breeds = listOf(
            Breed(name = "akita", subBreeds = emptyList()),
            Breed(name = "bulldog", subBreeds = listOf("boston", "english", "french")),
            Breed(name = "corgi", subBreeds = listOf("cardigan"))
        )
        coEvery { repository.getAllBreeds() } returns Result.success(breeds)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(breeds, result.getOrNull())
        assertEquals(3, result.getOrNull()?.size)
        coVerify(exactly = 1) { repository.getAllBreeds() }
    }

    @Test
    fun `invoke returns Result failure when repository fails`() = runTest {
        // Arrange
        val exception = RuntimeException("API error")
        coEvery { repository.getAllBreeds() } returns Result.failure(exception)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.getAllBreeds() }
    }

    @Test
    fun `invoke returns empty list when repository returns no breeds`() = runTest {
        // Arrange
        coEvery { repository.getAllBreeds() } returns Result.success(emptyList())

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }
}
