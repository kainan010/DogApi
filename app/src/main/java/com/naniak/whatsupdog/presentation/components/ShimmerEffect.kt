package com.naniak.whatsupdog.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.naniak.whatsupdog.presentation.theme.ShimmerDark
import com.naniak.whatsupdog.presentation.theme.ShimmerHighlightDark
import com.naniak.whatsupdog.presentation.theme.ShimmerHighlightLight
import com.naniak.whatsupdog.presentation.theme.ShimmerLight

@Composable
fun shimmerBrush(
    baseColor: Color = ShimmerLight,
    highlightColor: Color = ShimmerHighlightLight,
    widthOfShadowBrush: Int = 500,
    durationMillis: Int = 1200
): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = widthOfShadowBrush.toFloat() + 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(translateAnimation - widthOfShadowBrush, translateAnimation - widthOfShadowBrush),
        end = Offset(translateAnimation, translateAnimation)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val isDark = !MaterialTheme.colorScheme.background.luminance().let { it > 0.5f }
    val brush = shimmerBrush(
        baseColor = if (isDark) ShimmerDark else ShimmerLight,
        highlightColor = if (isDark) ShimmerHighlightDark else ShimmerHighlightLight
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 300.dp
) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ShimmerCircle(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        modifier = modifier.size(size),
        shape = CircleShape
    )
}

@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp
) {
    ShimmerBox(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(4.dp)
    )
}

private fun Color.luminance(): Float {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Shimmer Shapes")
@Composable
private fun ShimmerPreview() {
    com.naniak.whatsupdog.presentation.theme.WhatsUpDogTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ShimmerCard(height = 200.dp)
        }
    }
}
