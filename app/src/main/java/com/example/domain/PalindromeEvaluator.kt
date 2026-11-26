package com.example.domain

enum class ValidationReason {
  EMPTY_INPUT,
  NO_ALPHANUMERIC,
}

enum class InputCategory {
  WORD,
  NUMBER,
  SENTENCE,
}

data class PalindromeResult(
  val originalInput: String,
  val forwardSequence: String,
  val backwardSequence: String,
  val isPalindrome: Boolean,
  val ignoreSpacesAndPunctuation: Boolean,
  val mismatchIndex: Int?,
  val category: InputCategory = InputCategory.WORD,
)

sealed interface PalindromeEvaluation {
  data class Invalid(val reason: ValidationReason) : PalindromeEvaluation

  data class Evaluated(val result: PalindromeResult) : PalindromeEvaluation
}

object PalindromeEvaluator {
  fun classify(rawInput: String): InputCategory {
    val trimmed = rawInput.trim()
    val alphanumeric = trimmed.filter { it.isLetterOrDigit() }
    return when {
      alphanumeric.isNotEmpty() && alphanumeric.all { it.isDigit() } && !trimmed.any { it.isWhitespace() } ->
        InputCategory.NUMBER
      trimmed.any { it.isWhitespace() } -> InputCategory.SENTENCE
      else -> InputCategory.WORD
    }
  }

  fun evaluate(
    rawInput: String,
    ignoreSpacesAndPunctuation: Boolean = true,
  ): PalindromeEvaluation {
    if (rawInput.isBlank()) {
      return PalindromeEvaluation.Invalid(ValidationReason.EMPTY_INPUT)
    }

    val normalized =
      if (ignoreSpacesAndPunctuation) {
        buildString {
          for (ch in rawInput) {
            if (ch.isLetterOrDigit()) {
              append(ch.lowercaseChar())
            }
          }
        }
      } else {
        rawInput.lowercase()
      }

    if (normalized.isEmpty()) {
      return PalindromeEvaluation.Invalid(ValidationReason.NO_ALPHANUMERIC)
    }

    val reversed = normalized.reversed()
    var firstMismatch: Int? = null
    for (i in normalized.indices) {
      if (normalized[i] != reversed[i]) {
        firstMismatch = i
        break
      }
    }

    val trimmed = rawInput.trim()
    return PalindromeEvaluation.Evaluated(
      PalindromeResult(
        originalInput = trimmed,
        forwardSequence = normalized,
        backwardSequence = reversed,
        isPalindrome = firstMismatch == null,
        ignoreSpacesAndPunctuation = ignoreSpacesAndPunctuation,
        mismatchIndex = firstMismatch,
        category = classify(trimmed),
      )
    )
  }
}
