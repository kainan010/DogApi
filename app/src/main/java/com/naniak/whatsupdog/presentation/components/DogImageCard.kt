package com.naniak.whatsupdog.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.naniak.whatsupdog.presentation.theme.Coral

@Composable
fun DogImageCard(
    imageUrl: String,
    modifier: Modifier = Modifier,
    breed: String? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = breed?.let { "Photo of $it dog" } ?: "Dog photo",
                loading = {
                    ShimmerBox(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp)
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐕",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay at bottom for text readability
            if (breed != null || onFavoriteClick != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
            }

            // Breed name chip
            if (breed != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    contentColor = Color.White
                ) {
                    Text(
                        text = breed.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Favorite button
            if (onFavoriteClick != null) {
                val favoriteScale by animateFloatAsState(
                    targetValue = if (isFavorite) 1f else 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "favorite_scale"
                )
                val heartColor by animateColorAsState(
                    targetValue = if (isFavorite) Coral else Color.White,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "heart_color"
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .scale(favoriteScale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onFavoriteClick
                        ),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.35f),
                    contentColor = heartColor
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        tint = heartColor
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "DogCard - Light")
@Composable
private fun DogImageCardPreview() {
    com.naniak.whatsupdog.presentation.theme.WhatsUpDogTheme {
        DogImageCard(
            imageUrl = "https://images.dog.ceo/breeds/retriever-golden/n02099601_1.jpg",
            breed = "Golden Retriever",
            isFavorite = true,
            onFavoriteClick = {},
            modifier = Modifier
                .fillMaxSize(),
            cornerRadius = 24.dp,
            elevation = 8.dp
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "DogCard - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DogImageCardDarkPreview() {
    com.naniak.whatsupdog.presentation.theme.WhatsUpDogTheme {
        DogImageCard(
            imageUrl = "https://images.dog.ceo/breeds/husky/n02110185_1.jpg",
            breed = "Husky",
            isFavorite = false,
            onFavoriteClick = {},
            modifier = Modifier
                .fillMaxSize(),
            cornerRadius = 24.dp,
            elevation = 8.dp
        )
    }
}

