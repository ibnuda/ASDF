package com.parametris.iteng.asdf.track

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock

class LokBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmReceiverIntent = Intent(context, LokAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, 0)

        val prefs = context.getSharedPreferences("lokjav", Context.MODE_PRIVATE)
        val interval = prefs.getInt("interval", 1)
        val trackingNow = prefs.getBoolean("trackingNow", false)

        if (trackingNow)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), (interval * 10000).toLong(), pendingIntent)
        else
            alarmManager.cancel(pendingIntent)


    }
}
