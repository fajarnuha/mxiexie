package com.fajarnuha.mccplus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.INITIALIZED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel { MainViewModel() }) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            DESTROYED, INITIALIZED, CREATED, STARTED -> Unit // Do nothing
            RESUMED -> {
                viewModel.fetch()
            }
        }
    }

    LaunchedEffect(uiState.selectedId) {
        viewModel.generateQrCode()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading(),
            onRefresh = { viewModel.fetch(true) }
        ) {
            if (!uiState.isError()) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    uiState.lastQrCodeBytes?.let { imageBitmap ->
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .size(280.dp)
                                .weight(1f)
                                .padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Text(
                        text = "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )

                    // FlowRow for chips
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        horizontalArrangement = Arrangement.SpaceAround, // Spacing between items in the same line
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between lines
                    ) {
                        uiState.access.forEach { option -> // Iterate using forEach for FlowRow
                            val targetScale = if (option.id == uiState.selectedId) 1.1f else 1.0f
                            val animatedScale by animateFloatAsState(
                                targetValue = targetScale,
                                animationSpec = tween(durationMillis = 100), // Adjust duration as needed
                                label = "chipScale"
                            )
                            FilterChip(
                                selected = (option.id == uiState.selectedId),
                                onClick = { viewModel.updateSelectedId(option.id) },
                                label = {
                                    Text(
                                        option.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                                    .graphicsLayer {
                                        scaleX = animatedScale
                                        scaleY = animatedScale
                                    },
                                elevation = FilterChipDefaults.filterChipElevation(
                                    disabledElevation = 2.dp, // Base elevation
                                    pressedElevation = 4.dp, // Elevation when pressed
                                )
                            )
                        }
                    }
                }
            } else {
                // Initial load, show the centered spinner
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading() && uiState.access.isNotEmpty()) {
                        // This space is intentionally left blank during initial load,
                        // as the PullToRefreshBox indicator is already showing.
                        // If you want a full-screen spinner for initial load,
                        // you can place it here, but it might be redundant with the refresh indicator.
                    } else {
                        // Optional: Show something if not refreshing and no content
                        Text("No content available.")
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun ImageWithChipSelectionScreenFlowRowPreview() { // Renamed preview for clarity
    MaterialTheme {
        MainScreen()
    }
}
