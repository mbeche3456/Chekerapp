package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PalindromeCheckerTheme

private data class DeveloperProfile(
  val numberLabel: String,
  val nameRes: Int,
  val roleRes: Int,
  val bioRes: Int,
  val icon: ImageVector,
  val initial: String,
)

private val ProjectDevelopers =
  listOf(
    DeveloperProfile(
      numberLabel = "01",
      nameRes = R.string.dev_shadrack_name,
      roleRes = R.string.dev_shadrack_role,
      bioRes = R.string.dev_shadrack_bio,
      icon = Icons.Rounded.Code,
      initial = "S",
    ),
    DeveloperProfile(
      numberLabel = "02",
      nameRes = R.string.dev_betty_name,
      roleRes = R.string.dev_betty_role,
      bioRes = R.string.dev_betty_bio,
      icon = Icons.Rounded.DesignServices,
      initial = "B",
    ),
    DeveloperProfile(
      numberLabel = "03",
      nameRes = R.string.dev_julian_name,
      roleRes = R.string.dev_julian_role,
      bioRes = R.string.dev_julian_bio,
      icon = Icons.Rounded.VerifiedUser,
      initial = "J",
    ),
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme

  Scaffold(
    modifier =
      modifier
        .fillMaxSize()
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
                center = Offset(size.width * 0.15f, size.height * 0.12f),
                radius = size.maxDimension * 0.6f,
              )
          )
        },
    containerColor = colorScheme.background.copy(alpha = 0f),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = stringResource(R.string.about_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("back_button"),
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
            containerColor = colorScheme.background.copy(alpha = 0f),
            titleContentColor = colorScheme.onBackground,
          ),
      )
    },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      val isWideLayout = maxWidth >= 780.dp

      Column(
        modifier =
          Modifier.widthIn(max = if (isWideLayout) 1020.dp else 600.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        ProjectOverviewCard()

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Groups,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp),
            )
            Text(
              text = stringResource(R.string.about_developers_heading),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
          }
          Text(
            text = stringResource(R.string.about_developers_subheading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        if (isWideLayout) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            ProjectDevelopers.forEach { developer ->
              DeveloperProfileCard(
                developer = developer,
                modifier = Modifier.weight(1f),
              )
            }
          }
        } else {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            ProjectDevelopers.forEach { developer ->
              DeveloperProfileCard(developer = developer)
            }
          }
        }

        Button(
          onClick = onBackClick,
          modifier =
            Modifier.fillMaxWidth()
              .heightIn(min = 52.dp)
              .testTag("about_back_home_button"),
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.about_back_to_home_btn),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun ProjectOverviewCard(modifier: Modifier = Modifier) {
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
            .height(168.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_dev_team),
          contentDescription = stringResource(R.string.about_team_image_cd),
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
                      Color.Transparent,
                      MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    )
                )
              )
        )
      }

      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Groups,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = stringResource(R.string.about_project_title_upper),
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.testTag("about_project_title"),
            )
          }
        }

        Text(
          text = stringResource(R.string.about_project_description),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}

@Composable
private fun DeveloperProfileCard(
  developer: DeveloperProfile,
  modifier: Modifier = Modifier,
) {
  val name = stringResource(developer.nameRes)
  val primaryColor = MaterialTheme.colorScheme.primary
  val containerColor = MaterialTheme.colorScheme.primaryContainer
  val secondaryAccent = MaterialTheme.colorScheme.secondary

  Card(
    modifier = modifier.fillMaxWidth().testTag("developer_card_${name.lowercase()}"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Custom stylized avatar illustration badge
      Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center,
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCircle(color = containerColor)
          drawCircle(
            color = primaryColor.copy(alpha = 0.22f),
            radius = size.minDimension * 0.42f,
            center = Offset(size.width * 0.65f, size.height * 0.3f),
          )
          drawCircle(
            color = secondaryAccent.copy(alpha = 0.2f),
            radius = size.minDimension * 0.28f,
            center = Offset(size.width * 0.25f, size.height * 0.72f),
          )
        }
        Surface(
          shape = CircleShape,
          color = primaryColor,
          modifier = Modifier.size(44.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = developer.initial,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary,
            )
          }
        }
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = developer.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
              )
              Text(
                text = developer.numberLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }

        Text(
          text = stringResource(developer.roleRes),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold,
        )

        Text(
          text = stringResource(developer.bioRes),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun AboutUsScreenPreview() {
  PalindromeCheckerTheme {
    AboutUsScreen(onBackClick = {})
  }
}
