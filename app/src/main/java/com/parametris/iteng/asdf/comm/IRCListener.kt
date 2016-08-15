package com.parametris.iteng.asdf.comm

import org.pircbotx.Configuration.*
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.types.GenericMessageEvent

class IRCListener : ListenerAdapter() {
    override fun onGenericMessage(messageEvent: GenericMessageEvent?) {

    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            val configuration = Builder().setName("TODO").addListener(IRCListener()).buildConfiguration()
        }
    }
}
