package edu.cornell.cusd.upson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.policy.resources.SQSQueueResource;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private NotificationManager mManager;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int num, int mum1) {
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                String errorMessage = "ERROR";
                Log.e("ERROR", errorMessage);
                return;
            }
            String id = "";
            if(geofencingEvent.getTriggeringGeofences() != null) {
                Geofence triggeredGeofence = geofencingEvent.getTriggeringGeofences().get(0);
                id = triggeredGeofence.getRequestId();
                int triggerType = geofencingEvent.getGeofenceTransition();
                if(triggerType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    if(id.equals("Campus")) {
                        Intent onCampusIntent = new Intent(this.getApplicationContext(), MainActivity.class);
                        Notification turnOnLocation = new Notification.Builder(this.getApplicationContext())
                                .setContentTitle("Turn on Campus Location Updates")
                                .setContentText(("Click here to enable location updates for the day. This will allow" +
                                        "us to better predict your heating and cooling needs."))
                                .setSmallIcon(R.drawable.mr_ic_media_route_connecting_mono_light)
                                .build();
                        onCampusIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, onCampusIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        turnOnLocation.flags |= Notification.FLAG_AUTO_CANCEL;
                        mManager.notify(0, turnOnLocation);
                    }
                    if(id.equals("Upson")) {
                        URL url = null;
                        try {
                            url = new URL("ec2-54-187-113-38.us-west-2.compute.amazonaws.com");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setChunkedStreamingMode(0);

                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(out, "UTF-8"));
                            writer.write(URLEncoder.encode("\\insertxBee?isinrange=true&roomid=1", "UTF-8"));
                            writer.flush();
                            writer.close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(triggerType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    if(id.equals("Campus")) {
                        Intent offCampusIntent = new Intent(this.getApplicationContext(), MainActivity.class);
                        Notification turnOffLocation = new Notification.Builder(this.getApplicationContext())
                                .setContentTitle("Turn off Campus Location Updates")
                                .setContentText(("Click here to disable location updates for the rest of the day." +
                                        " Use this if you are going off campus for a while."))
                                .setSmallIcon(R.drawable.mr_ic_media_route_connecting_mono_light)
                                .build();
                        offCampusIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, offCampusIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        turnOffLocation.flags |= Notification.FLAG_AUTO_CANCEL;
                        mManager.notify(0, turnOffLocation);
                    }
                    if(id.equals("Upson")) {
                        URL url = null;
                        try {
                            url = new URL("ec2-54-187-113-38.us-west-2.compute.amazonaws.com");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setChunkedStreamingMode(0);

                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(out, "UTF-8"));
                            writer.write(URLEncoder.encode("\\insertxBee?isinrange=false&roomid=1", "UTF-8"));
                            writer.flush();
                            writer.close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
