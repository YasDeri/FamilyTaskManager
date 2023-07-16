package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Child> childList;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!ChildManager.hasInstance()) {
            ChildManager childManager = ChildManager.getInstance();
            childManager.loadChild(MainActivity.this);
        }

        if (!CoinFlipRecordManager.hasInstance()) {
            CoinFlipRecordManager flipManager = CoinFlipRecordManager.getInstance();
            flipManager.loadRecord(MainActivity.this);
        }

        if (!TaskManager.hasInstance()) {
            TaskManager taskManager = TaskManager.getInstance();
            taskManager.loadTask(MainActivity.this);
        }
        setupUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(MainActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(MainActivity.this);
        TaskManager.getInstance().saveTask(MainActivity.this);
    }

    private void setupUI() {
        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);

        populateChildList();

        registerTimeoutButton();
        registerCoinFlipButton();
        registerTaskButton();
        registerDeepBreathButton();
        registerHelpPagebutton();
        registerAddChildButton();
        registerListClick();
    }

    private void populateChildList() {
        childList = new ArrayList<>();
        for (Child child : ChildManager.getInstance()) {
            childList.add(child);
        }

        ArrayAdapter<Child> adapter = new CustomChildAdapter();
        ListView childListView = findViewById(R.id.Main_childrenList);
        childListView.setAdapter(adapter);
    }

    private class CustomChildAdapter extends ArrayAdapter<Child> {
        public CustomChildAdapter() {
            super(MainActivity.this, R.layout.child_list_item, childList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.child_list_item, parent, false);
            }

            Child currentChild = childList.get(position);

            // get child task count:
            int taskCount = 0;
            for (Task task : TaskManager.getInstance()) {
                if (task.getChildID().equals(currentChild.getID())) {
                    taskCount++;
                }
            }

            // set view items
            ImageView avatarImg = itemView.findViewById(R.id.ChildItem_avatar);
            ImageHelper.setImageCropSmall(MainActivity.this, currentChild.getImagePath(), avatarImg, R.drawable.ic_person_black_24dp);
            TextView nameText = itemView.findViewById(R.id.ChildItem_childName);
            nameText.setText(currentChild.getName());
            TextView taskCountText = itemView.findViewById(R.id.ChildItem_taskCount);
            taskCountText.setText(String.format(getResources().getString(R.string.childItem_taskCount), taskCount));

            return itemView;
        }
    }

    private void registerTaskButton() {
        CardView button = findViewById(R.id.Main_tasksCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = TaskListActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerTimeoutButton() {
        CardView button = findViewById(R.id.Main_alarmCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = AlarmActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerCoinFlipButton() {
        CardView button = findViewById(R.id.Main_coinFlipCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = tossCoinActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }
    private void registerDeepBreathButton() {
        CardView button = findViewById(R.id.Main_breathCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = DeepBreathActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerHelpPagebutton() {
        CardView button = findViewById(R.id.Main_helpCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = HelpScreenActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerAddChildButton() {
        Button button = findViewById(R.id.Main_addChildButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = AddNewChildActivity.getIntent(MainActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerListClick() {
        ListView childrenList = findViewById(R.id.Main_childrenList);
        childrenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Child child = childList.get(position);
                Intent intent = ChildActivity.getIntent(MainActivity.this, child.getID());
                startActivity(intent);
            }
        });
    }
}

