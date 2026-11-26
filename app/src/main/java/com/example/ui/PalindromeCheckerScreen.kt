package com.example.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CheckHistoryEntity
import com.example.domain.PalindromeResult
import com.example.domain.ValidationReason
import com.example.ui.theme.LocalPalindromeSemanticColors
import com.example.ui.theme.MonoSequenceTextStyle
import com.example.ui.theme.PalindromeCheckerTheme
import java.util.Locale
import kotlinx.coroutines.launch

private val QuickExamples =
  listOf(
    "racecar",
    "12321",
    "Madam, I'm Adam",
    "Was it a car or a cat I saw?",
    "hello",
  )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PalindromeCheckerScreen(
  uiState: PalindromeUiState,
  recentChecks: List<CheckHistoryEntity>,
  isDarkModeActive: Boolean,
  onInputChanged: (String) -> Unit,
  onToggleIgnorePunctuation: (Boolean) -> Unit,
  onCheckPalindrome: () -> Unit,
  onVoiceRecognized: (String) -> Unit,
  onClear: () -> Unit,
  onSelectExample: (String) -> Unit,
  onSelectRecent: (CheckHistoryEntity) -> Unit,
  onToggleDarkMode: () -> Unit,
  onLearnClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAboutUsClick: () -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val scrollState = rememberScrollState()
  val feedbackBringIntoViewRequester = remember { BringIntoViewRequester() }
  val inputBringIntoViewRequester = remember { BringIntoViewRequester() }
  val coroutineScope = rememberCoroutineScope()

  val voicePromptText = stringResource(R.string.voice_prompt)
  val voiceUnavailableText = stringResource(R.string.voice_unavailable)
  val voiceNoResultText = stringResource(R.string.voice_no_result)
  val copiedText = stringResource(R.string.copied_to_clipboard)
  val shareChooserTitle = stringResource(R.string.share_chooser_title)
  val isPalindromeVerdict = stringResource(R.string.result_is_palindrome)
  val notPalindromeVerdict = stringResource(R.string.result_not_palindrome)

  val voiceLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val spoken = matches?.firstOrNull()?.trim()
        if (!spoken.isNullOrEmpty()) {
          onVoiceRecognized(spoken)
        } else {
          onShowMessage(voiceNoResultText)
        }
      }
    }

  val launchVoiceInput: () -> Unit = {
    val intent =
      Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
          RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, voicePromptText)
      }
    try {
      voiceLauncher.launch(intent)
    } catch (_: ActivityNotFoundException) {
      onShowMessage(voiceUnavailableText)
    }
  }

  val formatShareText: (PalindromeResult) -> String = { res ->
    val verdict = if (res.isPalindrome) isPalindromeVerdict else notPalindromeVerdict
    "\"${res.originalInput}\" — $verdict\nForward: ${res.forwardSequence}\nBackward: ${res.backwardSequence}"
  }

  LaunchedEffect(uiState.result, uiState.validationReason) {
    if (uiState.result != null || uiState.validationReason != null) {
      feedbackBringIntoViewRequester.bringIntoView()
    }
  }

  BoxWithConstraints(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    val isWideLayout = maxWidth >= 780.dp

    if (isWideLayout) {
      Row(
        modifier =
          Modifier.widthIn(max = 1040.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.Top,
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
          WelcomeHeaderCard(
            isDarkModeActive = isDarkModeActive,
            onToggleDarkMode = onToggleDarkMode,
            onLearnClick = onLearnClick,
            onSettingsClick = onSettingsClick,
            onAboutUsClick = onAboutUsClick,
          )
          InputCard(
            inputText = uiState.inputText,
            ignoreSpacesAndPunctuation = uiState.ignoreSpacesAndPunctuation,
            onInputChanged = onInputChanged,
            onToggleIgnorePunctuation = onToggleIgnorePunctuation,
            onInputFocused = {
              keyboardController?.show()
              coroutineScope.launch { inputBringIntoViewRequester.bringIntoView() }
            },
            onVoiceClick = launchVoiceInput,
            onCheckPalindrome = {
              keyboardController?.hide()
              focusManager.clearFocus()
              onCheckPalindrome()
            },
            onClear = onClear,
            onSelectExample = { example ->
              keyboardController?.hide()
              focusManager.clearFocus()
              onSelectExample(example)
            },
            inputModifier = Modifier.bringIntoViewRequester(inputBringIntoViewRequester),
          )
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
          FeedbackSection(
            validationReason = uiState.validationReason,
            result = uiState.result,
            onCopyResult = { res ->
              clipboardManager.setText(AnnotatedString(formatShareText(res)))
              onShowMessage(copiedText)
            },
            onShareResult = { res ->
              val sendIntent =
                Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, formatShareText(res))
                }
              context.startActivity(Intent.createChooser(sendIntent, shareChooserTitle))
            },
            modifier = Modifier.bringIntoViewRequester(feedbackBringIntoViewRequester),
          )
          WhatIsPalindromeCard()
          if (recentChecks.isNotEmpty()) {
            RecentChecksCard(
              recentChecks = recentChecks,
              onSelectRecent = onSelectRecent,
            )
          }
        }
      }
    } else {
      Column(
        modifier =
          Modifier.widthIn(max = 600.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        WelcomeHeaderCard(
          isDarkModeActive = isDarkModeActive,
          onToggleDarkMode = onToggleDarkMode,
          onLearnClick = onLearnClick,
          onSettingsClick = onSettingsClick,
          onAboutUsClick = onAboutUsClick,
        )

        InputCard(
          inputText = uiState.inputText,
          ignoreSpacesAndPunctuation = uiState.ignoreSpacesAndPunctuation,
          onInputChanged = onInputChanged,
          onToggleIgnorePunctuation = onToggleIgnorePunctuation,
          onInputFocused = {
            keyboardController?.show()
            coroutineScope.launch { inputBringIntoViewRequester.bringIntoView() }
          },
          onVoiceClick = launchVoiceInput,
          onCheckPalindrome = {
            keyboardController?.hide()
            focusManager.clearFocus()
            onCheckPalindrome()
          },
          onClear = onClear,
          onSelectExample = { example ->
            keyboardController?.hide()
            focusManager.clearFocus()
            onSelectExample(example)
          },
          inputModifier = Modifier.bringIntoViewRequester(inputBringIntoViewRequester),
        )

        FeedbackSection(
          validationReason = uiState.validationReason,
          result = uiState.result,
          onCopyResult = { res ->
            clipboardManager.setText(AnnotatedString(formatShareText(res)))
            onShowMessage(copiedText)
          },
          onShareResult = { res ->
            val sendIntent =
              Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, formatShareText(res))
              }
            context.startActivity(Intent.createChooser(sendIntent, shareChooserTitle))
          },
          modifier = Modifier.bringIntoViewRequester(feedbackBringIntoViewRequester),
        )

        WhatIsPalindromeCard()

        if (recentChecks.isNotEmpty()) {
          RecentChecksCard(
            recentChecks = recentChecks,
            onSelectRecent = onSelectRecent,
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun WelcomeHeaderCard(
  isDarkModeActive: Boolean,
  onToggleDarkMode: () -> Unit,
  onLearnClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAboutUsClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_lavender_hero),
          contentDescription = stringResource(R.string.hero_image_cd),
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors =
                    listOf(
                      MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                      MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    )
                )
              )
        )

        Row(
          modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          HeaderMiniIconButton(
            icon = if (isDarkModeActive) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = stringResource(R.string.action_toggle_theme_cd),
            onClick = onToggleDarkMode,
            testTag = "theme_toggle_button",
          )
          HeaderMiniIconButton(
            icon = Icons.Outlined.AutoStories,
            contentDescription = stringResource(R.string.action_learn_cd),
            onClick = onLearnClick,
            testTag = "learn_button",
          )
          HeaderMiniIconButton(
            icon = Icons.Outlined.Settings,
            contentDescription = stringResource(R.string.action_settings_cd),
            onClick = onSettingsClick,
            testTag = "settings_button",
          )
          FilledTonalButton(
            onClick = onAboutUsClick,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("about_us_button"),
            shape = RoundedCornerShape(999.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            colors =
              ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                contentColor = MaterialTheme.colorScheme.primary,
              ),
          ) {
            Icon(
              imageVector = Icons.Outlined.Groups,
              contentDescription = stringResource(R.string.about_us_cd),
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.btn_about_us),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(48.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Rounded.ManageSearch,
              contentDescription = stringResource(R.string.search_icon_cd),
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(28.dp),
            )
          }
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("app_title"),
          )
          Text(
            text = stringResource(R.string.hero_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun HeaderMiniIconButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
  ) {
    IconButton(
      onClick = onClick,
      modifier = Modifier.size(40.dp).minimumInteractiveComponentSize().testTag(testTag),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp),
      )
    }
  }
}

