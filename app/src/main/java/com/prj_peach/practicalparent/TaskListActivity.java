package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecord;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskListActivity extends AppCompatActivity {

    private List<Task> tasksToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        populateList();
        setToolBar();
        registerNewTaskButton();
        registerClickCallBackForPickingChild();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(TaskListActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerNewTaskButton() {
        FloatingActionButton newTaskFab = findViewById(R.id.newTaskFab);
        newTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = NewTaskActivity.getIntent(TaskListActivity.this);
                startActivity(i);
            }
        });
    }

    private void registerClickCallBackForPickingChild() {
        ListView taskList = findViewById(R.id.taskListView);
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Intent i = TaskActivity.getIntent(TaskListActivity.this, position);
                 startActivity(i);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(TaskListActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(TaskListActivity.this);
        TaskManager.getInstance().saveTask(TaskListActivity.this);
    }

    private void populateList() {
        ChildManager children = ChildManager.getInstance();

        tasksToDisplay = new ArrayList<>();
        for (Task task : TaskManager.getInstance()) {
            if (children.getChildByID(task.getChildID()).equals(
                    ChildManager.getNobody()))
            {
                task.setChild(children.get(0));
            }
            tasksToDisplay.add(task);
        }

        ArrayAdapter<Task> adapter = new CustomTaskAdapter();
        ListView taskList = findViewById(R.id.taskListView);
        taskList.setAdapter(adapter);
    }

    private class CustomTaskAdapter extends ArrayAdapter<Task> {
        public CustomTaskAdapter() {
            super(TaskListActivity.this, R.layout.task_list_item, tasksToDisplay);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_list_item, parent, false);
            }

            Task currentTask = tasksToDisplay.get(position);
            Child assignedChild = ChildManager.getInstance().getChildByID(currentTask.getChildID());

            TextView childName = itemView.findViewById(R.id.TaskListItem_childName);
            if (assignedChild.equals(ChildManager.getNobody())) {
                childName.setText(getResources().getString(R.string.tasks_noChildMessage));
            } else {
                childName.setText(assignedChild.getName());
            }

            TextView taskName = itemView.findViewById(R.id.TaskListItem_taskName);
            taskName.setText(currentTask.getTaskName());
            ImageView childAvatar = itemView.findViewById(R.id.TaskListItem_avatar);
            ImageHelper.setImageCropTiny(TaskListActivity.this, assignedChild.getImagePath(), childAvatar,
                    R.drawable.ic_person_black_24dp);

            return itemView;
        }
    }

    private void setToolBar() {
        Toolbar taskListToolbar = findViewById(R.id.taskListToolbar);
        taskListToolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);

        setSupportActionBar(taskListToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

}