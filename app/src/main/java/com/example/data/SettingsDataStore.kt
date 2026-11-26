package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
  SYSTEM,
  LIGHT,
  DARK,
}

data class AppPreferences(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val defaultIgnoreSpacesAndPunctuation: Boolean = true,
  val autoCheckVoiceInput: Boolean = true,
  val quizBestScore: Int = 0,
  val quizBestStreak: Int = 0,
)

private val Context.settingsDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "palindrome_settings")

class SettingsRepository(private val context: Context) {
  private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DEFAULT_IGNORE_PUNCT = booleanPreferencesKey("default_ignore_punct")
    val AUTO_CHECK_VOICE = booleanPreferencesKey("auto_check_voice")
    val QUIZ_BEST_SCORE = intPreferencesKey("quiz_best_score")
    val QUIZ_BEST_STREAK = intPreferencesKey("quiz_best_streak")
  }

  val preferencesFlow: Flow<AppPreferences> =
    context.settingsDataStore.data.map { prefs ->
      val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
      val themeMode =
        ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.SYSTEM
      AppPreferences(
        themeMode = themeMode,
        defaultIgnoreSpacesAndPunctuation = prefs[Keys.DEFAULT_IGNORE_PUNCT] ?: true,
        autoCheckVoiceInput = prefs[Keys.AUTO_CHECK_VOICE] ?: true,
        quizBestScore = prefs[Keys.QUIZ_BEST_SCORE] ?: 0,
        quizBestStreak = prefs[Keys.QUIZ_BEST_STREAK] ?: 0,
      )
    }

  suspend fun setThemeMode(mode: ThemeMode) {
    context.settingsDataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode.name }
  }

  suspend fun setDefaultIgnoreSpacesAndPunctuation(ignore: Boolean) {
    context.settingsDataStore.edit { prefs -> prefs[Keys.DEFAULT_IGNORE_PUNCT] = ignore }
  }

  suspend fun setAutoCheckVoiceInput(enabled: Boolean) {
    context.settingsDataStore.edit { prefs -> prefs[Keys.AUTO_CHECK_VOICE] = enabled }
  }

  suspend fun updateQuizBestStats(score: Int, streak: Int) {
    context.settingsDataStore.edit { prefs ->
      val currentBestScore = prefs[Keys.QUIZ_BEST_SCORE] ?: 0
      val currentBestStreak = prefs[Keys.QUIZ_BEST_STREAK] ?: 0
      if (score > currentBestScore) {
        prefs[Keys.QUIZ_BEST_SCORE] = score
      }
      if (streak > currentBestStreak) {
        prefs[Keys.QUIZ_BEST_STREAK] = streak
      }
    }
  }

  suspend fun resetQuizStats() {
    context.settingsDataStore.edit { prefs ->
      prefs[Keys.QUIZ_BEST_SCORE] = 0
      prefs[Keys.QUIZ_BEST_STREAK] = 0
    }
  }
}
