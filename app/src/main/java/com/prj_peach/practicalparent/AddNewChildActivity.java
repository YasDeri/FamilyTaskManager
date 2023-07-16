package com.prj_peach.practicalparent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.TaskManager;

import java.io.InputStream;

public class AddNewChildActivity extends AppCompatActivity {

    private static final int REQUEST_IMG_CAPTURE = 0;
    private static final int REQUEST_IMG_GET = 1;
    private ChildManager childManager;
    private static boolean editMode;
    private static int indexOfChildForEdit;
    private String currentName;
    private String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_child);
        childManager = ChildManager.getInstance();

        if (editMode)
        {
            Child childForEdit = childManager.get(indexOfChildForEdit);
            imagePath = childForEdit.getImagePath();
            currentName = childForEdit.getName();
            if (!imagePath.equals("")) {
                ImageView avatarImg = findViewById(R.id.NewChild_Avatar);
                ImageHelper.setImageCropLarge(this, imagePath, avatarImg,
                        R.drawable.ic_person_black_24dp);
            }
            if(!currentName.equals("")) {
                EditText nameTextView = findViewById(R.id.editTextViewForName);
                nameTextView.setText(currentName);
            }
        }

        setUpToolBar();
        setSaveButton();
        registerImageButton();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = MainActivity.getIntent(AddNewChildActivity.this);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onStop() {
        super.onStop();

        ChildManager.getInstance().saveChild(AddNewChildActivity.this);
        CoinFlipRecordManager.getInstance().saveRecord(AddNewChildActivity.this);
        TaskManager.getInstance().saveTask(AddNewChildActivity.this);
    }

    private void setUpToolBar() {
        Toolbar addNewChildToolBar = findViewById(R.id.addNewChildToolBar);
        addNewChildToolBar.setTitleTextAppearance(this, R.style.FredokaOneFont);
        setSupportActionBar(addNewChildToolBar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setSaveButton() {
        ImageButton saveButton = findViewById(R.id.childInfoSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextView = findViewById(R.id.editTextViewForName);
                currentName = nameTextView.getText().toString();
                if(currentName.length() == 0)
                    Toast.makeText(AddNewChildActivity.this, "Please enter name.",
                            Toast.LENGTH_SHORT).show();
                else {
                    if(editMode) {
                        childManager.get(indexOfChildForEdit).setName(currentName);
                        if (imagePath != null) {
                            childManager.get(indexOfChildForEdit).setImagePath(imagePath);
                        }
                        Intent i = MainActivity.getIntent(AddNewChildActivity.this);
                        startActivity(i);
                    }
                    else {
                        Child newChild = new Child(currentName);
                        newChild.setImagePath(imagePath);
                        childManager.add(newChild);
                        Intent i = MainActivity.getIntent(AddNewChildActivity.this);
                        startActivity(i);
                    }
                }
            }
        });
    }

    private void registerImageButton() {
        Button button = findViewById(R.id.NewChild_setAvatarBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewChildActivity.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMG_CAPTURE);
                    }

                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMG_GET);
                    }

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_IMG_GET)
        {
            Uri selectedImgUri = data.getData();
            try {
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        selectedImgUri);

                imagePath = ImageHelper.saveBitmapImage(getApplicationContext(), imgBitmap);

                ImageView avatarImg = findViewById(R.id.NewChild_Avatar);
                ImageHelper.setImageCropLarge(this, imagePath, avatarImg, R.drawable.ic_person_black_24dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMG_CAPTURE) {
            Bitmap imgBitmap = (Bitmap) data.getExtras().get("data");
            imagePath = ImageHelper.saveBitmapImage(getApplicationContext(), imgBitmap);

            ImageView avatarImg = findViewById(R.id.NewChild_Avatar);
            ImageHelper.setImageCropLarge(this, imagePath, avatarImg, R.drawable.ic_person_black_24dp);
        }
    }

    public static Intent getIntent(Context context) {
        editMode = false;
        return new Intent(context, AddNewChildActivity.class);
    }

    public static Intent getIntentInEditMode(Context context, int childIndex) {
        editMode = true;
        indexOfChildForEdit = childIndex;
        return new Intent(context, AddNewChildActivity.class);
    }
}