package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

import org.w3c.dom.Text;

import java.util.UUID;

public class ChildActivity extends AppCompatActivity {

    private static final String EXTRAS_CHILD_ID = "com.prj_peach.practicalparent.ChildActivity - childID";

    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        extractDataFromIntent();
        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_child:
                Intent intent = AddNewChildActivity.getIntentInEditMode(ChildActivity.this,
                        ChildManager.getInstance().collection.indexOf(child));
                startActivity(intent);
                return true;
            case R.id.action_delete_child:
                ChildManager manager = ChildManager.getInstance();
                manager.delete(manager.collection.indexOf(child));
                Intent backIntent = MainActivity.getIntent(ChildActivity.this);
                startActivity(backIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUI() {
        Toolbar toolbar = findViewById(R.id.Child_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TextView childNameText = findViewById(R.id.Child_childName);
        childNameText.setText(child.getName());

        ImageView childAvatarImg = findViewById(R.id.Child_avatarImg);
        ImageHelper.setImageCropLarge(this, child.getImagePath(), childAvatarImg, R.drawable.ic_person_black_24dp);

        // set task amount
        int count = 0;
        for (Task task : TaskManager.getInstance()) {
            if (task.getChildID().equals(child.getID())) {
                count++;
            }
        }
        TextView tasksCountText = findViewById(R.id.Child_taskCount);
        tasksCountText.setText(String.format(getResources().getString(R.string.child_tasksCount), count));
    }

    public static Intent getIntent(Context context, UUID childID) {
        Intent intent = new Intent(context, ChildActivity.class);
        intent.putExtra(EXTRAS_CHILD_ID, childID.toString());
        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        String childIDRaw = intent.getStringExtra(EXTRAS_CHILD_ID);
        UUID childID = UUID.fromString(childIDRaw);
        child = ChildManager.getInstance().getChildByID(childID);
    }
}