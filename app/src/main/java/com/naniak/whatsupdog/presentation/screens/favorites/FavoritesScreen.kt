package com.naniak.whatsupdog.presentation.screens.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.presentation.components.DogImageCard
import com.naniak.whatsupdog.presentation.components.ShimmerBox
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Favorites ❤️",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            val count = when (val state = uiState) {
                is FavoritesUiState.Success -> state.favorites.size
                else -> 0
            }
            Text(
                text = if (count > 0) "$count saved good ${if (count == 1) "boy" else "boys"}" else "Your saved dogs",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Content
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                FavoritesLoadingContent()
            }
            is FavoritesUiState.Success -> {
                FavoritesSuccessContent(
                    favorites = state.favorites,
                    onRemoveFavorite = viewModel::removeFavorite
                )
            }
            is FavoritesUiState.Empty -> {
                FavoritesEmptyContent()
            }
        }
    }
}

@Composable
private fun FavoritesLoadingContent() {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(4) { index ->
            val aspectRatio = if (index % 2 == 0) 0.85f else 1.1f
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun FavoritesSuccessContent(
    favorites: List<FavoriteDog>,
    onRemoveFavorite: (FavoriteDog) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        itemsIndexed(
            items = favorites,
            key = { _, fav -> fav.id }
        ) { index, favorite ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(favorite.id) {
                delay(index.coerceAtMost(10) * 50L)
                visible = true
            }

            val aspectRatio = remember {
                when {
                    index % 4 == 0 -> 0.75f
                    index % 3 == 0 -> 1.15f
                    index % 2 == 0 -> 0.9f
                    else -> 1f
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
                exit = fadeOut(tween(200)) + scaleOut(
                    targetScale = 0.85f,
                    animationSpec = tween(200)
                )
            ) {
                DogImageCard(
                    imageUrl = favorite.imageUrl,
                    breed = favorite.breed,
                    isFavorite = true,
                    onFavoriteClick = { onRemoveFavorite(favorite) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
                    cornerRadius = 16.dp,
                    elevation = 3.dp
                )
            }
        }
    }
}

@Composable
private fun FavoritesEmptyContent() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(
            initialOffsetY = { it / 5 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💔",
                    fontSize = 72.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No favorites yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap the heart on any dog photo\nto save it here!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
