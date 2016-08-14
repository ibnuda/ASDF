package com.parametris.iteng.asdf.track

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LokAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, LokService::class.java))
    }
}
