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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle.State.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mccplus.composeapp.generated.resources.Res
import mccplus.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import qrcode.QRCode
import qrcode.color.Colors


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // Added ExperimentalLayoutApi
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel { MainViewModel() }) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val isRefreshing = uiState is Loading

    // Remember the last successful content state to display during refresh
    var lastContent by remember { mutableStateOf<Content?>(null) }
    if (uiState is Content) {
        lastContent = uiState as Content
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when(lifecycleState) {
            DESTROYED, INITIALIZED, CREATED, STARTED -> Unit // Do nothing
            RESUMED -> {
                scope.launch {
                    viewModel.fetch()
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetch(true) }
        ) {
            // Use the last remembered content if available, otherwise it's an initial load
            val contentToShow = lastContent

            if (contentToShow != null) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val sampleChipOptions = contentToShow.access
                    val selectedChip = contentToShow.selectedId

                    var img: ImageBitmap? by remember { mutableStateOf(null) }

                    LaunchedEffect(selectedChip) {
                        val helloWorld = QRCode.ofSquares()
                            .withColor(Colors.BLACK) // Default is Colors.BLACK
                            .withSize(10) // Default is 25
                            .build(sampleChipOptions.first { it.id == selectedChip }.qr)

                        img = imageBitmapFromBytes(helloWorld.renderToBytes())
                    }
                    // Image at the top
                    if (img == null) {
                        Image(
                            painter = painterResource(resource = Res.drawable.compose_multiplatform),
                            contentDescription = "Descriptive text for the image", // Important for accessibility
                            modifier = Modifier
                                .size(120.dp)
                                .weight(1f) // Takes up available vertical space above chips
                                .padding(16.dp),
                            contentScale = ContentScale.Fit // Or ContentScale.Crop, etc.
                        )
                    } else {
                        Image(
                            bitmap = img!!,
                            contentDescription = "Descriptive text for the image", // Important for accessibility
                            modifier = Modifier
                                .size(280.dp)
                                .weight(1f) // Takes up available vertical space above chips
                                .padding(16.dp),
                            contentScale = ContentScale.Fit // Or ContentScale.Crop, etc.
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
                        sampleChipOptions.forEach { option -> // Iterate using forEach for FlowRow
                            val targetScale = if (option.id == selectedChip) 1.1f else 1.0f
                            val animatedScale by animateFloatAsState(
                                targetValue = targetScale,
                                animationSpec = tween(durationMillis = 100), // Adjust duration as needed
                                label = "chipScale"
                            )
                            FilterChip(
                                selected = (option.id == selectedChip),
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
                                modifier = Modifier.defaultMinSize(minHeight = 48.dp).graphicsLayer {
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
                    if (isRefreshing) {
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


@OptIn(ExperimentalLayoutApi::class)
@Preview()
@Composable
fun ImageWithChipSelectionScreenFlowRowPreview() { // Renamed preview for clarity
    MaterialTheme {
        MainScreen()
    }
}
