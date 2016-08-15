package com.parametris.iteng.asdf.models

import java.util.ArrayList
import java.util.LinkedHashMap

class Server {
    var id: Int = 0
    var title: String? = null
    var host: String? = null
    var port: Int = 0
    var password: String? = null
    var charset: String? = null
    var isUseSSL = false

    var identity: Identity? = null
    var authentication: Authentication? = null

    private val conversations = LinkedHashMap<String, Conversation>()
    var autoJoinChannels: ArrayList<String>? = null
    var connectCommands: ArrayList<String>? = null

    var status = Status.DISCONNECTED
    var selected = ""
    var isForeground = false
    var isMayReconnect = false

    init {
        conversations.put(ServerInfo.DEFAULT_NAME, ServerInfo())
        this.selected = ServerInfo.DEFAULT_NAME
    }

    fun getConversations(): Collection<Conversation> {
        return conversations.values
    }

    fun getConversation(name: String): Conversation? {
        return conversations[name.toLowerCase()]
    }

    fun addConversation(conversation: Conversation) {
        conversations.put(conversation.name.toLowerCase(), conversation)
    }

    fun removeConversation(name: String) {
        conversations.remove(name.toLowerCase())
    }

    fun clearConversations() {
        conversations.clear()
        conversations.put(ServerInfo.DEFAULT_NAME, ServerInfo())
        this.selected = ServerInfo.DEFAULT_NAME
    }

    val currentChannelNames: ArrayList<String>
        get() {
            val channels = ArrayList<String>()
            val mConversations = this.conversations.values

            for (conversation in mConversations) {
                if (conversation.type == Conversation.TYPE_CHANNEL)
                    channels.add(conversation.name)
            }
            return channels
        }
}
