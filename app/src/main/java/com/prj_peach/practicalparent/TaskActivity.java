package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

public class TaskActivity extends AppCompatActivity {
    private static int taskIndex;
    private ChildManager childManager;
    private Task task;
    private TaskManager taskManager;
    private Button MissionCompletedButton;
    private TextView Task_taskNameTextView;
    private TextView Task_descriptionTextView;
    private TextView Task_childNameTextView;
    private Toolbar taskActivityToolbar;
    private ImageView childPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setToolBar();
        printTaskInfo();
        registerMissionCompletedButton();
    }

    private void setToolBar() {
        taskActivityToolbar = findViewById(R.id.taskToolBar);
        taskActivityToolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);

        setSupportActionBar(taskActivityToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        ChildManager.getInstance().saveChild(TaskActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(TaskActivity.this);
        TaskManager.getInstance().saveTask(TaskActivity.this);
    }

    private void registerMissionCompletedButton() {

        MissionCompletedButton = findViewById(R.id.Task_completeButton);
        MissionCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.toNextChild();
                Child newChild = childManager.getChildByID(task.getChildID());
                Task_childNameTextView.setText(newChild.getName());
                ImageView childAvatar = findViewById(R.id.Task_childAvatar);
                ImageHelper.setImageCropSmall(TaskActivity.this, newChild.getImagePath(), childAvatar,
                        R.drawable.ic_person_black_24dp);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = TaskListActivity.getIntent(TaskActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void printTaskInfo() {
        //Views
        Task_taskNameTextView = findViewById(R.id.Task_taskNameTextView);
        Task_descriptionTextView = findViewById(R.id.Task_descriptionTextView);
        Task_childNameTextView = findViewById(R.id.Task_childName);
        childPhotoImageView = findViewById(R.id.Task_childAvatar);

        childManager = ChildManager.getInstance();
        taskManager = TaskManager.getInstance();
        task = taskManager.get(taskIndex);
        Task_taskNameTextView.setText(task.getTaskName());
        Task_descriptionTextView.setText(task.getDescription());
        Task_childNameTextView.setText(childManager.getChildByID(task.getChildID()).getName());
        String imagePath = childManager.getChildByID(task.getChildID()).getImagePath();
        ImageHelper.setImageCropSmall(this, imagePath, childPhotoImageView,
                R.drawable.ic_person_black_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteCurrentTaskButton) {
            taskManager.delete(taskIndex);
            Intent i = TaskListActivity.getIntent(TaskActivity.this);
            startActivity(i);
        }
        else if(item.getItemId() ==R.id.taskEditButton) {
            Intent i = NewTaskActivity.getIntentAndEnableEditMode(TaskActivity.this, taskIndex);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getIntent(Context context, int taskId) {
        taskIndex = taskId;
        return new Intent(context, TaskActivity.class);
    }
}