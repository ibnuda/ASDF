package com.parametris.iteng.asdf.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.parametris.iteng.asdf.R;

public class Settings {
    private final SharedPreferences sharedPreferences;
    private final Resources resources;

    public Settings(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.resources = context.getApplicationContext().getResources();
    }

    public boolean showTimestamp() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_timestamp),
                Boolean.parseBoolean(resources.getString(R.string.default_show_timestamp))
        );
    }

    public boolean showIcons() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_icons),
                Boolean.parseBoolean(resources.getString(R.string.default_show_icons))
        );
    }

    public boolean showColors() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_colors),
                Boolean.parseBoolean(resources.getString(R.string.default_show_colors))
        );
    }

    public boolean showColorsNick() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_colors_nick),
                Boolean.parseBoolean(resources.getString(R.string.default_show_colors_nick))
        );
    }

    public boolean use24hFormat() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_24h_format),
                Boolean.parseBoolean(resources.getString(R.string.default_24h_format))
        );
    }

    public boolean includeSeconds() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_include_seconds),
                Boolean.parseBoolean(resources.getString(R.string.default_include_seconds))
        );
    }


    public boolean isReconnectEnabled() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_reconnect),
                Boolean.parseBoolean(resources.getString(R.string.default_reconnect))
        );
    }

    public int getReconnectInterval() {
        return Integer.parseInt(sharedPreferences.getString(
                resources.getString(R.string.key_reconnect_interval),
                resources.getString(R.string.default_reconnect_interval)
        ));
    }

    public boolean isIgnoreMOTDEnabled() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_ignore_motd),
                Boolean.parseBoolean(resources.getString(R.string.default_ignore_motd))
        );
    }

    public String getQuitMessage() {
        return sharedPreferences.getString(
                resources.getString(R.string.key_quitmessage),
                resources.getString(R.string.default_quitmessage)
        );
    }

    public int getFontSize() {
        return Integer.parseInt(sharedPreferences.getString(
                resources.getString(R.string.key_fontsize),
                resources.getString(R.string.default_fontsize)
        ));
    }

    public boolean isSoundHighlightEnabled() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_sound_highlight),
                Boolean.parseBoolean(resources.getString(R.string.default_sound_highlight))
        );
    }

    public boolean isVibrateHighlightEnabled() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_vibrate_highlight),
                Boolean.parseBoolean(resources.getString(R.string.default_vibrate_highlight))
        );
    }

    public boolean isLedHighlightEnabled() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_led_highlight),
                Boolean.parseBoolean(resources.getString(R.string.default_led_highlight))
        );
    }

    public boolean showJoinPartAndQuit() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_show_joinpartquit),
                Boolean.parseBoolean(resources.getString(R.string.default_show_joinpartquit))
        );
    }

    public boolean showNoticeInServerWindow() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_notice_server_window),
                Boolean.parseBoolean(resources.getString(R.string.default_notice_server_window))
        );
    }

    public boolean showMircColors() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_mirc_colors),
                Boolean.parseBoolean(resources.getString(R.string.default_mirc_colors))
        );
    }

    public boolean showGraphicalSmilies() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_graphical_smilies),
                Boolean.parseBoolean(resources.getString(R.string.default_graphical_smilies))
        );
    }

    public boolean autoCorrectText() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_autocorrect_text),
                Boolean.parseBoolean(resources.getString(R.string.default_autocorrect_text))
        );
    }

    public boolean debugTraffic() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_debug_traffic),
                Boolean.parseBoolean(resources.getString(R.string.default_debug_traffic))
        );
    }

    public boolean autoCapSentences() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_autocap_sentences),
                Boolean.parseBoolean(resources.getString(R.string.default_autocap_sentences))
        );
    }

    public boolean imeExtract() {
        return sharedPreferences.getBoolean(
                resources.getString(R.string.key_ime_extract),
                Boolean.parseBoolean(resources.getString(R.string.default_ime_extract))
        );
    }

    public int getHistorySize() {
        try {
            return Integer.parseInt(sharedPreferences.getString(
                    resources.getString(R.string.key_history_size),
                    resources.getString(R.string.default_history_size)
            ));
        } catch (NumberFormatException e) {
            return Integer.parseInt(resources.getString(R.string.default_history_size));
        }
    }
}
