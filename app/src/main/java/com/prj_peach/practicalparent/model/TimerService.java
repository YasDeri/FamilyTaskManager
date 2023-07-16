package com.prj_peach.practicalparent.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.prj_peach.practicalparent.AlarmActivity;
import com.prj_peach.practicalparent.R;

/**
 * Service for running timer while app is not open
 * Is a started and bound service, so we can communicate the time
 * remaining to the calling activity
 */

public class TimerService extends Service {

    public static boolean isRunning = false;
    private static final String CHANNEL_ID = "NotificationChannelID";
    private static final String INTENT_EXTRA_INITTIME_KEY = "com.prj_peach.practicalParent.TimerService.initTime";
    private final IBinder binder = new LocalBinder();

    private static CountDownTimer timer;
    private static long milsRemaining;

    // used to communicate with calling activity
    public class LocalBinder extends Binder {
        public TimerService getService() {
            // Return this instance of TimerService so clients can call public methods
            return TimerService.this;
        }
    }

    // implement onStartCommand since we want this to be a started service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // get initial time from intent
        milsRemaining = intent.getIntExtra(INTENT_EXTRA_INITTIME_KEY, 0);
        Context context = this;
        timer =  new CountDownTimer(milsRemaining * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                milsRemaining  = millisUntilFinished;
            }

            @Override
            public void onFinish() {


                milsRemaining = 0;
                stopSelf();
            }
        };
        timer.start();
        isRunning = true;

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // returns amount of time remaining in timer, to be called by activity
    public long timeRemaining() {
        return milsRemaining;
    }

    public static Intent getIntent(Context context, int initSeconds) {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra(INTENT_EXTRA_INITTIME_KEY,  initSeconds);
        return intent;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
        isRunning = false;
    }

}
