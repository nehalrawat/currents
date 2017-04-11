package edu.cornell.cusd.upson;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class NotificationService extends Service {
    public NotificationService(String s) {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
