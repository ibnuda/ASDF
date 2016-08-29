package com.parametris.iteng.protocol;

public class IRCException extends Exception {
    public static final Long serialVersionUUID = Long.MAX_VALUE - 23232;

    public IRCException(String e) {
        super(e);
    }
}
