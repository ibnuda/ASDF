package com.parametris.iteng.asdf.models

class ServerInfo : Conversation(ServerInfo.DEFAULT_NAME) {

    override val type: Int
        get() = Conversation.TYPE_SERVER

    companion object {

        val DEFAULT_NAME = ""
    }

}