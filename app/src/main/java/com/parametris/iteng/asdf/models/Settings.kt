package com.parametris.iteng.asdf.models

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager

import com.parametris.iteng.asdf.R

class Settings(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val resources: Resources

    init {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        this.resources = context.applicationContext.resources
    }

    fun showTimestamp(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_timestamp),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_show_timestamp)))
    }

    fun showIcons(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_icons),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_show_icons)))
    }

    fun showColors(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_colors),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_show_colors)))
    }

    fun showColorsNick(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_colors_nick),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_show_colors_nick)))
    }

    fun use24hFormat(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_24h_format),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_24h_format)))
    }

    fun includeSeconds(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_include_seconds),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_include_seconds)))
    }


    val isReconnectEnabled: Boolean
        get() = sharedPreferences.getBoolean(
                resources.getString(R.string.key_reconnect),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_reconnect)))

    val reconnectInterval: Int
        get() = Integer.parseInt(sharedPreferences.getString(
                resources.getString(R.string.key_reconnect_interval),
                resources.getString(R.string.default_reconnect_interval)))

    val isIgnoreMOTDEnabled: Boolean
        get() = sharedPreferences.getBoolean(
                resources.getString(R.string.key_ignore_motd),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_ignore_motd)))

    val quitMessage: String
        get() = sharedPreferences.getString(
                resources.getString(R.string.key_quitmessage),
                resources.getString(R.string.default_quitmessage))

    val fontSize: Int
        get() = Integer.parseInt(sharedPreferences.getString(
                resources.getString(R.string.key_fontsize),
                resources.getString(R.string.default_fontsize)))

    val isSoundHighlightEnabled: Boolean
        get() = sharedPreferences.getBoolean(
                resources.getString(R.string.key_sound_highlight),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_sound_highlight)))

    val isVibrateHighlightEnabled: Boolean
        get() = sharedPreferences.getBoolean(
                resources.getString(R.string.key_vibrate_highlight),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_vibrate_highlight)))

    val isLedHighlightEnabled: Boolean
        get() = sharedPreferences.getBoolean(
                resources.getString(R.string.key_led_highlight),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_led_highlight)))

    fun showJoinPartAndQuit(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_joinpartquit),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_show_joinpartquit)))
    }

    fun showNoticeInServerWindow(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_notice_server_window),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_notice_server_window)))
    }

    fun showMircColors(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_mirc_colors),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_mirc_colors)))
    }

    fun showGraphicalSmilies(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_graphical_smilies),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_graphical_smilies)))
    }

    fun autoCorrectText(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_autocorrect_text),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_autocorrect_text)))
    }

    fun debugTraffic(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_debug_traffic),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_debug_traffic)))
    }

    fun autoCapSentences(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_autocap_sentences),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_autocap_sentences)))
    }

    fun imeExtract(): Boolean {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_ime_extract),
                java.lang.Boolean.parseBoolean(resources.getString(R.string.default_ime_extract)))
    }

    val historySize: Int
        get() {
            try {
                return Integer.parseInt(sharedPreferences.getString(
                        resources.getString(R.string.key_history_size),
                        resources.getString(R.string.default_history_size)))
            } catch (e: NumberFormatException) {
                return Integer.parseInt(resources.getString(R.string.default_history_size))
            }

        }
}
