package com.parametris.iteng.asdf.comm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.activity.MainActivity;
import com.parametris.iteng.asdf.model.Broadcast;
import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Server;
import com.parametris.iteng.asdf.model.Settings;
import com.parametris.iteng.asdf.receiver.ReconnectReceiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    private static final Class[] startForegroundSignature = new Class[] { int.class, Notification.class };
    @SuppressWarnings("rawtypes")
    private static final Class[] stopForegroundSignature = new Class[] { boolean.class };
    @SuppressWarnings("rawtypes")
    private static final Class[] setForegroundSignature = new Class[] { boolean.class };

    private final IRCBinder binder;
    private final HashMap<Integer, IRCConnection> connections;
    private boolean foreground = false;
    private final ArrayList<String> connectedServerTitles;
    private final LinkedHashMap<String, Conversation> mentions;
    private int newMentions = 0;

    private NotificationManager notificationManager;
    private Method startForeground;
    private Method stopForeground;
    private final Object[] startForegroundArgs = new Object[2];
    private final Object[] stopForegroundArgs = new Object[1];
    private Notification notification;
    private Settings settings;

    private HashMap<Integer, PendingIntent> alarmIntents;
    private HashMap<Integer, ReconnectReceiver> alarmReceivers;
    private final Object alarmIntentsLock;

    public IRCService() {
        super();
        this.connections = new HashMap<>();
        this.binder = new IRCBinder(this);
        this.connectedServerTitles = new ArrayList<>();
        this.mentions = new LinkedHashMap<>();
        this.alarmIntents = new LinkedHashMap<>();
        this.alarmReceivers = new HashMap<>();
        this.alarmIntentsLock = new Object();
    }

    @Override
    public IRCBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        settings = new Settings(getBaseContext());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        try {
            startForeground = getClass().getMethod("startForeground", startForegroundSignature);
            stopForeground = getClass().getMethod("stopForeground", stopForegroundSignature);
        } catch (Exception e) {
            startForeground = null;
            stopForeground = null;
        }

        sendBroadcast(new Intent(Broadcast.SERVER_UPDATE));
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
        handleCommand(intent);
    }

    private void handleCommand(Intent intent) {
        if (ACTION_FOREGROUND.equals(intent.getAction())) {
            if (foreground) {
                return;
            }
            foreground = true;

            notification = new Notification(R.drawable.ic_media_play, getText(R.string.accept), System.currentTimeMillis());

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_media_play)
                    .setContentTitle(getText(R.string.app_name))
                    .setContentText(getText(R.string.cast_notification_connected_message))
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .build();
            startForegroundCompat(FOREGROUND_NOTIFICATION, notification);
        } else if (ACTION_BACKGROUND.equals(intent.getAction()) && !foreground) {
            stopForegroundCompat(FOREGROUND_NOTIFICATION);
        } else if (ACTION_ACK_NEW_MENTIONS.equals(intent.getAction())) {
            ackNewMentions(intent.getIntExtra(EXTRA_ACK_SERVERID, -1), intent.getStringExtra(EXTRA_ACK_SERVERID));
        }
    }

    private void ackNewMentions(int intExtra, String stringExtra) {
        if (null == stringExtra) {
            return;
        }

        Conversation conversation = mentions.remove(getConversationId(intExtra, stringExtra));
        if (null == conversation) {
            return;
        }
        newMentions -= conversation.getNewMentions();
        conversation.clearNewMentions();
        if (0 > newMentions) {
            newMentions = 0;
        }

        updateNotification(null, null, false, false, false);
    }

    private void updateNotification(String text, String contentText, boolean vibrate, boolean sound, boolean light) {
        if (foreground) {
            Intent intentNotify = new Intent(this, MainActivity.class);
        }
    }

    private String getConversationId(int intExtra, String stringExtra) {
        return intExtra + ":" + stringExtra;
    }

    private void stopForegroundCompat(int foregroundNotification) {
        foreground = false;
        if (null != stopForeground) {
            stopForegroundArgs[0] = Boolean.TRUE;
            try {
                stopForeground.invoke(this, stopForegroundArgs);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            notificationManager.cancel(foregroundNotification);

            try {
                Method setForeground = getClass().getMethod("setForeground", setForegroundSignature);
                setForeground.invoke(this, new Object[] {true});
            } catch (NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void startForegroundCompat(int foregroundNotification, Notification notification) {
        if (null != startForeground) {
            startForegroundArgs[0] = Integer.valueOf(foregroundNotification);
            startForegroundArgs[1] = notification;
            try {
                startForeground.invoke(this, startForegroundArgs);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method setForeground = getClass().getMethod("setForeground", setForegroundSignature);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            notificationManager.notify(foregroundNotification, notification);
        }
    }

    public void connect(final Server server) {

    }
}
