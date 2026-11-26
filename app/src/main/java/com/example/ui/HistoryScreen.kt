package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CheckHistoryEntity
import com.example.domain.InputCategory
import com.example.ui.theme.LocalPalindromeSemanticColors
import com.example.ui.theme.MonoSequenceTextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  historyState: HistoryUiState,
  onSearchChanged: (String) -> Unit,
  onFilterChanged: (HistoryFilter) -> Unit,
  onSelectEntry: (CheckHistoryEntity) -> Unit,
  onDeleteEntry: (Long) -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showClearDialog by remember { mutableStateOf(false) }

  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text(stringResource(R.string.history_clear_dialog_title)) },
      text = { Text(stringResource(R.string.history_clear_dialog_body)) },
      confirmButton = {
        TextButton(
          onClick = {
            showClearDialog = false
            onClearAll()
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

  Box(
    modifier = modifier.fillMaxSize().imePadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 720.dp)
          .fillMaxSize()
          .padding(horizontal = 20.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.history_title),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.testTag("history_title"),
        )

        if (historyState.totalStoredCount > 0) {
          TextButton(
            onClick = { showClearDialog = true },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("history_clear_all_button"),
          ) {
            Icon(
              imageVector = Icons.Outlined.DeleteSweep,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(stringResource(R.string.history_clear_all))
          }
        }
      }

      if (historyState.totalStoredCount > 0) {
        OutlinedTextField(
          value = historyState.searchQuery,
          onValueChange = onSearchChanged,
          modifier = Modifier.fillMaxWidth().testTag("history_search_input"),
          placeholder = { Text(stringResource(R.string.history_search_hint)) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Rounded.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
            )
          },
          trailingIcon = {
            if (historyState.searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { onSearchChanged("") },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Clear,
                  contentDescription = stringResource(R.string.clear_input_cd),
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline,
              cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          HistoryFilterChip(
            label = stringResource(R.string.history_filter_all),
            selected = historyState.filter == HistoryFilter.ALL,
            onClick = { onFilterChanged(HistoryFilter.ALL) },
          )
          HistoryFilterChip(
            label = stringResource(R.string.history_filter_palindromes),
            selected = historyState.filter == HistoryFilter.PALINDROMES,
            onClick = { onFilterChanged(HistoryFilter.PALINDROMES) },
          )
          HistoryFilterChip(
            label = stringResource(R.string.history_filter_not_palindromes),
            selected = historyState.filter == HistoryFilter.NOT_PALINDROMES,
            onClick = { onFilterChanged(HistoryFilter.NOT_PALINDROMES) },
          )
        }
      }

      if (historyState.totalStoredCount == 0) {
        HistoryEmptyCard(
          title = stringResource(R.string.history_empty_title),
          body = stringResource(R.string.history_empty_body),
          modifier = Modifier.testTag("history_empty_card"),
        )
      } else if (historyState.items.isEmpty()) {
        HistoryEmptyCard(
          title = stringResource(R.string.history_no_matches_title),
          body = stringResource(R.string.history_no_matches_body),
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize().testTag("history_list"),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 16.dp),
        ) {
          items(historyState.items, key = { it.id }) { item ->
            HistoryEntryCard(
              entry = item,
              onSelect = { onSelectEntry(item) },
              onDelete = { onDeleteEntry(item.id) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HistoryFilterChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    modifier = Modifier.minimumInteractiveComponentSize(),
    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
    shape = RoundedCornerShape(12.dp),
    colors =
      FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
      ),
  )
}

@Composable
private fun HistoryEntryCard(
  entry: CheckHistoryEntity,
  onSelect: () -> Unit,
  onDelete: () -> Unit,
) {
  val semantic = LocalPalindromeSemanticColors.current
  val accentColor = if (entry.isPalindrome) semantic.success else semantic.notMatch
  val badgeBg = if (entry.isPalindrome) semantic.successContainer else semantic.notMatchContainer
  val badgeFg =
    if (entry.isPalindrome) semantic.onSuccessContainer else semantic.onNotMatchContainer

  val formattedDate =
    remember(entry.checkedAtMillis) {
      SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(entry.checkedAtMillis))
    }

  val categoryLabel =
    when (entry.category) {
      InputCategory.NUMBER.name -> stringResource(R.string.category_number)
      InputCategory.SENTENCE.name -> stringResource(R.string.category_sentence)
      else -> stringResource(R.string.category_word)
    }

  Card(
    modifier =
      Modifier.fillMaxWidth()
        .clickable(onClick = onSelect)
        .testTag("history_entry_${entry.id}"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = badgeBg,
        modifier = Modifier.size(40.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector =
              if (entry.isPalindrome) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(22.dp),
          )
        }
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = entry.originalInput,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )

        Text(
          text = "${entry.forwardSequence} ↔ ${entry.backwardSequence}",
          style = MonoSequenceTextStyle.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(shape = RoundedCornerShape(999.dp), color = badgeBg) {
            Text(
              text =
                if (entry.isPalindrome) stringResource(R.string.badge_palindrome)
                else stringResource(R.string.badge_not_palindrome),
              style = MaterialTheme.typography.labelSmall,
              color = badgeFg,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }

          Text(
            text = "$categoryLabel • $formattedDate",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.minimumInteractiveComponentSize(),
      ) {
        Icon(
          imageVector = Icons.Outlined.DeleteOutline,
          contentDescription = stringResource(R.string.history_delete_item_cd),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun HistoryEmptyCard(
  title: String,
  body: String,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth().padding(top = 12.dp),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(28.dp),
          )
        }
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))
    }
  }
}
