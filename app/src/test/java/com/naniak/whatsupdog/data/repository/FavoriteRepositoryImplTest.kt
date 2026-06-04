package com.naniak.whatsupdog.data.repository

import com.naniak.whatsupdog.data.local.FavoriteDao
import com.naniak.whatsupdog.data.local.FavoriteEntity
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.model.FavoriteDog
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class FavoriteRepositoryImplTest {

    private val favoriteDao = mockk<FavoriteDao>()
    private val repository = FavoriteRepositoryImpl(favoriteDao)

    // --- toggleFavorite ---

    @Test
    fun `toggleFavorite inserts entity when image is not favorited`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg",
            breed = "labrador"
        )
        every { favoriteDao.isFavorite(dogImage.imageUrl) } returns flowOf(false)
        val entitySlot = slot<FavoriteEntity>()
        coEvery { favoriteDao.insert(capture(entitySlot)) } returns Unit

        // Act
        repository.toggleFavorite(dogImage)

        // Assert
        coVerify(exactly = 1) { favoriteDao.insert(any()) }
        coVerify(exactly = 0) { favoriteDao.deleteByImageUrl(any()) }
        assertEquals(dogImage.imageUrl, entitySlot.captured.imageUrl)
        assertEquals(dogImage.breed, entitySlot.captured.breed)
    }

    @Test
    fun `toggleFavorite deletes entity when image is already favorited`() = runTest {
        // Arrange
        val dogImage = DogImage(
            imageUrl = "https://images.dog.ceo/breeds/poodle/img1.jpg",
            breed = "poodle"
        )
        every { favoriteDao.isFavorite(dogImage.imageUrl) } returns flowOf(true)
        coEvery { favoriteDao.deleteByImageUrl(dogImage.imageUrl) } returns Unit

        // Act
        repository.toggleFavorite(dogImage)

        // Assert
        coVerify(exactly = 1) { favoriteDao.deleteByImageUrl(dogImage.imageUrl) }
        coVerify(exactly = 0) { favoriteDao.insert(any()) }
    }

    @Test
    fun `toggleFavorite handles null breed correctly`() = runTest {
        // Arrange
        val dogImage = DogImage(imageUrl = "https://dog.jpg", breed = null)
        every { favoriteDao.isFavorite(dogImage.imageUrl) } returns flowOf(false)
        val entitySlot = slot<FavoriteEntity>()
        coEvery { favoriteDao.insert(capture(entitySlot)) } returns Unit

        // Act
        repository.toggleFavorite(dogImage)

        // Assert
        coVerify(exactly = 1) { favoriteDao.insert(any()) }
        assertNull(entitySlot.captured.breed)
    }

    // --- getAllFavorites ---

    @Test
    fun `getAllFavorites maps entities to domain models correctly`() = runTest {
        // Arrange
        val entities = listOf(
            FavoriteEntity(id = 1, imageUrl = "url1", breed = "labrador", savedAt = 1000L),
            FavoriteEntity(id = 2, imageUrl = "url2", breed = "poodle", savedAt = 2000L)
        )
        every { favoriteDao.getAll() } returns flowOf(entities)

        // Act & Assert
        repository.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(2, favorites.size)

            assertEquals(1L, favorites[0].id)
            assertEquals("url1", favorites[0].imageUrl)
            assertEquals("labrador", favorites[0].breed)
            assertEquals(1000L, favorites[0].savedAt)

            assertEquals(2L, favorites[1].id)
            assertEquals("url2", favorites[1].imageUrl)
            assertEquals("poodle", favorites[1].breed)
            assertEquals(2000L, favorites[1].savedAt)

            awaitComplete()
        }
    }

    @Test
    fun `getAllFavorites returns empty list when no entities exist`() = runTest {
        // Arrange
        every { favoriteDao.getAll() } returns flowOf(emptyList())

        // Act & Assert
        repository.getAllFavorites().test {
            val favorites = awaitItem()
            assertTrue(favorites.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getAllFavorites maps entity with null breed`() = runTest {
        // Arrange
        val entities = listOf(
            FavoriteEntity(id = 1, imageUrl = "url1", breed = null, savedAt = 1000L)
        )
        every { favoriteDao.getAll() } returns flowOf(entities)

        // Act & Assert
        repository.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            assertNull(favorites[0].breed)
            awaitComplete()
        }
    }

    // --- isFavorite ---

    @Test
    fun `isFavorite delegates to DAO and returns true`() = runTest {
        // Arrange
        val imageUrl = "https://images.dog.ceo/breeds/labrador/img1.jpg"
        every { favoriteDao.isFavorite(imageUrl) } returns flowOf(true)

        // Act & Assert
        repository.isFavorite(imageUrl).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isFavorite delegates to DAO and returns false`() = runTest {
        // Arrange
        val imageUrl = "https://images.dog.ceo/breeds/poodle/img1.jpg"
        every { favoriteDao.isFavorite(imageUrl) } returns flowOf(false)

        // Act & Assert
        repository.isFavorite(imageUrl).test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }
}
