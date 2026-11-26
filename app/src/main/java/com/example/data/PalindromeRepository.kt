package com.example.data

import com.example.domain.PalindromeResult
import kotlinx.coroutines.flow.Flow

class PalindromeRepository(
  private val checkHistoryDao: CheckHistoryDao,
  private val settingsRepository: SettingsRepository,
) {
  val historyFlow: Flow<List<CheckHistoryEntity>> = checkHistoryDao.observeAllHistory()
  val preferencesFlow: Flow<AppPreferences> = settingsRepository.preferencesFlow

  suspend fun recordCheck(result: PalindromeResult) {
    checkHistoryDao.insertCheck(
      CheckHistoryEntity(
        originalInput = result.originalInput,
        forwardSequence = result.forwardSequence,
        backwardSequence = result.backwardSequence,
        isPalindrome = result.isPalindrome,
        ignoreSpacesAndPunctuation = result.ignoreSpacesAndPunctuation,
        category = result.category.name,
      )
    )
  }

  suspend fun deleteHistoryItem(id: Long) = checkHistoryDao.deleteById(id)

  suspend fun clearHistory() = checkHistoryDao.clearAll()

  suspend fun setThemeMode(mode: ThemeMode) = settingsRepository.setThemeMode(mode)

  suspend fun setDefaultIgnoreSpacesAndPunctuation(ignore: Boolean) =
    settingsRepository.setDefaultIgnoreSpacesAndPunctuation(ignore)

  suspend fun setAutoCheckVoiceInput(enabled: Boolean) =
    settingsRepository.setAutoCheckVoiceInput(enabled)

  suspend fun updateQuizBestStats(score: Int, streak: Int) =
    settingsRepository.updateQuizBestStats(score, streak)

  suspend fun resetQuizStats() = settingsRepository.resetQuizStats()
}
