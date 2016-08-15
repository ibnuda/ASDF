package com.parametris.iteng.asdf.models;

public class Authentication {
    private String saslUsername;
    private String saslPassword;
    private String nickservPassword;

    public String getSaslPassword() {
        return saslPassword;
    }

    public void setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
    }

    public String getNickservPassword() {
        return nickservPassword;
    }

    public void setNickservPassword(String nickservPassword) {
        this.nickservPassword = nickservPassword;
    }

    public String getSaslUsername() {
        return saslUsername;
    }

    public void setSaslUsername(String saslUsername) {
        this.saslUsername = saslUsername;
    }

    public boolean hasNickservCredentials() {
        return null != this.nickservPassword && this.nickservPassword.length() > 0;
    }

    public boolean hasSaslCredentials() {
        return null != this.saslUsername && this.saslUsername.length() > 0;
    }

}
