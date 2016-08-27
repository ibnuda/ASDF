package com.parametris.iteng.asdf.command;

import com.parametris.iteng.asdf.comm.IRCService;
import com.parametris.iteng.asdf.exception.CommandException;
import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Server;

/**
 * Created on 27/08/2016.
 */
public abstract class BaseHandler {
    public abstract void execute(String[] params, Server server, Conversation conversation, IRCService ircService) throws CommandException;
}
