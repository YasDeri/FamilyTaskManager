package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

import java.util.ArrayList;

public class NewTaskActivity extends AppCompatActivity {
    private static boolean editMode = false;
    private static Task task;
    private static int taskIndex;
    private ChildManager childManager;
    private TaskManager taskManager;
    private Child currentSelectedChild;
    private ListView childrenListView;
    private TextView currentlyAssignToTextView;
    private EditText taskName;
    private EditText description;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i;
            if(editMode) {
                editMode = false;
                i = TaskActivity.getIntent(NewTaskActivity.this, taskIndex);
            }else {
                i = TaskListActivity.getIntent(NewTaskActivity.this);
            }
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        setToolBar();
        populateChildList();
        registerClickCallBackForPickingChild();
        taskManager = TaskManager.getInstance();
        if(editMode) {
            editModeSetting();
        }
    }

    private void editModeSetting() {
        task = taskManager.get(taskIndex);
        taskName = findViewById(R.id.taskNameEditTextVIew);
        description = findViewById(R.id.taskDescriptionEditTextView);
        currentlyAssignToTextView = findViewById(R.id.currentlyAssignToTextView);
        task = taskManager.get(taskIndex);
        taskName.setText(task.getTaskName());
        description.setText(task.getDescription());
        currentSelectedChild = childManager.getChildByID(task.getChildID());
        currentlyAssignToTextView.setText(childManager.getChildByID(task.getChildID()).getName());
    }

    private void populateChildList() {
        childManager = ChildManager.getInstance();

        if (childManager.size() == 0) {
            TextView noChildrenMsg = findViewById(R.id.NewTask_noChildrenMsg);
            noChildrenMsg.setText(getResources().getString(R.string.tasks_noChildMessage));
        }

        ArrayList<String> arrayOfChildrenName = new ArrayList<String>();
        for(Child child: childManager){
            String name = child.getName();
            arrayOfChildrenName.add(name);
        }
        String[] nameArray = arrayOfChildrenName.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_in_children_list,
                nameArray
        );
        childrenListView = findViewById(R.id.pickChildForNewTaskListVIew);
        childrenListView.setAdapter(adapter);
    }

    //add back arrow to toolbar
    private void setToolBar() {
        Toolbar newTaskToolbar = findViewById(R.id.newTaskToolbar);
        newTaskToolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(newTaskToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    //let menu be set to the specific design we created.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    //Implement save button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveNewTaskButton) {
            taskName = findViewById(R.id.taskNameEditTextVIew);
            description = findViewById(R.id.taskDescriptionEditTextView);
            String errorMessage = "";
            if(taskName.getText().toString().length() == 0) {
                errorMessage += "Missing name\n";
            }
            if(description.getText().toString().length() == 0) {
                errorMessage += "Missing description\n";
            }
            if(currentSelectedChild == null) {
                errorMessage += "Assign a child for this task";
            }
            if(errorMessage.length() != 0) {
                Toast.makeText(NewTaskActivity.this, errorMessage,
                        Toast.LENGTH_SHORT).show();
            }else {
                if(editMode) {
                    Task currentTask = taskManager.get(taskIndex);
                    currentTask.setTaskName(taskName.getText().toString());
                    currentTask.setDescription(description.getText().toString());
                    currentTask.setChild(currentSelectedChild);
                    editMode = false;
                    Intent i = TaskListActivity.getIntent(NewTaskActivity.this);
                    startActivity(i);
                }else {
                    Task task = new Task(taskName.getText().toString(),
                            description.getText().toString(),
                            currentSelectedChild.getID());
                    taskManager.add(task);
                    Intent i = TaskListActivity.getIntent(NewTaskActivity.this);
                    startActivity(i);
                }
            }


        }
        return super.onOptionsItemSelected(item);
    }




    private void registerClickCallBackForPickingChild() {
        childrenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedChild = childManager.get(position);
                currentlyAssignToTextView = findViewById(R.id.currentlyAssignToTextView);
                currentlyAssignToTextView.setText(currentSelectedChild.getName());
            }
        });
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, NewTaskActivity.class);
    }

    //when user want to edit the task.
    public static Intent getIntentAndEnableEditMode(Context context, int taskId) {
        editMode = true;
        taskIndex = taskId;
        return new Intent(context, NewTaskActivity.class);
    }

}