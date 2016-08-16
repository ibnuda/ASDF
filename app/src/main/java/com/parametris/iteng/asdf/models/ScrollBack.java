package com.parametris.iteng.asdf.models;

import java.util.LinkedList;

public class ScrollBack {
    public static final int MAX_HISTORY = 10;
    private final LinkedList<String> messages;
    private int pointer;

    public ScrollBack() {
        this.messages = new LinkedList<>();
    }

    public void addMessage(String message) {
        messages.addLast(message);
        if (messages.size() > MAX_HISTORY) {
            messages.removeFirst();
        }

        pointer = messages.size();
    }

    public String goBack() {
        if (pointer > 0) {
            pointer--;
        }

        if (messages.size() > 0) {
            return messages.get(pointer);
        }
        return null;
    }

    public String goForward() {
        if (pointer < messages.size() - 1) {
            pointer++;
        } else {
            return "";
        }

        if (messages.size() > 0) {
            return messages.get(pointer);
        }
        return null;
    }
}
