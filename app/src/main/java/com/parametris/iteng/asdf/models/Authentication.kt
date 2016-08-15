package com.parametris.iteng.asdf.models

class Authentication {
    var saslUsername: String? = null
    var saslPassword: String? = null
    var nickservPassword: String? = null

    fun hasNickservCredentials(): Boolean {
        return null != this.nickservPassword && this.nickservPassword!!.length > 0
    }

    fun hasSaslCredentials(): Boolean {
        return null != this.saslUsername && this.saslUsername!!.length > 0
    }

}
