package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppPreferences
import com.example.data.CheckHistoryEntity
import com.example.data.PalindromeRepository
import com.example.data.SettingsRepository
import com.example.data.ThemeMode
import com.example.domain.InputCategory
import com.example.domain.PalindromeEvaluation
import com.example.domain.PalindromeEvaluator
import com.example.domain.PalindromeResult
import com.example.domain.QuizBank
import com.example.domain.QuizPrompt
import com.example.domain.ValidationReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PalindromeUiState(
  val inputText: String = "",
  val ignoreSpacesAndPunctuation: Boolean = true,
  val validationReason: ValidationReason? = null,
  val result: PalindromeResult? = null,
)

enum class HistoryFilter {
  ALL,
  PALINDROMES,
  NOT_PALINDROMES,
}

data class HistoryUiState(
  val items: List<CheckHistoryEntity> = emptyList(),
  val totalStoredCount: Int = 0,
  val searchQuery: String = "",
  val filter: HistoryFilter = HistoryFilter.ALL,
)

data class StatisticsUiState(
  val totalChecks: Int = 0,
  val palindromeCount: Int = 0,
  val nonPalindromeCount: Int = 0,
  val matchPercentage: Int = 0,
  val wordCount: Int = 0,
  val numberCount: Int = 0,
  val sentenceCount: Int = 0,
  val longestPalindrome: String? = null,
  val quizBestScore: Int = 0,
  val quizBestStreak: Int = 0,
)

data class QuizUiState(
  val questions: List<QuizPrompt> = QuizBank.buildRound(),
  val currentIndex: Int = 0,
  val selectedAnswer: Boolean? = null,
  val score: Int = 0,
  val currentStreak: Int = 0,
  val roundBestStreak: Int = 0,
  val isRoundComplete: Boolean = false,
  val isNewBestScore: Boolean = false,
) {
  val currentQuestion: QuizPrompt?
    get() = questions.getOrNull(currentIndex)

  val hasAnsweredCurrent: Boolean
    get() = selectedAnswer != null

  val isCurrentAnswerCorrect: Boolean?
    get() =
      selectedAnswer?.let { answer ->
        currentQuestion?.let { q -> answer == q.isPalindrome }
      }
}

class PalindromeViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: PalindromeRepository =
    PalindromeRepository(
      checkHistoryDao = AppDatabase.getInstance(application).checkHistoryDao(),
      settingsRepository = SettingsRepository(application),
    )

  private val _uiState = MutableStateFlow(PalindromeUiState())
  val uiState: StateFlow<PalindromeUiState> = _uiState.asStateFlow()

  private val _historyFilter = MutableStateFlow(HistoryFilter.ALL)
  private val _historySearch = MutableStateFlow("")

  private val _quizState = MutableStateFlow(QuizUiState())
  val quizState: StateFlow<QuizUiState> = _quizState.asStateFlow()

  val preferences: StateFlow<AppPreferences> =
    repository.preferencesFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = AppPreferences(),
    )

  val recentChecks: StateFlow<List<CheckHistoryEntity>> =
    repository.historyFlow
      .map { list -> list.take(4) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  val historyState: StateFlow<HistoryUiState> =
    combine(repository.historyFlow, _historyFilter, _historySearch) { allItems, filter, query ->
      val trimmedQuery = query.trim().lowercase()
      val filtered =
        allItems.filter { item ->
          val matchesFilter =
            when (filter) {
              HistoryFilter.ALL -> true
              HistoryFilter.PALINDROMES -> item.isPalindrome
              HistoryFilter.NOT_PALINDROMES -> !item.isPalindrome
            }
          val matchesQuery =
            trimmedQuery.isEmpty() || item.originalInput.lowercase().contains(trimmedQuery)
          matchesFilter && matchesQuery
        }
      HistoryUiState(
        items = filtered,
        totalStoredCount = allItems.size,
        searchQuery = query,
        filter = filter,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(),
      )

  val statisticsState: StateFlow<StatisticsUiState> =
    combine(repository.historyFlow, repository.preferencesFlow) { history, prefs ->
      val total = history.size
      val palindromes = history.count { it.isPalindrome }
      val nonPalindromes = total - palindromes
      val matchPct = if (total == 0) 0 else ((palindromes * 100f) / total).toInt()
      val words = history.count { it.category == InputCategory.WORD.name }
      val numbers = history.count { it.category == InputCategory.NUMBER.name }
      val sentences = history.count { it.category == InputCategory.SENTENCE.name }
      val longestPalindrome =
        history
          .filter { it.isPalindrome }
          .maxByOrNull { it.forwardSequence.length }
          ?.originalInput

      StatisticsUiState(
        totalChecks = total,
        palindromeCount = palindromes,
        nonPalindromeCount = nonPalindromes,
        matchPercentage = matchPct,
        wordCount = words,
        numberCount = numbers,
        sentenceCount = sentences,
        longestPalindrome = longestPalindrome,
        quizBestScore = prefs.quizBestScore,
        quizBestStreak = prefs.quizBestStreak,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState(),
      )

  init {
    viewModelScope.launch {
      val initialPrefs = repository.preferencesFlow.first()
      _uiState.update { it.copy(ignoreSpacesAndPunctuation = initialPrefs.defaultIgnoreSpacesAndPunctuation) }
    }
  }

  fun onInputChanged(newText: String) {
    _uiState.update { state ->
      state.copy(
        inputText = newText,
        validationReason = null,
        result = null,
      )
    }
  }

  fun onToggleIgnoreSpacesAndPunctuation(ignore: Boolean) {
    _uiState.update { state ->
      val updated = state.copy(ignoreSpacesAndPunctuation = ignore)
      if (state.result != null && state.inputText.isNotBlank()) {
        evaluateAndApply(updated, saveToHistory = false)
      } else {
        updated
      }
    }
  }

  fun onCheckPalindrome() {
    _uiState.update { state -> evaluateAndApply(state, saveToHistory = true) }
  }

  fun onVoiceRecognized(spokenText: String) {
    val autoCheck = preferences.value.autoCheckVoiceInput
    _uiState.update { state ->
      val updated = state.copy(inputText = spokenText, validationReason = null)
      if (autoCheck) {
        evaluateAndApply(updated, saveToHistory = true)
      } else {
        updated.copy(result = null)
      }
    }
  }

  fun onSelectExample(example: String) {
    _uiState.update { state ->
      val withExample =
        state.copy(
          inputText = example,
          validationReason = null,
        )
      evaluateAndApply(withExample, saveToHistory = true)
    }
  }

  fun onLoadHistoryEntry(entry: CheckHistoryEntity) {
    _uiState.update { state ->
      val updated =
        state.copy(
          inputText = entry.originalInput,
          ignoreSpacesAndPunctuation = entry.ignoreSpacesAndPunctuation,
          validationReason = null,
        )
      evaluateAndApply(updated, saveToHistory = false)
    }
  }

  fun onClear() {
    _uiState.update { state ->
      state.copy(
        inputText = "",
        validationReason = null,
        result = null,
      )
    }
  }

  // History actions
  fun onHistorySearchChanged(query: String) {
    _historySearch.value = query
  }

  fun onHistoryFilterChanged(filter: HistoryFilter) {
    _historyFilter.value = filter
  }

  fun onDeleteHistoryItem(id: Long) {
    viewModelScope.launch { repository.deleteHistoryItem(id) }
  }

  fun onClearAllHistory() {
    viewModelScope.launch { repository.clearHistory() }
  }

  // Quiz actions
  fun onAnswerQuiz(userSaysPalindrome: Boolean) {
    _quizState.update { current ->
      if (current.hasAnsweredCurrent || current.isRoundComplete) return@update current
      val question = current.currentQuestion ?: return@update current
      val correct = userSaysPalindrome == question.isPalindrome
      val newScore = if (correct) current.score + 1 else current.score
      val newStreak = if (correct) current.currentStreak + 1 else 0
      val newBestStreak = maxOf(current.roundBestStreak, newStreak)
      current.copy(
        selectedAnswer = userSaysPalindrome,
        score = newScore,
        currentStreak = newStreak,
        roundBestStreak = newBestStreak,
      )
    }
  }

  fun onNextQuizQuestion() {
    val current = _quizState.value
    if (!current.hasAnsweredCurrent) return
    val nextIndex = current.currentIndex + 1
    if (nextIndex >= current.questions.size) {
      val previousBest = preferences.value.quizBestScore
      val isNewBest = current.score > previousBest && current.score > 0
      _quizState.update {
        it.copy(
          isRoundComplete = true,
          isNewBestScore = isNewBest,
        )
      }
      viewModelScope.launch {
        repository.updateQuizBestStats(current.score, current.roundBestStreak)
      }
    } else {
      _quizState.update {
        it.copy(
          currentIndex = nextIndex,
          selectedAnswer = null,
        )
      }
    }
  }

  fun onStartNewQuiz() {
    _quizState.value = QuizUiState(questions = QuizBank.buildRound())
  }

  // Settings & Theme actions
  fun onSetThemeMode(mode: ThemeMode) {
    viewModelScope.launch { repository.setThemeMode(mode) }
  }

  fun onQuickToggleTheme(currentlyDark: Boolean) {
    val target = if (currentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
    onSetThemeMode(target)
  }

  fun onSetDefaultIgnorePunctuation(ignore: Boolean) {
    viewModelScope.launch {
      repository.setDefaultIgnoreSpacesAndPunctuation(ignore)
      _uiState.update { it.copy(ignoreSpacesAndPunctuation = ignore) }
    }
  }

  fun onSetAutoCheckVoiceInput(enabled: Boolean) {
    viewModelScope.launch { repository.setAutoCheckVoiceInput(enabled) }
  }

  fun onResetQuizStats() {
    viewModelScope.launch { repository.resetQuizStats() }
  }

  private fun evaluateAndApply(
    state: PalindromeUiState,
    saveToHistory: Boolean,
  ): PalindromeUiState {
    return when (
      val evaluation =
        PalindromeEvaluator.evaluate(
          rawInput = state.inputText,
          ignoreSpacesAndPunctuation = state.ignoreSpacesAndPunctuation,
        )
    ) {
      is PalindromeEvaluation.Invalid ->
        state.copy(
          validationReason = evaluation.reason,
          result = null,
        )
      is PalindromeEvaluation.Evaluated -> {
        val result = evaluation.result
        if (saveToHistory) {
          viewModelScope.launch { repository.recordCheck(result) }
        }
        state.copy(
          validationReason = null,
          result = result,
        )
      }
    }
  }
}
