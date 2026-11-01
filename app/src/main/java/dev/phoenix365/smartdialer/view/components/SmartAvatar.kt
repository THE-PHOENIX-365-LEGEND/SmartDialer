package dev.phoenix365.smartdialer.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.phoenix365.smartdialer.R
import dev.phoenix365.smartdialer.controller.util.PreferenceManager
import dev.phoenix365.smartdialer.controller.util.isEmoji
import dev.phoenix365.smartdialer.controller.util.isLetter
import org.koin.compose.koinInject
import kotlin.math.abs

private val avatarColors = listOf(
    Color(0xFF00D0EA), Color(0xFFFF5B55), Color(0xFFFFC600), Color(0xFFBC56FF),
    Color(0xFFFF891D), Color(0xFF26BC6D), Color(0xFFFF54BB)
)

@Composable
fun SmartAvatar(
    name: String,
    photoUri: String? = null,
    icon: ImageVector? = null,
    iconContainerColor: Color? = null,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    val prefs = koinInject<PreferenceManager>()
    @Suppress("UNUSED_VARIABLE")
    val settingsState by prefs.settingsChanged.collectAsStateWithLifecycle()

    val showPicture     = remember(settingsState) { prefs.getBoolean(PreferenceManager.KEY_SHOW_PICTURE, true) }
    val showFirstLetter = remember(settingsState) { prefs.getBoolean(PreferenceManager.KEY_SHOW_FIRST_LETTER, true) }
    val colorfulAvatars = remember(settingsState) { prefs.getBoolean(PreferenceManager.KEY_COLORFUL_AVATARS, true) }
    val secondaryColorAvatars by remember(settingsState) { mutableStateOf(prefs.getBoolean(PreferenceManager.KEY_SECONDARY_COLOR_AVATARS, false)) }
    val googleContactColorAvatars by remember(settingsState) { mutableStateOf(prefs.getBoolean(PreferenceManager.KEY_GOOGLE_CONTACTS_AVATARS, false)) }
    val avatarFrame = remember(settingsState) { prefs.getBoolean(PreferenceManager.KEY_AVATAR_FRAME, false) }

    val hasName  = name.trim().isNotEmpty()
    val isLetter  = name.trim().take(1).isLetter() || name.take(2).isEmoji()
    val colorKey = if (hasName) name else "unknown_caller"

    val (backgroundColor, contentColor) = when {
        iconContainerColor != null -> iconContainerColor.copy(alpha = 0.18f) to iconContainerColor
        colorfulAvatars -> avatarColors[abs(colorKey.hashCode()) % avatarColors.size] to if (avatarFrame) MaterialTheme.colorScheme.onSurface else Color.White
        googleContactColorAvatars -> Color(0xFFFFAFAB) to Color(0xFF690A08)
        secondaryColorAvatars -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    BoxWithConstraints(
        modifier = modifier
            .then(
                if (avatarFrame) modifier
                    .drawBehind {
                        val borderWidth = size.width * 0.09f
                        drawOutline(
                            outline = shape.createOutline(size, layoutDirection, this),
                            color = contentColor,
                            style = Stroke(width = borderWidth)
                        )
                    }
                else modifier
            )
            .background(backgroundColor, shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        val letterFontSize  = (maxWidth.value * 0.60f).coerceIn(14f, 72f).sp
        val iconSize        = (maxWidth.value * 0.65f).coerceIn(16f, 92f).dp
        val placeholderSize = (maxWidth.value * 0.85f).dp

        when {
            showPicture && !photoUri.isNullOrEmpty() -> {
                AsyncImage(
                    model = photoUri,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            icon != null -> {
                Icon(imageVector = icon, contentDescription = name, tint = contentColor, modifier = Modifier.size(iconSize))
            }
            showFirstLetter && hasName && isLetter -> {
                val emoji = name.take(2)
                val letter = if (emoji.isEmoji()) emoji else name.trim().take(1).uppercase()
                Text(
                    text = letter,
                    fontSize = letterFontSize,
                    color = contentColor,
                    lineHeight = letterFontSize
                )
            }
            else -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = name, tint = contentColor,
                    modifier = Modifier.size(placeholderSize)
                )
            }
        }
    }
}
