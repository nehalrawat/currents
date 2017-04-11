package edu.cornell.cusd.upson;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;


public class MyAlarmService extends IntentService
{

    private NotificationManager mManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyAlarmService(String name) {
        super(name);
    }

    public MyAlarmService() {
        super("");
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Notification note = new Notification.Builder(this.getApplicationContext())
                .setContentTitle("Alarm")
                .setContentText("Text")
                .setSmallIcon(R.drawable.mr_ic_media_route_connecting_mono_light)
                .build();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        mManager.notify(0, note);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyAlarmService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), 10000, pendingIntent);
        // Broadcasts the result of the service
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MyReceiver.ACTION_RESP);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MyAlarmService.class);
        Notification note = new Notification.Builder(this.getApplicationContext())
                .setContentTitle("Other Alarm")
                .setContentText("Text")
                .setSmallIcon(R.drawable.mr_ic_media_route_connecting_mono_light)
                .build();
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        mManager.notify(0, note);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
