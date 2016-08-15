package com.parametris.iteng.asdf.models

import android.content.Context
import android.system.StructPollfd
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.widget.TextView

import java.util.Date

class Message @JvmOverloads constructor(val text: String, private val sender: String? = null, type: Int = Message.TYPE_MESSAGE) {
    private var canvas: SpannableString? = null
    private var timestamp: Long = 0

    private var color = NO_COLOR
    var type = NO_ICON
    var icon = NO_TYPE

    init {
        this.timestamp = Date().time
        this.type = type
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
    }

    private val senderColor: Int
        get() {
            if (null == sender) {
                return COLOR_DEFAULT
            }
            var color = 0
            for (i in 0..sender.length - 1) {
                color += sender[i].toInt()
            }
            color %= (colors.size - 1)
            return colors[color]
        }

    fun render(context: Context): SpannableString {
        val settings = Settings(context)
        if (null == canvas) {
            val prefix = if (hasIcon() && settings.showIcons()) " " else ""
            val nick = if (hasSender()) "<$sender>" else ""
            val timestamp = if (settings.showTimestamp())
                renderTimeStamp(settings.use24hFormat(), settings.includeSeconds())
            else
                ""
            canvas = SpannableString(prefix + nick + timestamp)
            val renderedText = SpannableString(this.text)

            canvas = SpannableString(TextUtils.concat(canvas, renderedText))
            if (hasSender()) {
                val start = (prefix + timestamp).length + 1
                val end = start + sender!!.length

                if (settings.showColorsNick()) {
                    canvas!!.setSpan(ForegroundColorSpan(senderColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            if (hasColor() && settings.showColors()) {
                val foregroundColorSpans = canvas!!.getSpans(0, canvas!!.length, ForegroundColorSpan::class.java)
                var start = 0

                for (foregroundColorSpan in foregroundColorSpans) {
                    canvas!!.setSpan(ForegroundColorSpan(color), start, canvas!!.getSpanStart(foregroundColorSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    start = canvas!!.getSpanEnd(foregroundColorSpan)
                }

                canvas!!.setSpan(ForegroundColorSpan(color), start, canvas!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return canvas!!
    }

    fun renderTextView(context: Context, textView: TextView?): TextView {
        var textView = textView
        if (null == textView) {
            textView = TextView(context)
        }

        textView.autoLinkMask = Linkify.ALL
        textView.linksClickable = true
        textView.setLinkTextColor(COLOR_BLUE)
        textView.text = this.render(context)
        textView.setTextIsSelectable(true)

        return textView
    }

    private fun renderTimeStamp(use24HourFormat: Boolean, includeSeconds: Boolean): String {
        val date = Date(timestamp)

        var hours = date.hours
        val minutes = date.minutes
        val seconds = date.seconds

        if (!use24HourFormat) {
            hours = Math.abs(12 - hours)
            if (12 == hours) {
                hours = 0
            }
        }
        if (includeSeconds) {
            return String.format(
                    "[%02d:%02d:%02d]",
                    hours,
                    minutes,
                    seconds)
        } else {
            return String.format(
                    "[%02d:%02d]",
                    hours,
                    minutes)
        }
    }

    fun hasSender(): Boolean {
        return null != sender
    }

    fun hasColor(): Boolean {
        return color != NO_COLOR
    }

    fun hasIcon(): Boolean {
        return icon != NO_ICON
    }

    companion object {
        val COLOR_GREEN = 0xFF4caf50.toInt()
        val COLOR_RED = 0xFFf44336.toInt()
        val COLOR_BLUE = 0xFF3f51b5.toInt()
        val COLOR_YELLOW = 0xFFffc107.toInt()
        val COLOR_GREY = 0xFF607d8b.toInt()
        val COLOR_DEFAULT = 0xFF212121.toInt()

        val TYPE_MESSAGE = 0
        val TYPE_MISC = 1

        val NO_ICON = -1
        val NO_TYPE = -1
        val NO_COLOR = -1

        private val colors = intArrayOf(0xFFf44336.toInt(), // Red
                0xFFe91e63.toInt(), // Pink
                0xFF9c27b0.toInt(), // Purple
                0xFF673ab7.toInt(), // Deep Purple
                0xFF3f51b5.toInt(), // Indigo
                0xFF2196f3.toInt(), // Blue
                0xFF03a9f4.toInt(), // Light Blue
                0xFF00bcd4.toInt(), // Cyan
                0xFF009688.toInt(), // Teal
                0xFF4caf50.toInt(), // Green
                0xFF8bc34a.toInt(), // Light green
                0xFFcddc39.toInt(), // Lime
                0xFFffeb3b.toInt(), // Yellow
                0xFFffc107.toInt(), // Amber
                0xFFff9800.toInt(), // Orange
                0xFFff5722.toInt(), // Deep Orange
                0xFF795548.toInt())// Brown
    }
}
