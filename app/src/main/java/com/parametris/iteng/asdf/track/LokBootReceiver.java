package com.parametris.iteng.asdf.track;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class LokBootReceiver extends BroadcastReceiver {
    public LokBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiverIntent = new Intent(context, LokAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, 0);

        SharedPreferences prefs = context.getSharedPreferences("lokjav", Context.MODE_PRIVATE);
        int interval = prefs.getInt("interval", 1);
        Boolean trackingNow = prefs.getBoolean("trackingNow", false);

        if (trackingNow)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval * 10000, pendingIntent);
        else
            alarmManager.cancel(pendingIntent);


    }
}
