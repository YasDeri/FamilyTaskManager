package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HelpScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        populateNameListView();
        //populateCopyrightListView();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(HelpScreenActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void populateNameListView() {
        String[] names = {"Pavanpal Bhasin" , "Lukas Kuppers" , "ZeMing Gong", "Yasaman Deriszadeh"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.developernames,
                names                );

        ListView listview = findViewById(R.id.developersList);
        listview.setAdapter(adapter);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, HelpScreenActivity.class);
    }


}