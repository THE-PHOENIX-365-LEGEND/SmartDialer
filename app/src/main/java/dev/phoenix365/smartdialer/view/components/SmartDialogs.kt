package dev.phoenix365.smartdialer.view.components

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.phoenix365.smartdialer.R
import dev.phoenix365.smartdialer.cardCornerExtraSmall
import dev.phoenix365.smartdialer.cardSpacedBy
import dev.phoenix365.smartdialer.controller.util.PreferenceManager
import dev.phoenix365.smartdialer.liquidglass.LocalLiquidGlassBackdrop
import dev.phoenix365.smartdialer.liquidglass.drawBackdrop
import dev.phoenix365.smartdialer.liquidglass.drawPlainBackdrop
import dev.phoenix365.smartdialer.liquidglass.effects.blur
import dev.phoenix365.smartdialer.liquidglass.effects.colorControls
import dev.phoenix365.smartdialer.liquidglass.effects.lens
import dev.phoenix365.smartdialer.liquidglass.highlight.Highlight
import dev.phoenix365.smartdialer.view.theme.LocalCardRoundness
import dev.phoenix365.smartdialer.view.theme.MyColors.cardColor
import dev.phoenix365.smartdialer.view.theme.SmartMotion
import dev.phoenix365.smartdialer.view.theme.SmartShapeDefaults
import dev.phoenix365.smartdialer.view.theme.smartCornerDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

val LocalSmartDialogDismiss = compositionLocalOf<((() -> Unit) -> Unit)?> { null }

@Immutable
data class SmartDialogAction(
    val label: String,
    val onClick: () -> Unit,
    val destructive: Boolean = false,
    val enabled: Boolean = true,
    val dismissOnClick: Boolean = true
)

private val DialogMaxWidth = 460.dp
private val DialogActionHeight = 48.dp
private val DialogHeaderTileSize = 52.dp
private val DialogHeaderIconSize = 26.dp
private const val ScrimAlpha = 0.38f
private const val DialogEnterScale = 0.72f
private const val DialogExitScale = 0.80f
private const val DialogExitDurationMs = 180

@Composable
fun ApplyDialogBlurBehind() {
    val prefs = koinInject<PreferenceManager>()
    val isBlurEnabled = prefs.isUiBlurEnabled()
    val view = LocalView.current

    DisposableEffect(isBlurEnabled, view) {
        val window = (view.parent as? DialogWindowProvider)?.window
            ?: (view.context as? Activity)?.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && window != null) {
            if (isBlurEnabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                val lp = window.attributes
                lp.blurBehindRadius = 80
                window.attributes = lp
                window.setDimAmount(0.25f)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                val lp = window.attributes
                lp.blurBehindRadius = 0
                window.attributes = lp
                window.setDimAmount(0f)
            }
        }
        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            }
        }
    }
}

