package com.parametris.iteng.asdf.command.handler;

import com.parametris.iteng.asdf.comm.IRCService;
import com.parametris.iteng.asdf.command.BaseHandler;
import com.parametris.iteng.asdf.exception.CommandException;
import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Message;
import com.parametris.iteng.asdf.model.Server;

import java.util.Collection;

/**
 * Created on 27/08/2016.
 */
public class AMsgHandler extends BaseHandler {
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService ircService) throws CommandException {
        Collection<Conversation> conversations = server.getConversations();

        for (Conversation currentConversation : conversations) {
            if (currentConversation.getType() == Conversation.TYPE_CHANNEL) {
            }
        }
    }
}
