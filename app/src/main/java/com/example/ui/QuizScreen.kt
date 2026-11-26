package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.InputCategory
import com.example.ui.theme.LocalPalindromeSemanticColors

@Composable
fun QuizScreen(
  quizState: QuizUiState,
  bestScore: Int,
  onAnswer: (Boolean) -> Unit,
  onNextQuestion: () -> Unit,
  onStartNewQuiz: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val semantic = LocalPalindromeSemanticColors.current

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 640.dp)
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = stringResource(R.string.quiz_title),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.testTag("quiz_title"),
        )
        Text(
          text = stringResource(R.string.quiz_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        QuizStatChip(
          label = stringResource(R.string.quiz_score_label, quizState.score),
          modifier = Modifier.weight(1f),
        )
        QuizStatChip(
          label = stringResource(R.string.quiz_streak_label, quizState.currentStreak),
          icon = Icons.Rounded.LocalFireDepartment,
          modifier = Modifier.weight(1f),
        )
        QuizStatChip(
          label = stringResource(R.string.quiz_best_label, maxOf(bestScore, quizState.score)),
          icon = Icons.Rounded.EmojiEvents,
          modifier = Modifier.weight(1f),
        )
      }

      if (quizState.isRoundComplete) {
        QuizSummaryCard(
          score = quizState.score,
          total = quizState.questions.size,
          bestStreak = quizState.roundBestStreak,
          isNewBestScore = quizState.isNewBestScore,
          onPlayAgain = onStartNewQuiz,
        )
      } else {
        val question = quizState.currentQuestion
        if (question != null) {
          val progress = (quizState.currentIndex + 1).toFloat() / quizState.questions.size.toFloat()

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text =
                stringResource(
                  R.string.quiz_question_counter,
                  quizState.currentIndex + 1,
                  quizState.questions.size,
                ),
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary,
            )
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.primaryContainer,
            )
          }

          Card(
            modifier = Modifier.fillMaxWidth().testTag("quiz_question_card"),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
              ) {
                Text(
                  text =
                    when (question.category) {
                      InputCategory.NUMBER -> stringResource(R.string.category_number)
                      InputCategory.SENTENCE -> stringResource(R.string.category_sentence)
                      InputCategory.WORD -> stringResource(R.string.category_word)
                    },
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
              }

              Text(
                text = question.promptText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("quiz_prompt_text"),
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Button(
              onClick = { onAnswer(true) },
              enabled = !quizState.hasAnsweredCurrent,
              modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("quiz_btn_yes"),
              shape = RoundedCornerShape(16.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = semantic.success,
                  contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.quiz_btn_yes),
                style = MaterialTheme.typography.labelLarge,
              )
            }

            OutlinedButton(
              onClick = { onAnswer(false) },
              enabled = !quizState.hasAnsweredCurrent,
              modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("quiz_btn_no"),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.5.dp, semantic.notMatch),
              colors =
                ButtonDefaults.outlinedButtonColors(
                  contentColor = semantic.notMatch,
                ),
            ) {
              Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.quiz_btn_no),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }

          AnimatedVisibility(
            visible = quizState.hasAnsweredCurrent,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
          ) {
            val isCorrect = quizState.isCurrentAnswerCorrect == true
            val accent = if (isCorrect) semantic.success else semantic.notMatch
            val container =
              if (isCorrect) semantic.successContainer else semantic.notMatchContainer
            val onContainer =
              if (isCorrect) semantic.onSuccessContainer else semantic.onNotMatchContainer

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Card(
                modifier = Modifier.fillMaxWidth().testTag("quiz_feedback_card"),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = container),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
              ) {
                Column(
                  modifier = Modifier.fillMaxWidth().padding(18.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Text(
                    text =
                      if (isCorrect) stringResource(R.string.quiz_correct_feedback)
                      else stringResource(R.string.quiz_incorrect_feedback),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onContainer,
                  )
                  Text(
                    text = question.hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainer.copy(alpha = 0.88f),
                  )
                }
              }

              Button(
                onClick = onNextQuestion,
                modifier =
                  Modifier.fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("quiz_next_button"),
                shape = RoundedCornerShape(16.dp),
              ) {
                Text(
                  text =
                    if (quizState.currentIndex + 1 >= quizState.questions.size) {
                      stringResource(R.string.quiz_btn_finish)
                    } else {
                      stringResource(R.string.quiz_btn_next)
                    },
                  style = MaterialTheme.typography.labelLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
private fun QuizStatChip(
  label: String,
  modifier: Modifier = Modifier,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun QuizSummaryCard(
  score: Int,
  total: Int,
  bestStreak: Int,
  isNewBestScore: Boolean,
  onPlayAgain: () -> Unit,
) {
  val semantic = LocalPalindromeSemanticColors.current
  Card(
    modifier = Modifier.fillMaxWidth().testTag("quiz_summary_card"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(64.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Rounded.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(34.dp),
          )
        }
      }

      if (isNewBestScore) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = semantic.successContainer,
        ) {
          Text(
            text = stringResource(R.string.quiz_new_best_badge),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = semantic.onSuccessContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
      }

      Text(
        text = stringResource(R.string.quiz_complete_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      Text(
        text = stringResource(R.string.quiz_complete_score, score, total),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
      )

      Text(
        text = stringResource(R.string.quiz_streak_label, bestStreak),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Button(
        onClick = onPlayAgain,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("quiz_play_again_button"),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(16.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.Refresh,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.quiz_btn_play_again))
      }
    }
  }
}
