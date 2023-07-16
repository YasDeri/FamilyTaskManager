package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecord;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.TaskManager;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class tossCoinActivity extends AppCompatActivity implements ChooseChildDialog.ChooseChildDialogListener {

    private boolean head = true;
    private CoinFlipRecord.FlipResult selectedSide;
    private ImageView coinImage;
    private Child pickingChild;
    private MediaPlayer alarmSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toss_coin);
        alarmSoundPlayer = MediaPlayer.create(tossCoinActivity.this, R.raw.ding);
        alarmSoundPlayer.setLooping(false);
        Button tossAgainBtn = findViewById(R.id.Coin_flipAgainBtn);
        tossAgainBtn.setVisibility(View.GONE);

        setToolBar();
        setTossCoin();
        updateUI();
        registerChooseChildButton();
        registerHistoryButton();
        registerTossAgainButton();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(tossCoinActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(tossCoinActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(tossCoinActivity.this);
        TaskManager.getInstance().saveTask(tossCoinActivity.this);
    }

    @Override
    public void applyChosenChild(Child chosenChild) {
        pickingChild = chosenChild;

        TextView choosingChildText = findViewById(R.id.Coin_childNameLabel);
        TextView childNameText = findViewById(R.id.Coin_childName);
        ImageView childAvatar = findViewById(R.id.Coin_childAvatar);
        LinearLayoutCompat choiceButtons = findViewById(R.id.coin_choiceLayout);

        if (pickingChild.equals(ChildManager.getNobody())) {
            choosingChildText.setVisibility(View.INVISIBLE);
            childNameText.setVisibility(View.INVISIBLE);
            childAvatar.setVisibility(View.INVISIBLE);
            choiceButtons.setVisibility(View.INVISIBLE);
        } else {
            choosingChildText.setVisibility(View.VISIBLE);
            choiceButtons.setVisibility(View.VISIBLE);
            childNameText.setText(chosenChild.getName());
            ImageHelper.setImageCropTiny(this, chosenChild.getImagePath(), childAvatar, R.drawable.ic_person_black_24dp);
        }
    }

    private void registerChooseChildButton() {
        Button chooseBtn = findViewById(R.id.Coin_chooseChildBtn);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseChildDialog dialog = new ChooseChildDialog();
                dialog.show(getSupportFragmentManager(), "ChooseChildDialog");
            }
        });
    }

    private void registerHistoryButton() {
        Button viewHistBtn = findViewById(R.id.Coin_viewHistoryButton);
        viewHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create and start intent to go to history screen
                Intent i = FlipHistoryActivity.getIntent(tossCoinActivity.this);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerTossAgainButton() {
        Button tossAgainBtn = findViewById(R.id.Coin_flipAgainBtn);
        tossAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button tossAgainBtn = findViewById(R.id.Coin_flipAgainBtn);
                tossAgainBtn.setVisibility(View.GONE);
                updateUI();
            }
        });
    }

    private void updateUI() {
        ChildManager children = ChildManager.getInstance();
        Button tossCoinButton = findViewById(R.id.tossButton);
        tossCoinButton.setEnabled(true);
        TextView choosingChildText = findViewById(R.id.Coin_childNameLabel);
        TextView childNameText = findViewById(R.id.Coin_childName);
        ImageView childAvatar = findViewById(R.id.Coin_childAvatar);
        LinearLayoutCompat choiceButtons = findViewById(R.id.coin_choiceLayout);
        TextView flipResultText = findViewById(R.id.Coin_flipResult);

        flipResultText.setVisibility(View.INVISIBLE);
        choosingChildText.setVisibility(View.VISIBLE);
        childNameText.setVisibility(View.VISIBLE);
        childAvatar.setVisibility(View.VISIBLE);
        choiceButtons.setVisibility(View.VISIBLE);

        if (children.size() <= 0) {
            choosingChildText.setVisibility(View.INVISIBLE);
            childNameText.setVisibility(View.INVISIBLE);
            childAvatar.setVisibility(View.INVISIBLE);
            choiceButtons.setVisibility(View.INVISIBLE);
        } else {
            UUID prevChildId = CoinFlipRecordManager.getInstance().getLastChildId();
            pickingChild = children.getNext(prevChildId);

            childNameText.setText(pickingChild.getName());
            ImageHelper.setImageCropTiny(this, pickingChild.getImagePath(), childAvatar, R.drawable.ic_person_black_24dp);

            RadioGroup choiceButtonGroup = findViewById(R.id.Coin_choiceRadioButtons);
            choiceButtonGroup.check(R.id.Coin_headsBtn);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.FlipHistory_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setTossCoin() {
        coinImage = findViewById(R.id.coinImageView);
        Button tossCoinButton = findViewById(R.id.tossButton);
        Random r = new Random();
        TextView resultTextView = findViewById(R.id.Coin_flipResult);
        tossCoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText(R.string.empty);
                tossCoinButton.setEnabled(false);
                ScaleAnimation oneToZeroAnimation = new ScaleAnimation(1f, 1f, 1f, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                ScaleAnimation zeroToOneAnimation = new ScaleAnimation(1f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);

                int duration = 90;
                oneToZeroAnimation.setDuration(duration);
                zeroToOneAnimation.setDuration(duration);
                int numberOfIteration = 10 + r.nextInt(10);
                alarmSoundPlayer.start();
                for(int i = 0; i < numberOfIteration; i++) {
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            coinImage.startAnimation(oneToZeroAnimation);
                        }
                    }, 2*i*duration);

                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            swapImage();
                            coinImage.startAnimation(zeroToOneAnimation);
                        }
                    }, ((2*i+1)*duration));
                }
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        Button tossAgainBtn = findViewById(R.id.Coin_flipAgainBtn);
                        tossAgainBtn.setVisibility(View.VISIBLE);
                        addResult();
                    }
                }, ((2*numberOfIteration)*duration));
            }
        });
    }

    private void addResult() {
        TextView resultText = findViewById(R.id.Coin_flipResult);
        CoinFlipRecord.FlipResult result;
        if(head) {
            resultText.setText(getResources().getString(R.string.coin_heads));
            result = CoinFlipRecord.FlipResult.HEADS;
        } else {
            resultText.setText(getResources().getString(R.string.coin_tails));
            result = CoinFlipRecord.FlipResult.TAILS;
        }
        resultText.setVisibility(View.VISIBLE);

        Animation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(1000);
        resultText.startAnimation(fade);
        resultText.setVisibility(View.VISIBLE);

        if (pickingChild != null && !pickingChild.equals(ChildManager.getNobody())) {
            boolean won = false;
            if (head && selectedSide == CoinFlipRecord.FlipResult.HEADS ||
                    !head && selectedSide == CoinFlipRecord.FlipResult.TAILS) {
                won = true;
            }
            addNewCoinFlipRecord(pickingChild, result, won);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.Coin_headsBtn:
                if (checked)
                    selectedSide = CoinFlipRecord.FlipResult.HEADS;
                break;
            case R.id.Coin_tailsBtn:
                if (checked)
                    selectedSide = CoinFlipRecord.FlipResult.TAILS;
                break;
        }
    }

    private void swapImage() {
        if(head) {
            coinImage.setImageResource(R.drawable.coin_tail);
            head = false;
        }
        else {
            coinImage.setImageResource(R.drawable.coin_head);
            head = true;
        }
    }

    private void addNewCoinFlipRecord(Child pickingChild, CoinFlipRecord.FlipResult result, boolean won) {
        CoinFlipRecord record = new CoinFlipRecord(
                pickingChild.getID(),
                result,
                won,
                Calendar.getInstance().getTime()
        );

        CoinFlipRecordManager.getInstance().add(record);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, tossCoinActivity.class);
    }

}