package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `app name matches Palindrome Checker`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Palindrome Checker", appName)
    composeTestRule.onNodeWithTag("app_title").assertIsDisplayed()
  }

  @Test
  fun `empty submit displays friendly validation message`() {
    composeTestRule.onNodeWithTag("check_palindrome_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("friendly_empty_message").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun `palindrome input displays It is a Palindrome with share and copy actions and clear removes it`() {
    composeTestRule.onNodeWithTag("palindrome_input").performScrollTo().performTextInput("racecar")
    composeTestRule.onNodeWithTag("check_palindrome_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("result_verdict_text").assertTextEquals("It is a Palindrome")
    composeTestRule.onNodeWithTag("copy_result_button").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("share_result_button").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("clear_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("result_card").assertDoesNotExist()
    composeTestRule.onNodeWithTag("empty_state_tip").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun `non palindrome input displays It is Not a Palindrome`() {
    composeTestRule.onNodeWithTag("palindrome_input").performScrollTo().performTextInput("android")
    composeTestRule.onNodeWithTag("check_palindrome_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("result_verdict_text").assertTextEquals("It is Not a Palindrome")
  }

  @Test
  fun `keyboard Done action triggers palindrome check and voice button is displayed`() {
    composeTestRule.onNodeWithTag("voice_input_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("palindrome_input").performScrollTo().performTextInput("12321")
    composeTestRule.onNodeWithTag("palindrome_input").performImeAction()
    composeTestRule.onNodeWithTag("result_verdict_text").assertTextEquals("It is a Palindrome")
  }

  @Test
  fun `navigation tabs open History, Quiz, and Stats screens`() {
    composeTestRule.onNodeWithTag("nav_history").performClick()
    composeTestRule.onNodeWithTag("history_title").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_quiz").performClick()
    composeTestRule.onNodeWithTag("quiz_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quiz_btn_yes").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("quiz_feedback_card").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_stats").performClick()
    composeTestRule.onNodeWithTag("stats_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("stats_overview_card").assertIsDisplayed()
  }

  @Test
  fun `learn and settings and about us pages open and navigate back cleanly`() {
    composeTestRule.onNodeWithTag("learn_button").performClick()
    composeTestRule.onNodeWithTag("learn_hero_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("learn_back_button").performClick()

    composeTestRule.onNodeWithTag("settings_button").performClick()
    composeTestRule.onNodeWithTag("theme_chip_dark").performClick()
    composeTestRule.onNodeWithTag("theme_chip_light").performClick()
    composeTestRule.onNodeWithTag("settings_back_button").performClick()

    composeTestRule.onNodeWithTag("about_us_button").performClick()
    composeTestRule.onNodeWithTag("about_project_title").performScrollTo().assertTextEquals("PALINDROME CHECKER")
    composeTestRule.onNodeWithText("Shadrack").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Betty").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Julian").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("back_button").performClick()

    composeTestRule.onNodeWithTag("app_title").assertIsDisplayed()
  }
}
