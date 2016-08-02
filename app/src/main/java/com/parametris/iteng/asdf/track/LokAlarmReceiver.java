package com.parametris.iteng.asdf.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LokAlarmReceiver extends BroadcastReceiver {
    public LokAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LokService.class));
    }
}