@Composable
private fun InputCard(
  inputText: String,
  ignoreSpacesAndPunctuation: Boolean,
  onInputChanged: (String) -> Unit,
  onToggleIgnorePunctuation: (Boolean) -> Unit,
  onInputFocused: () -> Unit,
  onVoiceClick: () -> Unit,
  onCheckPalindrome: () -> Unit,
  onClear: () -> Unit,
  onSelectExample: (String) -> Unit,
  modifier: Modifier = Modifier,
  inputModifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { updated ->
          if (updated.contains('\n')) {
            onInputChanged(updated.replace("\n", ""))
            onCheckPalindrome()
          } else {
            onInputChanged(updated)
          }
        },
        modifier =
          inputModifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
              if (focusState.isFocused) {
                onInputFocused()
              }
            }
            .onPreviewKeyEvent { keyEvent ->
              if (
                keyEvent.type == KeyEventType.KeyDown &&
                  (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)
              ) {
                onCheckPalindrome()
                true
              } else {
                false
              }
            }
            .testTag("palindrome_input"),
        label = { Text(text = stringResource(R.string.input_label)) },
        placeholder = {
          Text(
            text = stringResource(R.string.input_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.EditNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
        },
        trailingIcon = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (inputText.isNotEmpty()) {
              IconButton(
                onClick = onClear,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("inline_clear_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Clear,
                  contentDescription = stringResource(R.string.clear_input_cd),
                  tint = MaterialTheme.colorScheme.primary,
                )
              }
            }
            IconButton(
              onClick = onVoiceClick,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("voice_input_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = stringResource(R.string.voice_input_cd),
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(18.dp),
        colors =
          OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            selectionColors =
              TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
              ),
          ),
        keyboardOptions =
          KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = true,
          ),
        keyboardActions = KeyboardActions(onDone = { onCheckPalindrome() }),
      )

      FilterChip(
        selected = ignoreSpacesAndPunctuation,
        onClick = { onToggleIgnorePunctuation(!ignoreSpacesAndPunctuation) },
        modifier = Modifier.minimumInteractiveComponentSize().testTag("ignore_punctuation_chip"),
        label = {
          Text(
            text =
              if (ignoreSpacesAndPunctuation) {
                stringResource(R.string.mode_ignore_punctuation)
              } else {
                stringResource(R.string.mode_exact_characters)
              },
            style = MaterialTheme.typography.labelMedium,
          )
        },
        leadingIcon = {
          Icon(
            imageVector =
              if (ignoreSpacesAndPunctuation) Icons.Rounded.Check else Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
        },
        shape = RoundedCornerShape(12.dp),
        colors =
          FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          ),
      )

      Button(
        onClick = onCheckPalindrome,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 56.dp)
            .testTag("check_palindrome_button"),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
      ) {
        Icon(
          imageVector = Icons.Rounded.ManageSearch,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = stringResource(R.string.btn_check_palindrome),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      OutlinedButton(
        onClick = onClear,
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("clear_button"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.Backspace,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.btn_clear),
          style = MaterialTheme.typography.labelLarge,
          maxLines = 1,
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = stringResource(R.string.try_examples_title),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          QuickExamples.forEach { example ->
            AssistChip(
              onClick = { onSelectExample(example) },
              modifier = Modifier.minimumInteractiveComponentSize(),
              label = {
                Text(
                  text = example,
                  style = MaterialTheme.typography.labelMedium,
                )
              },
              shape = RoundedCornerShape(12.dp),
              colors =
                AssistChipDefaults.assistChipColors(
                  containerColor = MaterialTheme.colorScheme.surfaceContainer,
                  labelColor = MaterialTheme.colorScheme.onSurface,
                ),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FeedbackSection(
  validationReason: ValidationReason?,
  result: PalindromeResult?,
  onCopyResult: (PalindromeResult) -> Unit,
  onShareResult: (PalindromeResult) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .animateContentSize(
          animationSpec =
            spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow,
            )
        ),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    AnimatedVisibility(
      visible = validationReason != null,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      if (validationReason != null) {
        FriendlyValidationCard(reason = validationReason)
      }
    }

    AnimatedVisibility(
      visible = result != null,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      if (result != null) {
        AnimatedContent(
          targetState = result,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "PalindromeResultTransition",
        ) { currentResult ->
          PalindromeResultCard(
            result = currentResult,
            onCopy = { onCopyResult(currentResult) },
            onShare = { onShareResult(currentResult) },
          )
        }
      }
    }

    AnimatedVisibility(
      visible = validationReason == null && result == null,
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      EmptyStateTipCard()
    }
  }
}

@Composable
private fun FriendlyValidationCard(
  reason: ValidationReason,
  modifier: Modifier = Modifier,
) {
  val semantic = LocalPalindromeSemanticColors.current
  val message =
    when (reason) {
      ValidationReason.EMPTY_INPUT -> stringResource(R.string.friendly_empty_input)
      ValidationReason.NO_ALPHANUMERIC -> stringResource(R.string.friendly_no_alphanumeric)
    }

  Card(
    modifier = modifier.fillMaxWidth().testTag("friendly_empty_message"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = semantic.warningContainer),
    border = BorderStroke(1.dp, semantic.warning.copy(alpha = 0.35f)),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = semantic.warning.copy(alpha = 0.16f),
        modifier = Modifier.size(44.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Rounded.Lightbulb,
            contentDescription = null,
            tint = semantic.warning,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = semantic.onWarningContainer,
        fontWeight = FontWeight.Medium,
      )
    }
  }
}

@Composable
private fun PalindromeResultCard(
  result: PalindromeResult,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val semantic = LocalPalindromeSemanticColors.current
  val accentColor = if (result.isPalindrome) semantic.success else semantic.notMatch
  val containerColor =
    if (result.isPalindrome) semantic.successContainer else semantic.notMatchContainer
  val onContainerColor =
    if (result.isPalindrome) semantic.onSuccessContainer else semantic.onNotMatchContainer

  Card(
    modifier = modifier.fillMaxWidth().testTag("result_card"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.45f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color = accentColor.copy(alpha = 0.16f),
          modifier = Modifier.size(52.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector =
                if (result.isPalindrome) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(30.dp),
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text =
              if (result.isPalindrome) {
                stringResource(R.string.result_is_palindrome)
              } else {
                stringResource(R.string.result_not_palindrome)
              },
            style = MaterialTheme.typography.headlineSmall,
            color = onContainerColor,
            modifier = Modifier.testTag("result_verdict_text"),
          )
          Text(
            text =
              if (result.isPalindrome) {
                stringResource(R.string.result_palindrome_detail)
              } else {
                stringResource(R.string.result_not_palindrome_detail)
              },
            style = MaterialTheme.typography.bodyMedium,
            color = onContainerColor.copy(alpha = 0.85f),
          )
        }
      }

      Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.22f)),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          SequenceRow(
            label = stringResource(R.string.label_forward),
            icon = Icons.AutoMirrored.Rounded.ArrowForward,
            sequence = result.forwardSequence,
            mismatchIndex = result.mismatchIndex,
            accentColor = accentColor,
            mismatchColor = semantic.notMatch,
          )
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
          SequenceRow(
            label = stringResource(R.string.label_backward),
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            sequence = result.backwardSequence,
            mismatchIndex = result.mismatchIndex,
            accentColor = accentColor,
            mismatchColor = semantic.notMatch,
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedButton(
          onClick = onCopy,
          modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("copy_result_button"),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
          colors =
            ButtonDefaults.outlinedButtonColors(
              containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
              contentColor = onContainerColor,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.btn_copy_result),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        Button(
          onClick = onShare,
          modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("share_result_button"),
          shape = RoundedCornerShape(14.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Share,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.btn_share_result),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun SequenceRow(
  label: String,
  icon: ImageVector,
  sequence: String,
  mismatchIndex: Int?,
  accentColor: androidx.compose.ui.graphics.Color,
  mismatchColor: androidx.compose.ui.graphics.Color,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      modifier = Modifier.width(100.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(16.dp),
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    val annotatedSequence = buildAnnotatedString {
      sequence.forEachIndexed { index, ch ->
        if (mismatchIndex != null && index == mismatchIndex) {
          withStyle(
            SpanStyle(
              color = mismatchColor,
              fontWeight = FontWeight.Bold,
              background = mismatchColor.copy(alpha = 0.16f),
            )
          ) {
            append(ch)
          }
        } else {
          append(ch)
        }
      }
    }

    Text(
      text = annotatedSequence,
      style = MonoSequenceTextStyle,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun EmptyStateTipCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth().testTag("empty_state_tip"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.Top,
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(40.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(22.dp),
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = stringResource(R.string.empty_state_tip_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = stringResource(R.string.empty_state_tip_body),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun WhatIsPalindromeCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth().testTag("palindrome_explanation_card"),
    shape = MaterialTheme.shapes.large,
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
      ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.Top,
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        modifier = Modifier.size(40.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.HelpOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = stringResource(R.string.what_is_palindrome_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = stringResource(R.string.what_is_palindrome_body),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun RecentChecksCard(
  recentChecks: List<CheckHistoryEntity>,
  onSelectRecent: (CheckHistoryEntity) -> Unit,
  modifier: Modifier = Modifier,
) {
  val semantic = LocalPalindromeSemanticColors.current

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Outlined.History,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp),
        )
        Text(
          text = stringResource(R.string.recent_checks_title),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      recentChecks.forEachIndexed { index, item ->
        val badgeBg =
          if (item.isPalindrome) semantic.successContainer else semantic.notMatchContainer
        val badgeText =
          if (item.isPalindrome) semantic.onSuccessContainer else semantic.onNotMatchContainer

        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clickable { onSelectRecent(item) }
              .minimumInteractiveComponentSize()
              .padding(horizontal = 20.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = item.originalInput,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 12.dp),
          )

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = badgeBg,
          ) {
            Text(
              text =
                if (item.isPalindrome) {
                  stringResource(R.string.badge_palindrome)
                } else {
                  stringResource(R.string.badge_not_palindrome)
                },
              style = MaterialTheme.typography.labelSmall,
              color = badgeText,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
        }

        if (index < recentChecks.lastIndex) {
          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PalindromeCheckerScreenPreview() {
  PalindromeCheckerTheme {
    PalindromeCheckerScreen(
      uiState =
        PalindromeUiState(
          inputText = "Madam, I'm Adam",
          result =
            PalindromeResult(
              originalInput = "Madam, I'm Adam",
              forwardSequence = "madamimadam",
              backwardSequence = "madamimadam",
              isPalindrome = true,
              ignoreSpacesAndPunctuation = true,
              mismatchIndex = null,
            ),
        ),
      recentChecks = emptyList(),
      isDarkModeActive = false,
      onInputChanged = {},
      onToggleIgnorePunctuation = {},
      onCheckPalindrome = {},
      onVoiceRecognized = {},
      onClear = {},
      onSelectExample = {},
      onSelectRecent = {},
      onToggleDarkMode = {},
      onLearnClick = {},
      onSettingsClick = {},
      onAboutUsClick = {},
      onShowMessage = {},
    )
  }
}
