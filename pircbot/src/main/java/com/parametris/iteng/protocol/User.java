package com.parametris.iteng.protocol;

public class User {

    private String nick;
    private String prefix;
    private String nickLower;

    User(String prefix, String nick) {
        this.prefix = prefix;
        this.nick = nick;
        this.nickLower = nick.toLowerCase();
    }

    public String getNick() {
        return nick;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isOp() {
        return prefix.indexOf('@') >= 0;
    }

    public boolean hasVoice() {
        return prefix.indexOf('+') >= 0;
    }

    public String toString() {
        return this.getPrefix() + this.getNick();
    }

    public int hashCode() {
        return nickLower.hashCode();
    }

    public boolean equals(Object another) {
        if (another instanceof User) {
            User user = (User) another;
            return user.nickLower.equals(nickLower);
        }
        return false;
    }

    public boolean equals(String another) {
        return another.toLowerCase().equals(nickLower);
    }

}
