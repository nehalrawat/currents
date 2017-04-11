package edu.cornell.cusd.upson;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
    public static final String ACTION_RESP = "edu.cornell.cusd.upson.intent.action.GEOFENCE_PROCESSED";

    NotificationManager mManager = null;
    Notification note = null;
    @Override
    public void onReceive(Context context, Intent intent)
    {

    }
}