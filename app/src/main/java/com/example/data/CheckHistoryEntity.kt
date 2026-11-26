package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_history")
data class CheckHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val originalInput: String,
  val forwardSequence: String,
  val backwardSequence: String,
  val isPalindrome: Boolean,
  val ignoreSpacesAndPunctuation: Boolean,
  val category: String,
  val checkedAtMillis: Long = System.currentTimeMillis(),
)
