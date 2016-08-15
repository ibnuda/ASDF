package com.parametris.iteng.asdf.models;

import android.content.Context;
import android.system.StructPollfd;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.widget.TextView;

import java.util.Date;

public class Message {
    public static final int COLOR_GREEN   = 0xFF4caf50;
    public static final int COLOR_RED     = 0xFFf44336;
    public static final int COLOR_BLUE    = 0xFF3f51b5;
    public static final int COLOR_YELLOW  = 0xFFffc107;
    public static final int COLOR_GREY    = 0xFF607d8b;
    public static final int COLOR_DEFAULT = 0xFF212121;

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_MISC = 1;

    public static final int NO_ICON  = -1;
    public static final int NO_TYPE  = -1;
    public static final int NO_COLOR = -1;

    private final String text;
    private final String sender;
    private SpannableString canvas;
    private long timestamp;

    private int color = NO_COLOR;
    private int type  = NO_ICON;
    private int icon  = NO_TYPE;

    private static final int[] colors = {
            0xFFf44336, // Red
            0xFFe91e63, // Pink
            0xFF9c27b0, // Purple
            0xFF673ab7, // Deep Purple
            0xFF3f51b5, // Indigo
            0xFF2196f3, // Blue
            0xFF03a9f4, // Light Blue
            0xFF00bcd4, // Cyan
            0xFF009688, // Teal
            0xFF4caf50, // Green
            0xFF8bc34a, // Light green
            0xFFcddc39, // Lime
            0xFFffeb3b, // Yellow
            0xFFffc107, // Amber
            0xFFff9800, // Orange
            0xFFff5722, // Deep Orange
            0xFF795548, // Brown
    };

    public Message(String text, String sender, int type) {
        this.text = text;
        this.sender = sender;
        this.timestamp = new Date().getTime();
        this.type = type;
    }

    public Message(String text) {
        this(text, null, TYPE_MESSAGE);
    }

    public Message(String text, String sender) {
        this(text, sender, TYPE_MESSAGE);
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public int getType() {
        return this.type;
    }

    private int getSenderColor() {
        if (null == sender) {
            return COLOR_DEFAULT;
        }
        int color = 0;
        for (int i = 0; i < sender.length(); i++) {
            color += sender.charAt(i);
        }
        color = color % (colors.length - 1);
        return colors[color];
    }

    public SpannableString render(Context context) {
        Settings settings = new Settings(context);
        if (null == canvas) {
            String prefix = hasIcon() && settings.showIcons() ? " " : "";
            String nick = hasSender() ? "<" + sender + ">" : "";
            String timestamp = settings.showTimestamp() ?
                    renderTimeStamp(settings.use24hFormat(), settings.includeSeconds()) :
                    "";
            canvas = new SpannableString(prefix + nick + timestamp);
            SpannableString renderedText = new SpannableString(this.text);

            canvas = new SpannableString(TextUtils.concat(canvas, renderedText));
            if (hasSender()) {
                int start = (prefix + timestamp).length() + 1;
                int end = start + sender.length();

                if (settings.showColorsNick()) {
                    canvas.setSpan(new ForegroundColorSpan(getSenderColor()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if (hasColor() && settings.showColors()) {
                ForegroundColorSpan[] foregroundColorSpans = canvas.getSpans(0, canvas.length(), ForegroundColorSpan.class);
                int start = 0;

                for (ForegroundColorSpan foregroundColorSpan : foregroundColorSpans) {
                    canvas.setSpan(new ForegroundColorSpan(color), start, canvas.getSpanStart(foregroundColorSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = canvas.getSpanEnd(foregroundColorSpan);
                }

                canvas.setSpan(new ForegroundColorSpan(color), start, canvas.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return canvas;
    }

    public TextView renderTextView(Context context, TextView textView) {
        if (null == textView) {
            textView = new TextView(context);
        }

        textView.setAutoLinkMask(Linkify.ALL);
        textView.setLinksClickable(true);
        textView.setLinkTextColor(COLOR_BLUE);
        textView.setText(this.render(context));
        textView.setTextIsSelectable(true);

        return textView;
    }

    private String renderTimeStamp(boolean use24HourFormat, boolean includeSeconds) {
        Date date = new Date(timestamp);

        int hours = date.getHours();
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();

        if (!use24HourFormat) {
            hours = Math.abs(12 - hours);
            if (12 == hours) {
                hours = 0;
            }
        }
        if (includeSeconds) {
            return String.format(
                    "[%02d:%02d:%02d]",
                    hours,
                    minutes,
                    seconds);
        } else {
            return String.format(
                    "[%02d:%02d]",
                    hours,
                    minutes);
        }
    }

    public boolean hasSender() {
        return null != sender;
    }

    public boolean hasColor() {
        return color != NO_COLOR;
    }

    public boolean hasIcon() {
        return icon != NO_ICON;
    }
}
