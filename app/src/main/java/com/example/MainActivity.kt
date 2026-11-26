package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.ThemeMode
import com.example.ui.AboutUsScreen
import com.example.ui.HistoryScreen
import com.example.ui.LearnScreen
import com.example.ui.PalindromeCheckerScreen
import com.example.ui.PalindromeViewModel
import com.example.ui.QuizScreen
import com.example.ui.SettingsScreen
import com.example.ui.StatsScreen
import com.example.ui.theme.PalindromeCheckerTheme
import kotlinx.coroutines.launch

const val ROUTE_HOME = "home"
const val ROUTE_HISTORY = "history"
const val ROUTE_QUIZ = "quiz"
const val ROUTE_STATS = "stats"
const val ROUTE_LEARN = "learn"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_ABOUT_US = "about_us"

private data class MainTabItem(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val MainTabs =
  listOf(
    MainTabItem(
      route = ROUTE_HOME,
      labelRes = R.string.nav_checker,
      selectedIcon = Icons.Rounded.ManageSearch,
      unselectedIcon = Icons.Outlined.ManageSearch,
      testTag = "nav_checker",
    ),
    MainTabItem(
      route = ROUTE_HISTORY,
      labelRes = R.string.nav_history,
      selectedIcon = Icons.Rounded.History,
      unselectedIcon = Icons.Outlined.History,
      testTag = "nav_history",
    ),
    MainTabItem(
      route = ROUTE_QUIZ,
      labelRes = R.string.nav_quiz,
      selectedIcon = Icons.Rounded.Quiz,
      unselectedIcon = Icons.Outlined.Quiz,
      testTag = "nav_quiz",
    ),
    MainTabItem(
      route = ROUTE_STATS,
      labelRes = R.string.nav_stats,
      selectedIcon = Icons.Rounded.BarChart,
      unselectedIcon = Icons.Outlined.BarChart,
      testTag = "nav_stats",
    ),
  )

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PalindromeCheckerApp()
    }
  }
}