@Composable
fun SmartDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    iconContainerColor: Color? = null,
    iconBgContainerColor: Color? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    supportingText: String? = null,
    confirmAction: SmartDialogAction? = null,
    dismissAction: SmartDialogAction? = null,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    showCloseButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val prefs = koinInject<PreferenceManager>()
    val isBlurEnabled = prefs.isUiBlurEnabled()

    var isVisible by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val dismissWithAnimation: (() -> Unit) -> Unit = { action ->
        if (!isDismissing) {
            isDismissing = true
            isVisible = false
            coroutineScope.launch {
                delay(DialogExitDurationMs.toLong())
                action()
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else if (isDismissing) DialogExitScale else DialogEnterScale,
        animationSpec = if (isVisible) {
            spring(
                dampingRatio = 0.68f,
                stiffness = Spring.StiffnessMediumLow
            )
        } else {
            tween(
                durationMillis = DialogExitDurationMs,
                easing = FastOutLinearInEasing
            )
        },
        label = "SmartDialogScale"
    )
    val fade by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = if (isVisible) {
            tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        } else {
            tween(durationMillis = 150, easing = LinearEasing)
        },
        label = "SmartDialogFade"
    )

    Dialog(
        onDismissRequest = {
            dismissWithAnimation { onDismissRequest() }
        },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        CompositionLocalProvider(LocalSmartDialogDismiss provides dismissWithAnimation) {
            ApplyDialogBlurBehind()

            val scrimColor = MaterialTheme.colorScheme.scrim
            val scrimInteraction = remember { MutableInteractionSource() }
            val dismissLabel = stringResource(R.string.close)
            val currentScrimAlpha = if (isBlurEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 0.22f else ScrimAlpha

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind { drawRect(color = scrimColor, alpha = currentScrimAlpha * fade) }
                    .then(
                        if (dismissOnClickOutside) {
                            Modifier.clickable(
                                interactionSource = scrimInteraction,
                                indication = null,
                                onClickLabel = dismissLabel,
                                onClick = { dismissWithAnimation { onDismissRequest() } }
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                val destructive = confirmAction?.destructive == true
                val headerContainer = iconBgContainerColor ?: if (destructive) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
                val headerContent = iconContainerColor ?: if (destructive) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }

                val roundness = LocalCardRoundness.current
                val dialogCornerDp = smartCornerDp(SmartShapeDefaults.BaseExtraLarge, roundness)

                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .widthIn(max = DialogMaxWidth)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = fade
                        }
                        .pointerInput(Unit) { detectTapGestures { } }
                        .animateContentSize(animationSpec = SmartMotion.spatialDefault<IntSize>())
                        .semantics { if (title != null) paneTitle = title },
                    shape = RoundedCornerShape(dialogCornerDp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (title != null || icon != null || showCloseButton) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (icon != null) {
                                        Surface(
                                            modifier = Modifier.size(DialogHeaderTileSize),
                                            shape = RoundedCornerShape(16.dp),
                                            color = headerContainer,
                                            contentColor = headerContent
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(DialogHeaderIconSize)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                    if (title != null) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                if (showCloseButton && dismissOnBackPress) {
                                    SmartIconButton(
                                        onClick = { dismissWithAnimation { onDismissRequest() } },
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        }

                        if (supportingText != null) {
                            Text(
                                text = supportingText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, false)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(cardSpacedBy)
                        ) {
                            content()
                        }

                        if (confirmAction != null || dismissAction != null || confirmButton != null || dismissButton != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (dismissButton != null) {
                                    dismissButton()
                                } else if (dismissAction != null) {
                                    SmartDialogActionButton(
                                        action = dismissAction,
                                        onDismiss = { dismissWithAnimation { onDismissRequest() } }
                                    )
                                }
                                if (confirmButton != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    confirmButton()
                                } else if (confirmAction != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    SmartDialogActionButton(
                                        action = confirmAction,
                                        onDismiss = { dismissWithAnimation { onDismissRequest() } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartDialogActionButton(
    action: SmartDialogAction,
    onDismiss: () -> Unit
) {
    val buttonShape = RoundedCornerShape(14.dp)
    val handleClick: () -> Unit = {
        action.onClick()
        if (action.dismissOnClick) {
            onDismiss()
        }
    }

    if (!action.destructive) {
        Button(
            onClick = handleClick,
            shape = buttonShape,
            modifier = Modifier.height(DialogActionHeight),
            enabled = action.enabled,
            colors = ButtonDefaults.buttonColors(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
        ) {
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    } else {
        FilledTonalButton(
            onClick = handleClick,
            shape = buttonShape,
            modifier = Modifier.height(DialogActionHeight),
            enabled = action.enabled,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
        ) {
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SmartConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String = stringResource(R.string.confirm),
    dismissLabel: String? = null,
    icon: ImageVector? = null,
    isDestructive: Boolean = false
) {
    SmartDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        icon = icon,
        confirmAction = SmartDialogAction(
            label = confirmLabel,
            onClick = onConfirm,
            destructive = isDestructive
        )
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun <T> SmartSelectionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    itemSupporting: ((T) -> String?)? = null,
    icon: ImageVector? = null,
    iconContainerColor: Color? = null,
    iconBgContainerColor: Color? = null,
    itemIcon: @Composable ((T) -> ImageVector)? = null,
    isSelected: (T) -> Boolean = { false },
    isSettings: Boolean = true,
) {
    SmartDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        icon = icon,
        iconContainerColor = iconContainerColor,
        iconBgContainerColor = iconBgContainerColor,
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
            }
        }
    ) {
        SmartExpressiveCard(
            modifier = Modifier
                .fillMaxWidth(),
            shape = if (isSettings) RoundedCornerShape(0.dp) else MaterialTheme.shapes.large
        ) {
            val inner = if (isSettings) 10.dp else cardCornerExtraSmall
            items.forEach { item ->
                val selected = isSelected(item)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val cornerRadius by animateDpAsState(
                    if (selected || isPressed) 40.dp else inner,
                    spring(stiffness = Spring.StiffnessMediumLow),
                    label = "ButtonShapeAnimation"
                )
                Surface(
                    onClick = {
                        onItemSelected(item)
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(cornerRadius),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else cardColor,
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = interactionSource
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = if (itemIcon != null) 8.dp else 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (itemIcon != null) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        itemIcon(item),
                                        null,
                                        tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                itemLabel(item),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (itemSupporting != null) {
                                val supporting = itemSupporting(item)
                                if (!supporting.isNullOrBlank()) {
                                    Text(
                                        supporting,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.8f
                                        ) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val prefs = koinInject<PreferenceManager>()
    val settingsVer by prefs.settingsChanged.collectAsStateWithLifecycle()
    val liquidGlass = remember(settingsVer) { prefs.getBoolean(PreferenceManager.KEY_LIQUID_GLASS, false) }
    val lgDropdownMenu = remember(settingsVer) { prefs.getBoolean(PreferenceManager.KEY_LG_DROPDOWN_MENU, true) }
    val blurEffects = remember(settingsVer) { prefs.getBoolean(PreferenceManager.KEY_BLUR_EFFECTS, false) }
    val blurDropdownMenu = remember(settingsVer) { prefs.getBoolean(PreferenceManager.KEY_BLUR_DROPDOWN_MENU, true) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded) showContent = true
    }

    if (showContent) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val dimAlpha by animateFloatAsState(
                targetValue = if (expanded) 0.45f else 0f,
                animationSpec = tween(320),
                label = "dimAlpha",
                finishedListener = { if (!expanded) showContent = false }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = scaleIn(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        ),
                        initialScale = 0.75f,
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    ) + fadeIn(tween(280)),
                    exit = scaleOut(
                        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                        targetScale = 0.85f,
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    ) + fadeOut(tween(200))
                ) {
                    val menuShape = RoundedCornerShape(35.dp)
                    val globalBackdrop = LocalLiquidGlassBackdrop.current
                    val useLgDropdown = liquidGlass && lgDropdownMenu && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && globalBackdrop != null
                    val useBlurDropdown = blurEffects && blurDropdownMenu && !useLgDropdown

                    Box(
                        modifier = modifier
                            .width(260.dp)
                            .then(
                                if (useLgDropdown) Modifier
                                else Modifier.shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = Color.Black.copy(alpha = 0.28f),
                                    ambientColor = Color.Black.copy(alpha = 0.12f)
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                    ) {
                        val dropdownShape = if (useLgDropdown) menuShape else RoundedCornerShape(24.dp)
                        if (useLgDropdown) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBackdrop(
                                        backdrop = globalBackdrop,
                                        shape = { menuShape },
                                        effects = {
                                            val d = density
                                            colorControls(brightness = -0.13f, saturation = 1.4f)
                                            blur(6f * d)
                                            lens(
                                                refractionHeight = 40f * d,
                                                refractionAmount = 248f * d
                                            )
                                        },
                                        highlight = { Highlight.Plain }
                                    ),
                                shape = menuShape,
                                color = Color.Black.copy(alpha = 0.25f),
                                tonalElevation = 0.dp
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
                            }
                        } else if (useBlurDropdown && globalBackdrop != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawPlainBackdrop(
                                        backdrop = globalBackdrop,
                                        shape = { dropdownShape },
                                        effects = { blur(30f * density) }
                                    ),
                                shape = dropdownShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.72f),
                                tonalElevation = 0.dp
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = dropdownShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                tonalElevation = 0.dp
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    isDestructive: Boolean = false
) {
    val prefs2 = koinInject<PreferenceManager>()
    val settingsVer2 by prefs2.settingsChanged.collectAsStateWithLifecycle()
    val liquidGlass2 = remember(settingsVer2) { prefs2.getBoolean(PreferenceManager.KEY_LIQUID_GLASS, false) }
    val lgDropdown   = remember(settingsVer2) { prefs2.getBoolean(PreferenceManager.KEY_LG_DROPDOWN_MENU, true) }

    val textColor  = when {
        isDestructive          -> MaterialTheme.colorScheme.error
        liquidGlass2 && lgDropdown -> Color.White
        else                   -> MaterialTheme.colorScheme.onSurface
    }
    val tintColor  = if (isDestructive) MaterialTheme.colorScheme.error else iconTint
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "rMenuItemScale"
    )

    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = tintColor.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
