package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun observeHabit(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabit(id: Long): HabitEntity?

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM completions")
    fun observeAllCompletions(): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId")
    fun observeCompletions(habitId: Long): Flow<List<CompletionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM completions WHERE habitId = :habitId AND epochDay = :epochDay)")
    suspend fun isCompleted(habitId: Long, epochDay: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: CompletionEntity)

    @Query("DELETE FROM completions WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteCompletion(habitId: Long, epochDay: Long)
}
