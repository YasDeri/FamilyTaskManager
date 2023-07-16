package com.prj_peach.practicalparent;

import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DeepBreathActivity extends AppCompatActivity {
    Context context;
    public static Intent getIntent(Context context) {
        return new Intent(context, DeepBreathActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);
        setUpToolBar();
        inButtonListeners();
        context = this;
    }

    void inButtonListeners(){
        Button btn = findViewById(R.id.inBtn);
        TextView helpText = findViewById(R.id.helpTxt);
        ImageView circleImg = findViewById(R.id.circleImageView);
        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        animZoomIn.setFillEnabled(true);
        animZoomIn.setFillAfter(true);
        final Handler handler = new Handler(Looper.getMainLooper());
        final Handler handler2 = new Handler(Looper.getMainLooper());
        final MediaPlayer[] mp = {MediaPlayer.create(this, R.raw.wind)};

        EditText countBtn = findViewById(R.id.count);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int counts = Integer.parseInt(countBtn.getText().toString());
                if(counts >= 1 && counts <= 10) {
                    mp[0] = MediaPlayer.create(context, R.raw.wind);
                    mp[0].start();
                    countBtn.setClickable(false);
                    countBtn.setFocusable(false);
                    counts --;
                    countBtn.setText(String.valueOf(counts));
                    helpText.setVisibility(View.INVISIBLE);
                    circleImg.startAnimation(animZoomIn);
                    //stopping animation after 10 mints
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            circleImg.clearAnimation();
                            mp[0].reset();
                        }
                    }, 10000);

                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn.setText("Out");
                        }
                    }, 3000);

                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Error");
                    builder1.setMessage("This operation is not valid!");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                return true;
            }
        });

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    helpText.setVisibility(View.VISIBLE);
                    btn.setText("Begin");
                    circleImg.clearAnimation();
                    mp[0].reset();
                    handler.removeCallbacksAndMessages(null);
                    handler2.removeCallbacksAndMessages(null);
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btn.setText("In");
                }

                return true;
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(DeepBreathActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.deep_breath_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }
}