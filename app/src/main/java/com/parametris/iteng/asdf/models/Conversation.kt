package com.parametris.iteng.asdf.models

import java.util.LinkedList

abstract class Conversation(name: String) {

    val buffer: LinkedList<Message>
    val history: LinkedList<Message>
    val name: String
    var status = 1
        set(status) {
            if (this.status == STATUS_SELECTED && status != STATUS_DEFAULT) return
            if (this.status == STATUS_HIGHLIGHT && status != STATUS_SELECTED) return
            if (this.status == STATUS_DEFAULT && status != STATUS_MISC) return

            field = status
        }
    var newMentions = 0
        private set
    var historySize = DEFAULT_HISTORY_SIZE
        set(size) {
            if (size <= 0) return

            field = size
            if (this.history.size > size) {
                history.subList(size, history.size).clear()
            }
        }
    private var notify: Boolean = false

    abstract val type: Int

    init {
        this.buffer = LinkedList<Message>()
        this.history = LinkedList<Message>()
        this.name = name.toLowerCase()
    }

    fun addMessage(message: Message) {
        buffer.add(0, message)
        history.add(message)

        if (history.size > this.historySize) {
            history.remove()
        }
    }

    fun getHistoryMessage(position: Int): Message {
        return this.history[position]
    }

    fun pollBufferedMessage(): Message {
        val message = this.buffer[this.buffer.size - 1]
        this.buffer.removeAt(this.buffer.size - 1)
        return message
    }

    fun hasBufferedMessages(): Boolean {
        return this.buffer.size > 0
    }

    fun cleanBuffer() {
        this.buffer.clear()
    }

    fun addNewMention() {
        ++this.newMentions
    }

    fun clearNewMentions() {
        this.newMentions = 0
    }

    fun shouldAlwaysNotify(): Boolean {
        return notify
    }

    fun setAlwaysNotify(notify: Boolean) {
        this.notify = notify
    }

    companion object {
        val TYPE_CHANNEL = 1
        val TYPE_QUERY = 2
        val TYPE_SERVER = 3

        val STATUS_DEFAULT = 1
        val STATUS_SELECTED = 2
        val STATUS_MESSAGE = 3
        val STATUS_HIGHLIGHT = 4
        val STATUS_MISC = 5 // join/part/quit

        private val DEFAULT_HISTORY_SIZE = 30
    }
}
