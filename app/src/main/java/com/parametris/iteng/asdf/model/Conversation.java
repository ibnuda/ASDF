package com.parametris.iteng.asdf.model;

import java.util.LinkedList;

public abstract class Conversation {
    public static final int TYPE_CHANNEL = 1;
    public static final int TYPE_QUERY   = 2;
    public static final int TYPE_SERVER  = 3;

    public static final int STATUS_DEFAULT   = 1;
    public static final int STATUS_SELECTED  = 2;
    public static final int STATUS_MESSAGE   = 3;
    public static final int STATUS_HIGHLIGHT = 4;
    public static final int STATUS_MISC      = 5; // join/part/quit

    private static final int DEFAULT_HISTORY_SIZE = 30;

    private final LinkedList<Message> buffer;
    private final LinkedList<Message> history;
    private final String name;
    private int status = 1;
    private int newMentions = 0;
    private int historySize = DEFAULT_HISTORY_SIZE;
    private boolean notify;

    public abstract int getType();

    public Conversation(String name) {
        this.buffer = new LinkedList<>();
        this.history = new LinkedList<>();
        this.name = name.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public void addMessage(Message message) {
        buffer.add(0, message);
        history.add(message);

        if (history.size() > historySize) {
            history.remove();
        }
    }

    public LinkedList<Message> getHistory() {
        return this.history;
    }

    public Message getHistoryMessage(int position) {
        return this.history.get(position);
    }

    public Message pollBufferedMessage() {
        Message message = this.buffer.get(this.buffer.size() - 1);
        this.buffer.remove(this.buffer.size() - 1);
        return message;
    }

    public LinkedList<Message> getBuffer() {
        return this.buffer;
    }

    public boolean hasBufferedMessages() {
        return this.buffer.size() > 0;
    }

    public void cleanBuffer() {
        this.buffer.clear();
    }

    public void setStatus(int status) {
        if (this.status == STATUS_SELECTED && status != STATUS_DEFAULT) return;
        if (this.status == STATUS_HIGHLIGHT && status != STATUS_SELECTED) return;
        if (this.status == STATUS_DEFAULT && status != STATUS_MISC) return;

        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void addNewMention() {
        ++this.newMentions;
    }

    public void clearNewMentions() {
        this.newMentions = 0;
    }

    public int getNewMentions() {
        return this.newMentions;
    }

    public int getHistorySize() {
        return this.historySize;
    }

    public void setHistorySize(int size) {
        if (size <= 0) return;

        this.historySize = size;
        if (this.history.size() > size) {
            history.subList(size, history.size()).clear();
        }
    }

    public boolean shouldAlwaysNotify() {
        return notify;
    }

    public void setAlwaysNotify(boolean notify) {
        this.notify = notify;
    }
}
