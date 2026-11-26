package com.example

import com.example.domain.PalindromeEvaluation
import com.example.domain.PalindromeEvaluator
import com.example.domain.ValidationReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PalindromeEvaluatorTest {

  @Test
  fun `single word palindrome returns true`() {
    val evaluation = PalindromeEvaluator.evaluate("Racecar")
    assertTrue(evaluation is PalindromeEvaluation.Evaluated)
    val result = (evaluation as PalindromeEvaluation.Evaluated).result
    assertTrue(result.isPalindrome)
    assertEquals("racecar", result.forwardSequence)
    assertEquals("racecar", result.backwardSequence)
    assertNull(result.mismatchIndex)
  }

  @Test
  fun `numeric palindrome returns true`() {
    val evaluation = PalindromeEvaluator.evaluate("12321")
    assertTrue(evaluation is PalindromeEvaluation.Evaluated)
    val result = (evaluation as PalindromeEvaluation.Evaluated).result
    assertTrue(result.isPalindrome)
  }

  @Test
  fun `sentence palindrome with spaces and punctuation returns true`() {
    val evaluation = PalindromeEvaluator.evaluate("A man, a plan, a canal: Panama!")
    assertTrue(evaluation is PalindromeEvaluation.Evaluated)
    val result = (evaluation as PalindromeEvaluation.Evaluated).result
    assertTrue(result.isPalindrome)
    assertEquals("amanaplanacanalpanama", result.forwardSequence)
  }

  @Test
  fun `non palindrome returns false with mismatch index`() {
    val evaluation = PalindromeEvaluator.evaluate("hello")
    assertTrue(evaluation is PalindromeEvaluation.Evaluated)
    val result = (evaluation as PalindromeEvaluation.Evaluated).result
    assertFalse(result.isPalindrome)
    assertEquals(0, result.mismatchIndex)
  }

  @Test
  fun `empty or whitespace input returns EMPTY_INPUT`() {
    val emptyEval = PalindromeEvaluator.evaluate("")
    val blankEval = PalindromeEvaluator.evaluate("   ")
    assertEquals(PalindromeEvaluation.Invalid(ValidationReason.EMPTY_INPUT), emptyEval)
    assertEquals(PalindromeEvaluation.Invalid(ValidationReason.EMPTY_INPUT), blankEval)
  }

  @Test
  fun `punctuation only input returns NO_ALPHANUMERIC when ignoring punctuation`() {
    val eval = PalindromeEvaluator.evaluate("... ! ?")
    assertEquals(PalindromeEvaluation.Invalid(ValidationReason.NO_ALPHANUMERIC), eval)
  }

  @Test
  fun `exact character mode treats spaces as significant`() {
    val eval = PalindromeEvaluator.evaluate("nurses run", ignoreSpacesAndPunctuation = false)
    assertTrue(eval is PalindromeEvaluation.Evaluated)
    assertFalse((eval as PalindromeEvaluation.Evaluated).result.isPalindrome)
  }
}
