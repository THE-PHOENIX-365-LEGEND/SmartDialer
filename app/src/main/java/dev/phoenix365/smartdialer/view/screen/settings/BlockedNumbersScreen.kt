package dev.phoenix365.smartdialer.view.screen.settings

import android.content.Context
import android.telecom.TelecomManager
import android.view.Surface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.rounded.DisabledVisible
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.phoenix365.smartdialer.R
import dev.phoenix365.smartdialer.controller.util.PreferenceManager
import dev.phoenix365.smartdialer.controller.util.getBlockedNumbers
import dev.phoenix365.smartdialer.view.components.NavigationIcon
import dev.phoenix365.smartdialer.view.components.SmartAnimatedSection
import dev.phoenix365.smartdialer.view.components.SmartExpressiveCard
import dev.phoenix365.smartdialer.view.components.SmartListItem
import dev.phoenix365.smartdialer.view.components.SmartSelectListItem
import dev.phoenix365.smartdialer.view.theme.customColors
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.phoenix365.smartdialer.view.components.SmartDialog
import dev.phoenix365.smartdialer.view.components.Title
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun BlockedNumbersScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val prefs = koinInject<PreferenceManager>()
    val settingsState by prefs.settingsChanged.collectAsStateWithLifecycle()
    
    var blockMethod by remember(settingsState) { mutableStateOf(prefs.getInt(PreferenceManager.KEY_BLOCK_METHOD, 0)) }
    var logVisibility by remember(settingsState) { mutableStateOf(prefs.getInt(PreferenceManager.KEY_BLOCK_LOG_VISIBILITY, 0)) }
    var blockNotification by remember(settingsState) { mutableStateOf(prefs.getBoolean(PreferenceManager.KEY_BLOCK_NOTIFICATION, true)) }

    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(350),
        label = "spamAlpha"
    )
    LaunchedEffect(Unit) { visible = true }

    val lifecycleOwner = LocalLifecycleOwner.current
    var blockedNumbersSize by remember { mutableStateOf(context.getBlockedNumbers().size.toString()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                blockedNumbersSize = context.getBlockedNumbers().size.toString()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val rotation =
        (context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager).defaultDisplay.rotation
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val isRotation90 = rotation == if (isLtr) Surface.ROTATION_90 else Surface.ROTATION_270
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.systemBars.only(
                    if (isRotation90) WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    else WindowInsetsSides.Top
                ),
                title = { Title(stringResource(R.string.manage_blocked)) },
                navigationIcon = {
                    NavigationIcon(onClick = { navigator.navigateUp() })
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
//                .padding(padding)
                .padding(
                    top = padding.calculateTopPadding(),
                    start = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp
                )
                .alpha(screenAlpha),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SmartAnimatedSection(delayMs = 60L) {
                    var showAboutNotesDialog by remember { mutableStateOf(false) }
                    Column {
                        SettingsSectionLabel(stringResource(R.string.system_settings))
                        SmartExpressiveCard {
                            SmartListItem(
                                headline = stringResource(R.string.blocked_numbers),
                                supporting = stringResource(R.string.blocked_size, blockedNumbersSize),
                                leadingIcon = Icons.AutoMirrored.Rounded.List,
                                iconContainerColor = MaterialTheme.colorScheme.customColors.colorDarkOrange,
                                iconBgContainerColor = MaterialTheme.colorScheme.customColors.colorOrange,
                                preTrailingIcon = Icons.AutoMirrored.Rounded.Help,
                                modifierPreTrailingIcon = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp)
                                    .combinedClickable(
                                        interactionSource = null,
                                        indication = ripple(
                                            bounded = false,
                                            radius = 24.dp
                                        ),
                                        onClick = { showAboutNotesDialog = true }),
                                trailingIcon = Icons.Default.ChevronRight,
                                onClick = {
                                    val telecomManager =
                                        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                                    try {
                                        val intent =
                                            telecomManager.createManageBlockedNumbersIntent()
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        // Fallback if the intent is not supported on some older/custom versions
                                    }
                                }
                            )
                        }

                        if (showAboutNotesDialog) {
                            SmartDialog(
                                onDismissRequest = { showAboutNotesDialog = false },
                                title = stringResource(R.string.blocked_numbers),
                                icon = Icons.AutoMirrored.Rounded.Help,
                                iconContainerColor = MaterialTheme.colorScheme.customColors.colorDarkAmber,
                                iconBgContainerColor = MaterialTheme.colorScheme.customColors.colorAmber,
                                confirmButton = {
                                    TextButton(onClick = {
                                        showAboutNotesDialog = false
                                    }) {
                                        Text(
                                            stringResource(R.string.close),
                                            textAlign = TextAlign.End,
                                        )
                                    }
                                },
                            ) {
                                Text(stringResource(R.string.blocked_numbers_system_info))
                            }
                        }
                    }
                }
            }

            // Our app's rules do not apply to blocked numbers on the system list; such calls are blocked by the system beforehand.
//            item {
//                SmartAnimatedSection(delayMs = 60L) {
//                    SmartExpressiveCard {
//                        SmartSwitchListItem(
//                            headline = stringResource(R.string.blocking_notifications),
//                            supporting = stringResource(R.string.blocking_notifications_subtitle),
//                            leadingIcon = Icons.Rounded.NotificationsPaused,
//                            iconContainerColor = MaterialTheme.colorScheme.customColors.colorDarkAmber,
//                            iconBgContainerColor = MaterialTheme.colorScheme.customColors.colorAmber,
//                            checked = blockNotification,
//                            onCheckedChange = {
//                                blockNotification = it
//                                prefs.setBoolean(PreferenceManager.KEY_BLOCK_NOTIFICATION, it)
//                            }
//                        )
//                    }
//                }
//            }

            item {
                SmartAnimatedSection(delayMs = 120L) {
                    SmartExpressiveCard {
                        // Our app's rules do not apply to blocked numbers on the system list; such calls are blocked by the system beforehand.
//                        SmartSelectListItem(
//                            headline = stringResource(R.string.block_method),
//                            supporting = stringResource(R.string.block_method_subtitle),
//                            leadingIcon = if (blockMethod == 0) Icons.Rounded.CallEnd else Icons.Rounded.MusicOff,
//                            iconContainerColor = MaterialTheme.colorScheme.customColors.colorDarkRed,
//                            iconBgContainerColor = MaterialTheme.colorScheme.customColors.colorRed,
//                            options = listOf(
//                                stringResource(R.string.decline_automatically) to 0,
//                                stringResource(R.string.ring_silently) to 1
//                            ),
//                            selectedValue = blockMethod,
//                            onValueChange = {
//                                blockMethod = it
//                                prefs.setInt(PreferenceManager.KEY_BLOCK_METHOD, it)
//                            }
//                        )
                        SmartSelectListItem(
                            headline = stringResource(R.string.log_visibility),
                            supporting = stringResource(R.string.log_visibility_subtitle),
                            leadingIcon = if (logVisibility == 0) Icons.Rounded.DisabledVisible else Icons.Rounded.Visibility,
                            iconContainerColor = MaterialTheme.colorScheme.customColors.colorDarkRed,
                            iconBgContainerColor = MaterialTheme.colorScheme.customColors.colorRed,
                            options = listOf(
                                stringResource(R.string.hide_from_logs) to 0,
                                stringResource(R.string.show_in_logs) to 1
                            ),
                            selectedValue = logVisibility,
                            onValueChange = {
                                logVisibility = it
                                prefs.setInt(PreferenceManager.KEY_BLOCK_LOG_VISIBILITY, it)
                            }
                        )
                    }
                }
            }

            item { SettingsBottomPadding() }
        }
    }
}
