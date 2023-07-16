package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FlipHistoryActivity extends AppCompatActivity {

    List<CoinFlipRecord> flipRecords = new ArrayList<CoinFlipRecord>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);

        setupUI();
        populateList(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(FlipHistoryActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(FlipHistoryActivity.this);
        TaskManager.getInstance().saveTask(FlipHistoryActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = tossCoinActivity.getIntent(FlipHistoryActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void setupUI() {
        Toolbar toolbar = findViewById(R.id.FlipHistory_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        RadioGroup filterButtons = findViewById(R.id.FlipHistory_radioGroup);
        filterButtons.check(R.id.FlipHistory_allButton);
    }

    public void onFlipHistoryRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.FlipHistory_allButton:
                if (checked) {
                    populateList(false);
                }
                break;
            case R.id.FlipHistory_CurrentButton:
                if (checked) {
                    populateList(true);
                }
                break;
        }
    }

    public void populateList(boolean filterCurrent) {
        //create list of items
        CoinFlipRecordManager recordManager = CoinFlipRecordManager.getInstance();
        ChildManager childManager = ChildManager.getInstance();

        Child currentChild = ChildManager.getNobody();
        if (filterCurrent) {
            UUID prevChildID = recordManager.getLastChildId();
            currentChild = childManager.getNext(prevChildID);
        }

        flipRecords = new ArrayList<>();

        for(CoinFlipRecord record : recordManager) {
            if (filterCurrent && !record.getPickedByID().equals(currentChild.getID())) {
                continue;
            }

            flipRecords.add(0, record);
        }

        ArrayAdapter<CoinFlipRecord> adapter = new CustomRecordAdapter();

        //configure ListView
        ListView list = findViewById(R.id.historyListView);
        list.setAdapter(adapter);
    }

    private class CustomRecordAdapter extends ArrayAdapter<CoinFlipRecord> {
        public CustomRecordAdapter() {
            super(FlipHistoryActivity.this, R.layout.flip_history_list_item, flipRecords);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.flip_history_list_item,
                        parent, false);
            }

            CoinFlipRecord currentRecord = flipRecords.get(position);
            ChildManager children = ChildManager.getInstance();

            String childName;
            try {
                childName = children.getChildByID(currentRecord.getPickedByID()).getName();
            } catch (Exception e) {
                childName = "Deleted Child";
            }

            TextView nameText = itemView.findViewById(R.id.FlipHistoryItem_childName);
            nameText.setText(childName);
            TextView resultText = itemView.findViewById(R.id.FlipHistoryItem_result);
            resultText.setText(String.format(getResources().getString(R.string.FlipHistory_resultText),
                    currentRecord.getResult().toString(),
                    currentRecord.childWon() ? "won" : "lost"));
            TextView timeText = itemView.findViewById(R.id.FlipHistoryItem_date);
            timeText.setText(currentRecord.getDate().toString());

            if (currentRecord.childWon()) {
                itemView.setBackground(getResources().getDrawable(R.drawable.flip_history_item_won));
            } else {
                itemView.setBackground(getResources().getDrawable(R.drawable.flip_history_item_lost));
            }
            ImageView childAvatar = itemView.findViewById(R.id.filpHistoryListItem_avatar);
            ImageHelper.setImageCropTiny(FlipHistoryActivity.this, children.getChildByID(currentRecord.getPickedByID()).getImagePath(), childAvatar,
                    R.drawable.ic_person_black_24dp);
            return itemView;
        }
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }
}