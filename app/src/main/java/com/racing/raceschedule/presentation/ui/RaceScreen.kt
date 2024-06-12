package com.racing.raceschedule.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.racing.raceschedule.presentation.viewmodel.RaceViewModel
import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.presentation.FilterCategories
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import com.racing.raceschedule.presentation.ui.theme.spacing

@Composable
fun RaceScreen() {
    val viewModel: RaceViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RaceFilter(viewModel)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.errorMessage != null -> ErrorMessage(uiState.errorMessage)
                uiState.races.isEmpty() -> ErrorMessage("No Races Available")
                else -> RaceList(uiState.races)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceFilter(viewModel: RaceViewModel) {
    var selectedFilter by remember { mutableStateOf<String?>(FilterCategories.ALL) }

    Column {
        Text("Filter Races By:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            FilterChip(
                selected = selectedFilter == FilterCategories.ALL,
                onClick = { selectedFilter = FilterCategories.ALL; viewModel.filterRaces(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == FilterCategories.HORSE,
                onClick = { selectedFilter = FilterCategories.HORSE; viewModel.filterRaces(
                    FilterCategories.HORSE) },
                label = { Text("Horse") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == FilterCategories.HARNESS,
                onClick = { selectedFilter = FilterCategories.HARNESS; viewModel.filterRaces(
                    FilterCategories.HARNESS) },
                label = { Text("Harness") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == FilterCategories.GREYHOUND,
                onClick = { selectedFilter = FilterCategories.GREYHOUND; viewModel.filterRaces(
                    FilterCategories.GREYHOUND) },
                label = { Text("Greyhound") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun RaceList(races: List<Race>) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = Modifier.fillMaxSize()
    ) {
        items(races) { race ->
            RaceItem(race)
        }
    }
}

@Composable
fun RaceItem(race: Race) {
    var remainingTime by remember { mutableStateOf(calculateRemainingTime(race.advertisedStart)) }

    LaunchedEffect(key1 = race.advertisedStart) {
        while (true) {
            remainingTime = calculateRemainingTime(race.advertisedStart)
            delay(1000)
            if (remainingTime == "00:00") break
        }
    }

    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.medium),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(MaterialTheme.spacing.medium),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large)
        ) {
            Text(
                text = race.meetingName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = "Race: ${race.raceNumber}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = "Starts in: $remainingTime",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "Unknown Error",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun calculateRemainingTime(advertisedStart: Long): String {
    val currentTime = System.currentTimeMillis() / 1000
    val remainingTimeInSeconds = advertisedStart - currentTime
    val minutes = TimeUnit.SECONDS.toMinutes(remainingTimeInSeconds.coerceAtLeast(0))
    val seconds = remainingTimeInSeconds.coerceAtLeast(0) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}