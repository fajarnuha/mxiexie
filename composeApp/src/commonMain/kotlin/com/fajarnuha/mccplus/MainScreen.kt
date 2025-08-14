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
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel { MainViewModel() }) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED, Lifecycle.State.INITIALIZED, Lifecycle.State.CREATED, Lifecycle.State.STARTED -> Unit // Do nothing
            Lifecycle.State.RESUMED -> {
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
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading(),
                onRefresh = { viewModel.fetch(true) },
                modifier = Modifier.fillMaxSize()
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
            IconButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(
                    imageVector = Logout,
                    contentDescription = "Logout"
                )
            }
        }
    }
}

val Logout: ImageVector
    get() {
        if (_logout != null) {
            return _logout!!
        }
        _logout = materialIcon(name = "AutoMirrored.Filled.Logout", autoMirror = true) {
            materialPath {
                moveTo(17.0f, 7.0f)
                lineToRelative(-1.41f, 1.41f)
                lineTo(18.17f, 11.0f)
                horizontalLineTo(8.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(10.17f)
                lineToRelative(-2.58f, 2.58f)
                lineTo(17.0f, 17.0f)
                lineToRelative(5.0f, -5.0f)
                close()
                moveTo(4.0f, 5.0f)
                horizontalLineToRelative(8.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(5.0f)
                close()
            }
        }
        return _logout!!
    }

private var _logout: ImageVector? = null

@Preview()
@Composable
fun ImageWithChipSelectionScreenFlowRowPreview() { // Renamed preview for clarity
    MaterialTheme {
        MainScreen()
    }
}
