package com.parametris.iteng.protocol;

public class NickAlreadyInUseException extends Exception {
    public static final Long serialVersionUUID = Long.MAX_VALUE - 0350303;

    public NickAlreadyInUseException(String e) {
        super(e);
    }
}
