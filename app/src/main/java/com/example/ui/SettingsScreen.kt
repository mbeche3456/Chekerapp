package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppPreferences
import com.example.data.ThemeMode
import com.example.ui.theme.LocalPalindromeSemanticColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  preferences: AppPreferences,
  onBackClick: () -> Unit,
  onThemeModeChanged: (ThemeMode) -> Unit,
  onDefaultIgnorePunctuationChanged: (Boolean) -> Unit,
  onAutoCheckVoiceChanged: (Boolean) -> Unit,
  onClearHistory: () -> Unit,
  onResetQuizStats: () -> Unit,
  onOpenLearn: () -> Unit,
  onOpenAboutUs: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showClearDialog by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val quizResetMsg = stringResource(R.string.settings_quiz_reset_done)

  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text(stringResource(R.string.history_clear_dialog_title)) },
      text = { Text(stringResource(R.string.history_clear_dialog_body)) },
      confirmButton = {
        TextButton(
          onClick = {
            showClearDialog = false
            onClearHistory()
          }
        ) {
          Text(
            text = stringResource(R.string.dialog_confirm_clear),
            color = LocalPalindromeSemanticColors.current.notMatch,
            fontWeight = FontWeight.Bold,
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text(stringResource(R.string.dialog_cancel))
        }
      },
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("settings_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back_home_cd),
              tint = MaterialTheme.colorScheme.primary,
            )
          }
        },
        colors =
          TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
          ),
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 680.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Appearance Section
        SectionHeader(stringResource(R.string.settings_appearance_section))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_theme_label),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              ThemeModeChip(
                label = stringResource(R.string.settings_theme_system),
                icon = Icons.Rounded.BrightnessAuto,
                selected = preferences.themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChanged(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f).testTag("theme_chip_system"),
              )
              ThemeModeChip(
                label = stringResource(R.string.settings_theme_light),
                icon = Icons.Rounded.LightMode,
                selected = preferences.themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChanged(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f).testTag("theme_chip_light"),
              )
              ThemeModeChip(
                label = stringResource(R.string.settings_theme_dark),
                icon = Icons.Rounded.DarkMode,
                selected = preferences.themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChanged(ThemeMode.DARK) },
                modifier = Modifier.weight(1f).testTag("theme_chip_dark"),
              )
            }
          }
        }

        // Checker Preferences Section
        SectionHeader(stringResource(R.string.settings_checker_section))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            SettingsSwitchRow(
              title = stringResource(R.string.settings_ignore_punct_title),
              subtitle = stringResource(R.string.settings_ignore_punct_subtitle),
              checked = preferences.defaultIgnoreSpacesAndPunctuation,
              onCheckedChange = onDefaultIgnorePunctuationChanged,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsSwitchRow(
              title = stringResource(R.string.settings_auto_voice_title),
              subtitle = stringResource(R.string.settings_auto_voice_subtitle),
              checked = preferences.autoCheckVoiceInput,
              onCheckedChange = onAutoCheckVoiceChanged,
            )
          }
        }

        // Data & Progress Section
        SectionHeader(stringResource(R.string.settings_data_section))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
              icon = Icons.Outlined.DeleteSweep,
              title = stringResource(R.string.settings_clear_history_title),
              subtitle = stringResource(R.string.settings_clear_history_subtitle),
              onClick = { showClearDialog = true },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsActionRow(
              icon = Icons.Outlined.RestartAlt,
              title = stringResource(R.string.settings_reset_quiz_title),
              subtitle = stringResource(R.string.settings_reset_quiz_subtitle),
              onClick = {
                onResetQuizStats()
                scope.launch { snackbarHostState.showSnackbar(quizResetMsg) }
              },
            )
          }
        }

        // Explore & Info Section
        SectionHeader(stringResource(R.string.settings_explore_section))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
              icon = Icons.Outlined.AutoStories,
              title = stringResource(R.string.settings_learn_row),
              subtitle = null,
              showChevron = true,
              onClick = onOpenLearn,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsActionRow(
              icon = Icons.Outlined.Groups,
              title = stringResource(R.string.settings_about_row),
              subtitle = null,
              showChevron = true,
              onClick = onOpenAboutUs,
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleSmall,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(horizontal = 4.dp),
  )
}

@Composable
private fun ThemeModeChip(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize(),
    label = {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
      )
    },
    leadingIcon = {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
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
}

@Composable
private fun SettingsSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable { onCheckedChange(!checked) }
        .minimumInteractiveComponentSize()
        .padding(horizontal = 18.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@Composable
private fun SettingsActionRow(
  icon: ImageVector,
  title: String,
  subtitle: String?,
  onClick: () -> Unit,
  showChevron: Boolean = false,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable(onClick = onClick)
        .minimumInteractiveComponentSize()
        .padding(horizontal = 18.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(22.dp),
    )
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    if (showChevron) {
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
