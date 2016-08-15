package com.parametris.iteng.asdf.models

import java.util.ArrayList
import java.util.Collections

class Identity {
    var nickname: String? = null
    private val aliases = ArrayList<String>()
    var ident: String? = null
    var realName: String? = null

    fun setAliases(aliases: Collection<String>) {
        this.aliases.clear()
        this.aliases.addAll(aliases)
    }

    fun getAliases(): List<String> {
        return Collections.unmodifiableList(aliases)
    }

}
