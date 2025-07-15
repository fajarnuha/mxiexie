package com.fajarnuha.mccplus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(Unit) {
        scope.launch {
            delay(1000)
            viewModel.fetch()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is Content -> {
                    val sampleImagePainter =
                        painterResource(resource = Res.drawable.compose_multiplatform)
                    val sampleChipOptions = (uiState as Content).access
                    val selectedChip = (uiState as Content).selectedId

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
                            painter = sampleImagePainter,
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


                    // Chip selection at the bottom
                    Text(
                        text = "Choose access:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )

                    // FlowRow for chips
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround, // Spacing between items in the same line
                        verticalArrangement = Arrangement.spacedBy(2.dp) // Spacing between lines
                    ) {
                        sampleChipOptions.forEach { option -> // Iterate using forEach for FlowRow
                            FilterChip(
                                selected = (option.id == selectedChip),
                                onClick = { viewModel.updateSelectedId(option.id) },
                                label = { Text(option.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold) },
                                shape = MaterialTheme.shapes.medium,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                            )
                        }
                    }
                }

                Loading -> CircularProgressIndicator()
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