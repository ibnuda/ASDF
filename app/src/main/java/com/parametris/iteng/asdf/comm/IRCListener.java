package com.parametris.iteng.asdf.comm;

import org.pircbotx.Configuration;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class IRCListener extends ListenerAdapter {
    @Override
    public void onGenericMessage(GenericMessageEvent messageEvent) {

    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration.Builder()
                .setName("TODO")
                .addListener(new IRCListener())
                .buildConfiguration();


    }
}
