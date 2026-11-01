package dev.phoenix365.smartdialer

import android.app.Application
import dev.phoenix365.smartdialer.controller.util.DialpadT9
import dev.phoenix365.smartdialer.controller.util.PreferenceManager
import dev.phoenix365.smartdialer.view.screen.settings.KEY_SELECTED_APP_ICON
import dev.phoenix365.smartdialer.view.screen.settings.applyIcon
import dev.phoenix365.smartdialer.view.screen.settings.buildIcons
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SmartDialerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SmartDialerApp)
            modules(appModule)
        }
        restoreSavedAppIcon()
        DialpadT9.init(this)
    }

    private fun restoreSavedAppIcon() {
        try {
            val prefs = PreferenceManager(this)
            val savedKey = prefs.getString(KEY_SELECTED_APP_ICON, "default") ?: "default"
            val icons = buildIcons(this)
            val entry = icons.find { it.key == savedKey } ?: icons.first()
            applyIcon(this, prefs, entry)
        } catch (_: Exception) {}
    }
}
