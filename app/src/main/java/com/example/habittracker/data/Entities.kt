package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.habittracker.domain.Habit
import java.time.LocalDate

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    /** LocalDate.toEpochDay() */
    val createdOnEpochDay: Long,
)

@Entity(
    tableName = "completions",
    primaryKeys = ["habitId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitId")],
)
data class CompletionEntity(
    val habitId: Long,
    /** День выполнения: LocalDate.toEpochDay() */
    val epochDay: Long,
)

fun HabitEntity.toDomain() = Habit(
    id = id,
    name = name,
    description = description,
    reminderEnabled = reminderEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdOn = LocalDate.ofEpochDay(createdOnEpochDay),
)

fun Habit.toEntity() = HabitEntity(
    id = id,
    name = name,
    description = description,
    reminderEnabled = reminderEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdOnEpochDay = createdOn.toEpochDay(),
)
