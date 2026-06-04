package com.naniak.whatsupdog.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.presentation.components.DogImageCard
import com.naniak.whatsupdog.presentation.components.ErrorView
import com.naniak.whatsupdog.presentation.components.ShimmerBox
import com.naniak.whatsupdog.presentation.theme.WhatsUpDogTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onNextDog = viewModel::goToNext,
        onPreviousDog = viewModel::goToPrevious,
        onRetry = viewModel::loadRandomDog,
        onFavoriteClick = viewModel::toggleFavorite
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onNextDog: () -> Unit,
    onPreviousDog: () -> Unit,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "What's Up Dog? 🐶",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Discover adorable dogs",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main content with animated transitions
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                },
                contentKey = { state ->
                    when (state) {
                        is HomeUiState.Loading -> "loading"
                        is HomeUiState.Success -> state.dogImage.imageUrl
                        is HomeUiState.Error -> "error"
                    }
                },
                label = "home_content"
            ) { state ->
                when (state) {
                    is HomeUiState.Loading -> {
                        HomeLoadingContent()
                    }
                    is HomeUiState.Success -> {
                        SwipeableDogCard(
                            state = state,
                            onFavoriteClick = onFavoriteClick,
                            onSwipeLeft = onNextDog,
                            onSwipeRight = onPreviousDog
                        )
                    }
                    is HomeUiState.Error -> {
                        ErrorView(
                            message = state.message,
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Swipe hint
            if (uiState is HomeUiState.Success) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if ((uiState as HomeUiState.Success).canGoBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "← Swipe to explore →",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Dog Button
            NextDogButton(
                onClick = onNextDog,
                isLoading = uiState is HomeUiState.Loading
            )
        }
    }
}

@Composable
private fun SwipeableDogCard(
    state: HomeUiState.Success,
    onFavoriteClick: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val swipeThreshold = screenWidthPx * 0.25f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = (offsetX.value / screenWidthPx * 15f).coerceIn(-12f, 12f)
                val scale = 1f - (kotlin.math.abs(offsetX.value) / screenWidthPx * 0.1f)
                scaleX = scale.coerceAtLeast(0.9f)
                scaleY = scale.coerceAtLeast(0.9f)
            }
            .pointerInput(state.dogImage.imageUrl) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                // Swiped right → go to previous
                                offsetX.value > swipeThreshold && state.canGoBack -> {
                                    offsetX.animateTo(
                                        screenWidthPx,
                                        animationSpec = tween(250)
                                    )
                                    onSwipeRight()
                                    offsetX.snapTo(-screenWidthPx)
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                                // Swiped left → go to next
                                offsetX.value < -swipeThreshold -> {
                                    offsetX.animateTo(
                                        -screenWidthPx,
                                        animationSpec = tween(250)
                                    )
                                    onSwipeLeft()
                                    offsetX.snapTo(screenWidthPx)
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                                // Snap back
                                else -> {
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                        }
                    }
                )
            }
    ) {
        DogImageCard(
            imageUrl = state.dogImage.imageUrl,
            breed = state.dogImage.breed,
            isFavorite = state.isFavorite,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 24.dp,
            elevation = 8.dp
        )

        // Swipe direction indicators
        val alpha = (kotlin.math.abs(offsetX.value) / swipeThreshold).coerceIn(0f, 1f)
        if (offsetX.value > 20f && state.canGoBack) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha * 0.7f)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "◀",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
        if (offsetX.value < -20f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha * 0.7f)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "▶",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeLoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun NextDogButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        enabled = !isLoading
    ) {
        Text(
            text = if (isLoading) "Finding a good boy..." else "Next Dog 🐾",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, name = "Home - Success")
@Composable
private fun HomeScreenSuccessPreview() {
    WhatsUpDogTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                dogImage = DogImage(
                    imageUrl = "https://images.dog.ceo/breeds/retriever-golden/n02099601_1.jpg",
                    breed = "Golden Retriever"
                ),
                isFavorite = false,
                canGoBack = true
            ),
            onNextDog = {},
            onPreviousDog = {},
            onRetry = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Loading")
@Composable
private fun HomeScreenLoadingPreview() {
    WhatsUpDogTheme {
        HomeContent(
            uiState = HomeUiState.Loading,
            onNextDog = {},
            onPreviousDog = {},
            onRetry = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Error")
@Composable
private fun HomeScreenErrorPreview() {
    WhatsUpDogTheme {
        HomeContent(
            uiState = HomeUiState.Error(message = "Network connection failed"),
            onNextDog = {},
            onPreviousDog = {},
            onRetry = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    WhatsUpDogTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                dogImage = DogImage(
                    imageUrl = "https://images.dog.ceo/breeds/husky/n02110185_1.jpg",
                    breed = "Husky"
                ),
                isFavorite = true,
                canGoBack = false
            ),
            onNextDog = {},
            onPreviousDog = {},
            onRetry = {},
            onFavoriteClick = {}
        )
    }
}
