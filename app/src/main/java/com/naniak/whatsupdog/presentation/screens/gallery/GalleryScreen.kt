package com.naniak.whatsupdog.presentation.screens.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naniak.whatsupdog.presentation.components.DogImageCard
import com.naniak.whatsupdog.presentation.components.ErrorView
import com.naniak.whatsupdog.presentation.components.ShimmerBox
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    breed: String,
    onBackClick: () -> Unit,
    viewModel: GalleryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            GalleryTopBar(
                breed = breed,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is GalleryUiState.Loading -> {
                    GalleryLoadingContent()
                }
                is GalleryUiState.Success -> {
                    GallerySuccessContent(
                        state = state,
                        onFavoriteClick = viewModel::toggleFavorite
                    )
                }
                is GalleryUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = viewModel::loadImages,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryTopBar(
    breed: String,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = breed.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun GalleryLoadingContent() {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(6) { index ->
            val aspectRatio = if (index % 3 == 0) 0.75f else if (index % 2 == 0) 1.2f else 1f
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
private fun GallerySuccessContent(
    state: GalleryUiState.Success,
    onFavoriteClick: (com.naniak.whatsupdog.domain.model.DogImage) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        itemsIndexed(
            items = state.images,
            key = { _, image -> image.imageUrl }
        ) { index, image ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(image.imageUrl) {
                delay(index.coerceAtMost(12) * 50L)
                visible = true
            }

            val aspectRatio = remember {
                when {
                    index % 5 == 0 -> 0.7f
                    index % 3 == 0 -> 1.2f
                    index % 2 == 0 -> 0.85f
                    else -> 1f
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400)
                )
            ) {
                DogImageCard(
                    imageUrl = image.imageUrl,
                    breed = null,
                    isFavorite = state.favoriteStatuses[image.imageUrl] == true,
                    onFavoriteClick = { onFavoriteClick(image) },
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