@Composable
fun PalindromeCheckerApp() {
  val sharedViewModel: PalindromeViewModel = viewModel()
  val preferences by sharedViewModel.preferences.collectAsStateWithLifecycle()
  val uiState by sharedViewModel.uiState.collectAsStateWithLifecycle()
  val recentChecks by sharedViewModel.recentChecks.collectAsStateWithLifecycle()
  val historyState by sharedViewModel.historyState.collectAsStateWithLifecycle()
  val quizState by sharedViewModel.quizState.collectAsStateWithLifecycle()
  val statisticsState by sharedViewModel.statisticsState.collectAsStateWithLifecycle()

  val isDarkMode =
    when (preferences.themeMode) {
      ThemeMode.SYSTEM -> isSystemInDarkTheme()
      ThemeMode.LIGHT -> false
      ThemeMode.DARK -> true
    }

  PalindromeCheckerTheme(darkTheme = isDarkMode) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_HOME
    val showMainNavigation = MainTabs.any { it.route == currentRoute }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    val navigateToTab: (String) -> Unit = { route ->
      navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        launchSingleTop = true
        restoreState = true
      }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val useNavigationRail = maxWidth >= 780.dp

      Scaffold(
        modifier =
          Modifier.fillMaxSize()
            .drawBehind {
              drawRect(color = colorScheme.background)
              drawCircle(
                brush =
                  Brush.radialGradient(
                    colors =
                      listOf(
                        colorScheme.primaryContainer.copy(alpha = 0.5f),
                        colorScheme.background.copy(alpha = 0f),
                      ),
                    center = Offset(size.width * 0.88f, size.height * 0.06f),
                    radius = size.maxDimension * 0.65f,
                  )
              )
              drawCircle(
                brush =
                  Brush.radialGradient(
                    colors =
                      listOf(
                        colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                        colorScheme.background.copy(alpha = 0f),
                      ),
                    center = Offset(size.width * 0.1f, size.height * 0.82f),
                    radius = size.maxDimension * 0.55f,
                  )
              )
            },
        containerColor = colorScheme.background.copy(alpha = 0f),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
          if (showMainNavigation && !useNavigationRail) {
            NavigationBar(
              containerColor = colorScheme.surface,
              contentColor = colorScheme.onSurface,
            ) {
              MainTabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationBarItem(
                  selected = selected,
                  onClick = { navigateToTab(tab.route) },
                  modifier = Modifier.testTag(tab.testTag),
                  icon = {
                    Icon(
                      imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = stringResource(tab.labelRes),
                    )
                  },
                  label = { Text(stringResource(tab.labelRes)) },
                )
              }
            }
          }
        },
      ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          if (showMainNavigation && useNavigationRail) {
            NavigationRail(
              containerColor = colorScheme.surface,
              contentColor = colorScheme.onSurface,
              modifier = Modifier.fillMaxHeight(),
            ) {
              MainTabs.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationRailItem(
                  selected = selected,
                  onClick = { navigateToTab(tab.route) },
                  modifier = Modifier.testTag(tab.testTag),
                  icon = {
                    Icon(
                      imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = stringResource(tab.labelRes),
                    )
                  },
                  label = { Text(stringResource(tab.labelRes)) },
                )
              }
            }
          }

          NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.weight(1f).fillMaxSize(),
            enterTransition = {
              slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(220),
              ) + fadeIn(animationSpec = tween(180))
            },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = {
              slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(220),
              ) + fadeOut(animationSpec = tween(180))
            },
          ) {
            composable(route = ROUTE_HOME) {
              PalindromeCheckerScreen(
                uiState = uiState,
                recentChecks = recentChecks,
                isDarkModeActive = isDarkMode,
                onInputChanged = sharedViewModel::onInputChanged,
                onToggleIgnorePunctuation = sharedViewModel::onToggleIgnoreSpacesAndPunctuation,
                onCheckPalindrome = sharedViewModel::onCheckPalindrome,
                onVoiceRecognized = sharedViewModel::onVoiceRecognized,
                onClear = sharedViewModel::onClear,
                onSelectExample = sharedViewModel::onSelectExample,
                onSelectRecent = sharedViewModel::onLoadHistoryEntry,
                onToggleDarkMode = { sharedViewModel.onQuickToggleTheme(isDarkMode) },
                onLearnClick = {
                  navController.navigate(ROUTE_LEARN) { launchSingleTop = true }
                },
                onSettingsClick = {
                  navController.navigate(ROUTE_SETTINGS) { launchSingleTop = true }
                },
                onAboutUsClick = {
                  navController.navigate(ROUTE_ABOUT_US) { launchSingleTop = true }
                },
                onShowMessage = { msg ->
                  coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                },
              )
            }

            composable(route = ROUTE_HISTORY) {
              HistoryScreen(
                historyState = historyState,
                onSearchChanged = sharedViewModel::onHistorySearchChanged,
                onFilterChanged = sharedViewModel::onHistoryFilterChanged,
                onSelectEntry = { entry ->
                  sharedViewModel.onLoadHistoryEntry(entry)
                  navigateToTab(ROUTE_HOME)
                },
                onDeleteEntry = sharedViewModel::onDeleteHistoryItem,
                onClearAll = sharedViewModel::onClearAllHistory,
              )
            }

            composable(route = ROUTE_QUIZ) {
              QuizScreen(
                quizState = quizState,
                bestScore = preferences.quizBestScore,
                onAnswer = sharedViewModel::onAnswerQuiz,
                onNextQuestion = sharedViewModel::onNextQuizQuestion,
                onStartNewQuiz = sharedViewModel::onStartNewQuiz,
              )
            }

            composable(route = ROUTE_STATS) {
              StatsScreen(stats = statisticsState)
            }

            composable(route = ROUTE_LEARN) {
              LearnScreen(
                onBackClick = { navController.popBackStack() },
                onTryExample = { example ->
                  sharedViewModel.onSelectExample(example)
                  navController.popBackStack()
                  navigateToTab(ROUTE_HOME)
                },
              )
            }

            composable(route = ROUTE_SETTINGS) {
              SettingsScreen(
                preferences = preferences,
                onBackClick = { navController.popBackStack() },
                onThemeModeChanged = sharedViewModel::onSetThemeMode,
                onDefaultIgnorePunctuationChanged = sharedViewModel::onSetDefaultIgnorePunctuation,
                onAutoCheckVoiceChanged = sharedViewModel::onSetAutoCheckVoiceInput,
                onClearHistory = sharedViewModel::onClearAllHistory,
                onResetQuizStats = sharedViewModel::onResetQuizStats,
                onOpenLearn = {
                  navController.navigate(ROUTE_LEARN) { launchSingleTop = true }
                },
                onOpenAboutUs = {
                  navController.navigate(ROUTE_ABOUT_US) { launchSingleTop = true }
                },
              )
            }

            composable(route = ROUTE_ABOUT_US) {
              AboutUsScreen(
                onBackClick = { navController.popBackStack() },
              )
            }
          }
        }
      }
    }
  }
}
