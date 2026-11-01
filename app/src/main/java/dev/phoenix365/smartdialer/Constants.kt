package dev.phoenix365.smartdialer

import androidx.compose.ui.unit.dp

val APP_VERSION = BuildConfig.VERSION_NAME
val GITHUB_DEV = "https://github.com/Phoenix365"
val GITHUB_URL = "https://github.com/Phoenix365/SmartDialer"
val SITE_URL = "https://www.phoenix365.dev"
val GP_URL = "https://play.google.com/store/apps/details?id=dev.phoenix365.smartdialer"
val GP_DEV_URL = "https://play.google.com/store/apps/dev?id=6544655677105796673"
val GITHUB_RELEASES_URL = "https://github.com/Phoenix365/SmartDialer/releases"
val GITHUB_API_RELEASES = "https://api.github.com/repos/Phoenix365/SmartDialer/releases/latest"
val GITHUB_API_RELEASES_LIST = "https://api.github.com/repos/Phoenix365/SmartDialer/releases"
val PRIVACY_POLICY = "https://www.phoenix365.dev/privacy-policy/privacy-policy-smart-dialer"

// Phoenix365
const val device_only = "local"
const val private_only = "private"

val bottomBarHeight = 64.dp

val cardCornerExtraLarge = 28.dp //MaterialTheme.shapes.extraLarge
val cardCornerLarge = 16.dp //MaterialTheme.shapes.large
val cardCornerMedium = 12.dp //MaterialTheme.shapes.medium
val cardCornerSmall = 8.dp //MaterialTheme.shapes.small
val cardCornerExtraSmall = 4.dp //MaterialTheme.shapes.extraSmall
val cardSpacedBy = 2.dp

const val HOUR_MINUTES = 60
const val DAY_MINUTES = 24 * HOUR_MINUTES
const val WEEK_MINUTES = DAY_MINUTES * 7
const val MONTH_MINUTES = DAY_MINUTES * 30
const val YEAR_MINUTES = DAY_MINUTES * 365

const val MINUTE_SECONDS = 60
const val HOUR_SECONDS = HOUR_MINUTES * 60
const val DAY_SECONDS = DAY_MINUTES * 60
const val WEEK_SECONDS = WEEK_MINUTES * 60
const val MONTH_SECONDS = MONTH_MINUTES * 60
const val YEAR_SECONDS = YEAR_MINUTES * 60
