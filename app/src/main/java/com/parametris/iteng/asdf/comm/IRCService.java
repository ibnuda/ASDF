package com.parametris.iteng.asdf.comm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.parametris.iteng.asdf.models.Conversation;
import com.parametris.iteng.asdf.models.Server;
import com.parametris.iteng.asdf.models.Settings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class IRCService extends Service {
    public static final String ACTION_FOREGROUND = "service.foreground";
    public static final String ACTION_BACKGROUND = "service.background";
    public static final String ACTION_ACK_NEW_MENTIONS = "service.ack_new_mentions";
    public static final String EXTRA_ACK_SERVERID = "service.ack_serverid";
    public static final String EXTRA_ACK_CONVTITLE = "service.ack_convtitle";

    private static final int FOREGROUND_NOTIFICATION = 1;
    private static final int NOTIFICATION_LED_OFF_MS = 1000;
    private static final int NOTIFICATION_LED_ON_MS = 300;
    private static final int NOTIFICATION_LED_COLOR = 0xff00ff00;

    @SuppressWarnings("rawtypes")
    private static final Class[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
    @SuppressWarnings("rawtypes")
    private static final Class[] mStopForegroundSignature = new Class[] { boolean.class };
    @SuppressWarnings("rawtypes")
    private static final Class[] mSetForegroudSignaure = new Class[] { boolean.class };

    private final IRCBinder binder;
    private final HashMap<Integer, IRCConnection> connections;
    private boolean foreground = false;
    private final ArrayList<String> connectedServerTitles;
    private final LinkedHashMap<String, Conversation> mentions;
    private int newMentions = 0;

    private NotificationManager notificationManager;
    private Method mStartForeground;
    private Method mStopForeground;
    private final Object[] mStartForegroundArgs = new Object[2];
    private final Object[] mStopForegroundArgs = new Object[1];
    private Notification notification;
    private Settings settings;

    private HashMap<Integer, PendingIntent> alarmIntents;
    // private HashMap<Integer, ReconnectReceiver> alarmReceivers;
    private final Object alarmIntentsLock;

    public IRCService() {
        super();
        this.connections = new HashMap<>();
        this.binder = new IRCBinder(this);
        this.connectedServerTitles = new ArrayList<>();
        this.mentions = new LinkedHashMap<>();
        this.alarmIntents = new LinkedHashMap<>();
        // this.alarmReceivers = new HashMap<>();
        this.alarmIntentsLock = new Object();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void connect(Server server) {
    }
}
