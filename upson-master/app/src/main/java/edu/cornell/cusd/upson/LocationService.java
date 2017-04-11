package edu.cornell.cusd.upson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient; // Runs the Google API for location tracking
    private LocationRequest mLocationRequest; // The continuous request for location
    private NotificationManager mManager;
            // Allows us to send notifications to the phone.

    // Currently points to Carpenter Hall

    private double UPSON_LAT = 42.444844;
    private double UPSON_LONG = -76.484054;
    private double CAMPUS_LAT = 42.447859;
    private double CAMPUS_LONG = -76.476396;
    private double UPSON_LAT_DIFF = 0.0009;
    private double UPSON_LONG_DIFF = 0.0007;
    private double CAMPUS_LAT_DIFF = 0.022;
    private double CAMPUS_LONG_DIFF = 0.016;

    public LocationService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000)        // 30 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Gets our location continuously
        startLocationUpdates();
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Able to request new updates
        if(location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*
    Starts up the continuous location requests.
     */
    protected void createLocationRequest() {
        if(mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(30000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    /*
   Self explanatory - starts location updates
    */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /*
    Called every time we detect a new location.
     */
    private void handleNewLocation(Location location) {
        createLocationRequest();

        SharedPreferences p = getSharedPreferences("Account", MODE_PRIVATE);
        boolean onCampus = p.getBoolean("onCampus", false);

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        double upsonLatRange = Math.abs(UPSON_LAT - currentLatitude);
        double upsonLongRange = Math.abs(UPSON_LONG - currentLongitude);
        if(upsonLatRange < UPSON_LAT_DIFF && upsonLongRange < UPSON_LONG_DIFF) {
            // Send data to Server.
            Intent intent1 = new Intent(this.getApplicationContext(), HttpIntentService.class);
            intent1.putExtra("caller", "upsonenter");
            startService(intent1);
        }
        else {
            Intent intent1 = new Intent(this.getApplicationContext(), HttpIntentService.class);
            intent1.putExtra("caller", "upsonexit");
            startService(intent1);
        }
        double campusLatRange = Math.abs(CAMPUS_LAT - currentLatitude);
        double campusLongRange = Math.abs(CAMPUS_LONG - currentLongitude);
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        if(campusLatRange < CAMPUS_LAT_DIFF && campusLongRange < CAMPUS_LONG_DIFF && !onCampus) {
            SharedPreferences.Editor e = p.edit();
            e.putBoolean("onCampus", true);
            e.commit();
            e.apply();
            // The code below is working code for the notification.
            // We aren't entirely sure how it works, but it generally does.
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
            // End notification
        }
        else if(onCampus && campusLatRange > CAMPUS_LAT_DIFF && campusLongRange > CAMPUS_LONG_DIFF) {
            SharedPreferences.Editor e = p.edit();
            e.putBoolean("onCampus", false);
            e.commit();
            e.apply();
            // The code below is working code for the notification.
            // We aren't entirely sure how it works, but it generally does.
            Intent onCampusIntent = new Intent(this.getApplicationContext(), MainActivity.class);
            Notification turnOnLocation = new Notification.Builder(this.getApplicationContext())
                    .setContentTitle("Turn off Campus Location Updates")
                    .setContentText(("Click here to disable location updates for the rest of the day. This will" +
                            "save power on your phone when you're off campus."))
                    .setSmallIcon(R.drawable.mr_ic_media_route_connecting_mono_light)
                    .build();
            onCampusIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, onCampusIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            turnOnLocation.flags |= Notification.FLAG_AUTO_CANCEL;
            mManager.notify(0, turnOnLocation);
            // End notification
        }
    }
}
