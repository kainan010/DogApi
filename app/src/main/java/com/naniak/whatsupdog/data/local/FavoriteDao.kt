package com.naniak.whatsupdog.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE imageUrl = :imageUrl")
    suspend fun deleteByImageUrl(imageUrl: String)

    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE imageUrl = :imageUrl)")
    fun isFavorite(imageUrl: String): Flow<Boolean>

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)
}
