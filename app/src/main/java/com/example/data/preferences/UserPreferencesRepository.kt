package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val fontSizeSp: Float = 18f,
    val fontFamily: String = "vazir", // "vazir", "sans", "serif", "monospace", "nastaliq"
    val fontColorHex: String = "#FFFFFF",
    val themeMode: String = "DARK", // "LIGHT", "DARK" (Default to DARK mode)
    val isGlassMode: Boolean = true, // Default to true (Frosted Glassmorphism mode in dark)
    val coins: Int = 150, // Coins balance
    val lastDailyClaimTime: Long = 0L // Timestamp of last 15-coin claim
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val themeMode = prefs.getString("theme_mode", "DARK") ?: "DARK"
        val fontColorDefault = if (themeMode == "DARK") "#FFFFFF" else "#0F172A"
        return UserSettings(
            fontSizeSp = prefs.getFloat("font_size", 18f),
            fontFamily = prefs.getString("font_family", "vazir") ?: "vazir",
            fontColorHex = prefs.getString("font_color", fontColorDefault) ?: fontColorDefault,
            themeMode = themeMode,
            isGlassMode = prefs.getBoolean("glass_mode", true),
            coins = prefs.getInt("user_coins", 150),
            lastDailyClaimTime = prefs.getLong("last_daily_claim_time", 0L)
        )
    }

    fun addCoins(amount: Int) {
        val current = _settings.value.coins
        val updated = current + amount
        prefs.edit().putInt("user_coins", updated).apply()
        _settings.value = _settings.value.copy(coins = updated)
    }

    fun spendCoins(amount: Int): Boolean {
        val current = _settings.value.coins
        if (current >= amount) {
            val updated = current - amount
            prefs.edit().putInt("user_coins", updated).apply()
            _settings.value = _settings.value.copy(coins = updated)
            return true
        }
        return false
    }

    fun canClaimDailyReward(): Boolean {
        val lastClaim = _settings.value.lastDailyClaimTime
        if (lastClaim == 0L) return true
        val diff = System.currentTimeMillis() - lastClaim
        return diff >= 24 * 60 * 60 * 1000
    }

    fun getRemainingTimeMillis(): Long {
        val lastClaim = _settings.value.lastDailyClaimTime
        if (lastClaim == 0L) return 0L
        val diff = System.currentTimeMillis() - lastClaim
        val totalMillis = 24 * 60 * 60 * 1000L
        val remaining = totalMillis - diff
        return if (remaining > 0L) remaining else 0L
    }

    fun claimDailyReward(): Boolean {
        if (canClaimDailyReward()) {
            val now = System.currentTimeMillis()
            val current = _settings.value.coins
            val updated = current + 15
            prefs.edit()
                .putInt("user_coins", updated)
                .putLong("last_daily_claim_time", now)
                .apply()
            _settings.value = _settings.value.copy(
                coins = updated,
                lastDailyClaimTime = now
            )
            return true
        }
        return false
    }

    fun updateFontSize(size: Float) {
        prefs.edit().putFloat("font_size", size).apply()
        _settings.value = _settings.value.copy(fontSizeSp = size)
    }

    fun updateFontFamily(family: String) {
        prefs.edit().putString("font_family", family).apply()
        _settings.value = _settings.value.copy(fontFamily = family)
    }

    fun updateFontColor(hex: String) {
        prefs.edit().putString("font_color", hex).apply()
        _settings.value = _settings.value.copy(fontColorHex = hex)
    }

    fun updateThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateGlassMode(enabled: Boolean) {
        prefs.edit().putBoolean("glass_mode", enabled).apply()
        _settings.value = _settings.value.copy(isGlassMode = enabled)
    }
}
