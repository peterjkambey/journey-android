package com.anyflow.journey.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/** Kartu putih radius besar — elemen dasar semua layar Journey. */
@Composable
fun JourneyCard(
    modifier: Modifier = Modifier,
    padding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Brand.Paper,
        border = BorderStroke(1.dp, Brand.Line),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(padding), content = content)
    }
}

/** Label kecil uppercase (TODAY'S PRACTICE, PRAYER REQUESTS, …). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Brand.Muted,
        modifier = modifier,
    )
}

@Composable
fun AvatarBadge(initials: String?, size: Dp = 38.dp, background: Color = Brand.Wine) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = (initials ?: "?").take(2).uppercase(),
            color = Color.White,
            fontSize = (size.value / 3.1f).sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun WineButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled && !loading) { onClick() },
        color = if (enabled) Brand.Wine else Brand.Line,
        shape = RoundedCornerShape(14.dp),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = text,
                    color = if (enabled) Color.White else Brand.Muted,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
fun OutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = Color.Transparent,
        border = BorderStroke(1.dp, Brand.Wine.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon?.invoke()
            Text(text = text, color = Brand.Wine, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Pill filter (All / Prayer / Journal, Circles / Global Community, …). */
@Composable
fun PillTabs(
    options: List<Pair<String, Int?>>,
    selected: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (label, count) ->
            val active = label == selected
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelect(label) },
                color = if (active) Brand.Wine else Brand.Paper,
                border = BorderStroke(1.dp, if (active) Brand.Wine else Brand.Line),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = if (count == null) label else "$label · $count",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    color = if (active) Color.White else Brand.Body,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Brand.WineSoft,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Brand.WineDeep)
            Spacer(Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

/** Foto/video aktivitas Feed — kalau media tidak bisa dimuat, tampil blok warna. */
@Composable
fun MediaBlock(
    mediaUrl: String?,
    mediaType: String?,
    modifier: Modifier = Modifier,
    height: Dp = 190.dp,
) {
    val shape = RoundedCornerShape(16.dp)

    if (mediaUrl.isNullOrBlank()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(shape)
                .background(Brand.WineSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No media", style = MaterialTheme.typography.bodySmall, color = Brand.Muted)
        }
        return
    }

    Box {
        AsyncImage(
            model = mediaUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Brand.WineSoft),
            error = ColorPainter(Brand.WineSoft),
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape),
        )
        if (mediaType == "video") {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.55f),
            ) {
                Text(
                    text = "Video",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
fun ProgressLine(fraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(Brand.WineSoft),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Brand.Wine),
        )
    }
}

@Composable
fun ComingSoonBadge() {
    Surface(shape = RoundedCornerShape(50), color = Brand.GoldSoft) {
        Text(
            text = "Coming soon",
            color = Brand.Gold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun DoneBadge(label: String = "Done") {
    Surface(shape = RoundedCornerShape(50), color = Brand.GreenSoft) {
        Text(
            text = label,
            color = Brand.Green,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun LoadingNote(text: String = "Loading…", modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(color = Brand.Wine, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Brand.Muted)
    }
}

@Composable
fun ErrorNote(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    JourneyCard(modifier = modifier) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Brand.Ink)
        if (onRetry != null) {
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = onRetry, contentPadding = PaddingValues(0.dp)) {
                Text(text = "Try again", color = Brand.Wine, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun EmptyNote(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Brand.Muted, textAlign = TextAlign.Center)
    }
}

@Composable
fun DividerLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .border(0.dp, Color.Transparent)
            .background(Brand.Line),
    )
}
