package com.prj_peach.practicalparent;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.NoisePlayer;
import com.prj_peach.practicalparent.model.TaskManager;
import com.prj_peach.practicalparent.model.TimerService;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private static  boolean callBackFromNotification = false;
    private static final String CHANNEL_ID = "NotificationChannelID";

    private static boolean uiLoopIsPaused = false;
    private static boolean timerIsPaused = true;
    private static boolean useCustomTime = false;
    private static boolean timeUp = false;
    private static NoisePlayer myNoisePlayer;
    private static int initTime;
    private static long milsRemaining;
    private static int presentSecondsRemaining;
    private static boolean performTimeUpChange;
    private static TimerService timerService;
    private static boolean boundToTimer = false;
    private double currentSpeed = 1.0;
    private TextView timeText;
    private ProgressBar progressCircle;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmActivity.class);
    }

    public static Intent getIntentCallBack(Context context) {
        callBackFromNotification = true;
        return new Intent(context, AlarmActivity.class);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(AlarmActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
        createNotificationChannel();
        performTimeUpChange = false;
        initTime = 60;
        if(callBackFromNotification) {
            milsRemaining = 0;
            presentSecondsRemaining = 0;
        }
        else {
            milsRemaining = (int) Math.round(initTime*1000.0/currentSpeed);
            presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
        }
        timeText = findViewById(R.id.Alarm_timeText);
        progressCircle = findViewById(R.id.Alarm_progressCircle);
        myNoisePlayer = NoisePlayer.getInstance(this);

        setUpTimeSpeedSpinner();
        setUpToolBar();
        setupUiLoop();
        registerAllButtonsAndTimeView();
        setupTimeOptionsSpinner();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }



    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(AlarmActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(AlarmActivity.this);
        TaskManager.getInstance().saveTask(AlarmActivity.this);
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.Alarm_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (TimerService.isRunning) {
            unbindService(connection);
        }
        uiLoopIsPaused = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // attempt to bind to timer (we may have a previously started timer running)
        if (TimerService.isRunning) {
            Intent serviceIntent = TimerService.getIntent(AlarmActivity.this, (int) milsRemaining/1000);
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            timerIsPaused = false;
            FloatingActionButton playPauseBtn = findViewById(R.id.Alarm_startButton);
            playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
        } else {
            //resetTimer();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        uiLoopIsPaused = false;
        setupUiLoop();
        updateUi();
    }

    // creates a timer to regularly update the value of the timer text
    private void setupUiLoop() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!boundToTimer || uiLoopIsPaused || timerIsPaused || milsRemaining <= 0) {
                    handler.removeCallbacks(this);
                    uiLoopIsPaused = true;
                }
                updateUi();
                handler.postDelayed(this, 250);
            }
        }, 250);
    }

    private void updateUi() {
        if (boundToTimer && !timerIsPaused && !callBackFromNotification) {
            System.out.println(timerService.timeRemaining());
            milsRemaining = timerService.timeRemaining();
            presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
        }
        timeText.setText(formatTime(presentSecondsRemaining));
        int progress = (int) ((1.0 - ((float) presentSecondsRemaining / (float) initTime)) * 100.0);
        progressCircle.setProgress(progress);
        if(milsRemaining == 0) {
            presentSecondsRemaining = 0;
            if(!callBackFromNotification) {
                myNoisePlayer.play();
            }
            FloatingActionButton startButton = findViewById(R.id.Alarm_startButton);
            startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white_24dp));
            timeUp = true;
            timerIsPaused = true;
            if(!performTimeUpChange && !callBackFromNotification) {
                Intent intent = AlarmActivity.getIntentCallBack(this);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifyLemubit")
                        .setContentTitle(getResources().getString(R.string.notification_title))
                        .setContentText(getResources().getString(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_notice_icon)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setSound(Uri.parse("scan"))
                        .setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());



                performTimeUpChange = true;
            }
        }
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= 26)
        {
            CharSequence name = "LemubitReminderChannel";
            String description = "Channel for Lemubit Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void registerAllButtonsAndTimeView() {
        FloatingActionButton startButton = findViewById(R.id.Alarm_startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePausePlay();
            }
        });

        Button resetButton = findViewById(R.id.Alarm_resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        EditText customTime = findViewById(R.id.Alarm_customTimeField);
        customTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                initTime = getCustomTime() * 60;
                if(!callBackFromNotification) {
                    milsRemaining = (int) Math.round(initTime*1000.0/currentSpeed);
                    presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
                }
                resetTimer();
                return true;
            }
        });
    }

    private void resetTimer() {
        if (!timerIsPaused) {
            togglePausePlay();
        }
        if(!callBackFromNotification) {
            milsRemaining = (int) Math.round(initTime*1000.0/currentSpeed);
            presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
        }
        updateUi();
    }

    private void togglePausePlay() {
        FloatingActionButton startButton = findViewById(R.id.Alarm_startButton);
        Intent serviceIntent = TimerService.getIntent(AlarmActivity.this, (int) milsRemaining/1000);
        if(timeUp) {
            myNoisePlayer.stop();
            milsRemaining = (int) Math.round(initTime*1000.0/currentSpeed);
            presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
            updateUi();
            stopService(serviceIntent);
            startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
            performTimeUpChange = false;
            callBackFromNotification= false;
            timeUp = !timeUp;
        }
        else {
            if (timerIsPaused) {
                // start timer
                startService(serviceIntent);
                bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
            } else {
                // pause timer
                try {
                    unbindService(connection);
                    stopService(serviceIntent);
                }catch (Exception e){
                    System.out.println(e);
                }
                startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
            }
            timerIsPaused = !timerIsPaused;
        }
    }

    // Defines callbacks for service binding, passed to bindService(), gets data from timerService
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to TimerService, cast the IBinder and get TimerService instance
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            boundToTimer = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundToTimer = false;
        }
    };

    private void setupTimeOptionsSpinner() {
        int[] timeOptions = getResources().getIntArray(R.array.time_options);
        List<String> items = new ArrayList<String>();
        for(int option : timeOptions) {
            items.add(Integer.toString(option));
        }
        items.add(getResources().getString(R.string.alarm_customOption));

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = items.get(position);
                EditText customField = findViewById(R.id.Alarm_customTimeField);
                if (selection == getResources().getString(R.string.alarm_customOption)) {
                    // display edit text for user to enter custom time
                    customField.setVisibility(View.VISIBLE);
                    customField.setText("1");
                    initTime = getCustomTime() * 60;
                    milsRemaining = (int) Math.round(initTime*1000.0/currentSpeed);
                    presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
                    useCustomTime = true;
                } else {
                    customField.setVisibility(View.INVISIBLE);
                    int timeInSec = Integer.parseInt(selection) * 60;
                    if(milsRemaining/1000 == initTime) {
                        milsRemaining = timeInSec*1000;
                        presentSecondsRemaining = (int) Math.round(milsRemaining/1000.0 * currentSpeed);
                    }
                    initTime = timeInSec;

                    useCustomTime = false;
                }
                resetTimer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // configure spinner
        Spinner optionsSpinner = findViewById(R.id.Alarm_timeOptions);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        optionsSpinner.setAdapter(dataAdapter);
        optionsSpinner.setOnItemSelectedListener(listener);
        optionsSpinner.setSelection(0);
    }

    private void setUpTimeSpeedSpinner() {
        int[] timeSpeedOptions = getResources().getIntArray(R.array.timeSpeed);
        List<String> items = new ArrayList<String>();
        for(int option : timeSpeedOptions) {
            items.add(Integer.toString(option));
        }
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selection = items.get(position);
                double oldSpeed = currentSpeed;
                currentSpeed = timeSpeedOptions[position]*0.01;
                changeSpeed(oldSpeed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        Spinner timeSpeedSpinner = findViewById(R.id.speedSpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.time_speed_spinner_item,
                items);
        timeSpeedSpinner.setAdapter(myAdapter);
        timeSpeedSpinner.setOnItemSelectedListener(listener);
        timeSpeedSpinner.setSelection(3);
    }

    private void changeSpeed(double oldSpeed) {
        if(timerIsPaused) {
            milsRemaining = (int) Math.round(milsRemaining * oldSpeed / currentSpeed);
        }else {
            togglePausePlay();
            milsRemaining = (int) Math.round(milsRemaining * oldSpeed / currentSpeed);
            togglePausePlay();
        }
    }

    private int getCustomTime() {
        if (useCustomTime) {
            EditText customField = findViewById(R.id.Alarm_customTimeField);
            Editable timeRaw = customField.getEditableText();
            if (timeRaw.length() <= 0) {
                return 1;
            }
            int time = Integer.parseInt(timeRaw.toString());
            if (time > 0  && time < 1440) {
                return time;
            }
        }
        return 1;
    }

    private String formatTime(int secondsRemaining) {
        int minutes = (int) Math.floor((float) secondsRemaining / 60.0);
        int seconds = secondsRemaining % 60;
        return String.format(getResources().getString(R.string.alarm_timeFormat),
                minutes, seconds);
    }
}