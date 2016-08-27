package com.parametris.iteng.asdf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by DELL on 8/15/2016.
 */
public class Identity {
    private String nickname;
    private final List<String> aliases = new ArrayList<>();
    private String ident;
    private String realName;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAliases(Collection<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

}
