package dev.phoenix365.smartdialer.liquidglass

import androidx.compose.runtime.compositionLocalOf
import dev.phoenix365.smartdialer.liquidglass.backdrops.LayerBackdrop

/**
 * Provides the root [LayerBackdrop] that captures the full screen content,
 * so that liquid glass elements (pill nav, context menu, etc.) can sample
 * what is behind them.
 */
val LocalLiquidGlassBackdrop = compositionLocalOf<LayerBackdrop?> { null }
