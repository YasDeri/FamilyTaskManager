package com.prj_peach.practicalparent.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.prj_peach.practicalparent.AlarmActivity;
import com.prj_peach.practicalparent.R;

public class NoisePlayer {
    private static NoisePlayer instance;
    private Context mContext;
    private MediaPlayer alarmSoundPlayer;
    private NoisePlayer(Context context){
            mContext = context;
    }

    public static NoisePlayer getInstance(Context context) {
        if(instance == null) {
            instance = new NoisePlayer(context);
        }
        return instance;
    }

    public void play()
    {
        if(alarmSoundPlayer!= null && alarmSoundPlayer.isPlaying()) {
            alarmSoundPlayer.stop();
        }
        alarmSoundPlayer = MediaPlayer.create(mContext, R.raw.scan);
        alarmSoundPlayer.setLooping(true);
        alarmSoundPlayer.start();
    }

    public  void stop()
    {
        if(alarmSoundPlayer!= null) {
            alarmSoundPlayer.stop();
        }
    }

}
