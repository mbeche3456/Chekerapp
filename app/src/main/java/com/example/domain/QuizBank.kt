package com.example.domain

data class QuizPrompt(
  val promptText: String,
  val isPalindrome: Boolean,
  val category: InputCategory,
  val hint: String,
)

object QuizBank {
  private val allQuestions =
    listOf(
      QuizPrompt(
        promptText = "racecar",
        isPalindrome = true,
        category = InputCategory.WORD,
        hint = "Classic 7-letter palindrome word.",
      ),
      QuizPrompt(
        promptText = "12321",
        isPalindrome = true,
        category = InputCategory.NUMBER,
        hint = "Digits mirror symmetrically around 3.",
      ),
      QuizPrompt(
        promptText = "android",
        isPalindrome = false,
        category = InputCategory.WORD,
        hint = "Reversed it reads 'diordna'.",
      ),
      QuizPrompt(
        promptText = "Madam, I'm Adam",
        isPalindrome = true,
        category = InputCategory.SENTENCE,
        hint = "Letters read 'madamimadam' both ways.",
      ),
      QuizPrompt(
        promptText = "kayak",
        isPalindrome = true,
        category = InputCategory.WORD,
        hint = "Symmetric 5-letter watercraft word.",
      ),
      QuizPrompt(
        promptText = "2024",
        isPalindrome = false,
        category = InputCategory.NUMBER,
        hint = "Backwards it is 4202.",
      ),
      QuizPrompt(
        promptText = "Was it a car or a cat I saw?",
        isPalindrome = true,
        category = InputCategory.SENTENCE,
        hint = "Ignoring spaces & punctuation, it reads the same backwards.",
      ),
      QuizPrompt(
        promptText = "palindrome",
        isPalindrome = false,
        category = InputCategory.WORD,
        hint = "Ironically, the word 'palindrome' is not a palindrome!",
      ),
      QuizPrompt(
        promptText = "9009",
        isPalindrome = true,
        category = InputCategory.NUMBER,
        hint = "4-digit mirror number.",
      ),
      QuizPrompt(
        promptText = "Never odd or even",
        isPalindrome = true,
        category = InputCategory.SENTENCE,
        hint = "Letters form 'neveroddoreven' in both directions.",
      ),
      QuizPrompt(
        promptText = "level",
        isPalindrome = true,
        category = InputCategory.WORD,
        hint = "Reads 'level' forwards and backwards.",
      ),
      QuizPrompt(
        promptText = "Hello world",
        isPalindrome = false,
        category = InputCategory.SENTENCE,
        hint = "Reversed letters read 'dlrowolleh'.",
      ),
      QuizPrompt(
        promptText = "No lemon, no melon",
        isPalindrome = true,
        category = InputCategory.SENTENCE,
        hint = "Letters read 'nolemonnomelon' both ways.",
      ),
      QuizPrompt(
        promptText = "12345",
        isPalindrome = false,
        category = InputCategory.NUMBER,
        hint = "Reversed digits are 54321.",
      ),
      QuizPrompt(
        promptText = "rotator",
        isPalindrome = true,
        category = InputCategory.WORD,
        hint = "7-letter palindrome word.",
      ),
    )

  fun buildRound(size: Int = 8, shuffled: Boolean = true): List<QuizPrompt> {
    val source = if (shuffled) allQuestions.shuffled() else allQuestions
    return source.take(size.coerceAtMost(allQuestions.size))
  }
}
