package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.example.prototypesabre.firebaseFunction;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class GroupPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURi = null;
    firebaseFunction Firebasefunction = new firebaseFunction();
    StorageReference objectStorageRefrences;
    FirebaseFirestore objectFirebaseFirestore;
    ImageView postImageView;
    ImageButton chooseImage, publicPostButton, privatePostButton;
    EditText postSomethingEditText;
    Boolean showPost = false;
    Menu m;

    /*
     *Gets the post data from editText
     */
    private String postDescription;

    /*
     *post_type= public or private
     */
    private String postType = "Public";
    private String imageLink = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentiated_user_group_post_activity);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectStorageRefrences = FirebaseStorage.getInstance().getReference("ProfileImage");
        postImageView = findViewById(R.id.imagePostImageView);
        chooseImage = findViewById(R.id.uploadImageForPost);
        publicPostButton = findViewById(R.id.publicPostButton);
        privatePostButton = findViewById(R.id.privatePostbutton);
        postSomethingEditText = findViewById(R.id.postSomethingEditText);


        publicPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privatePostButton.getDrawable().getConstantState() == getResources().
                        getDrawable(R.drawable.privatepostfinalicon).getConstantState()) {
                    privatePostButton.setImageResource(R.drawable.privatepost);
                    publicPostButton.setImageResource(R.drawable.publicposticonfinal);
                    postType = "Public";
                }
            }
        });

        privatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicPostButton.getDrawable().getConstantState() == getResources().
                        getDrawable(R.drawable.publicposticonfinal).getConstantState()) {
                    publicPostButton.setImageResource(R.drawable.publicposticon);
                    privatePostButton.setImageResource(R.drawable.privatepostfinalicon);
                    postType = "Private";
                }
            }
        });


        /*
         *add a back button on the action bar
         */
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });


        /*
         *this is to keep track if there is any change on editText
         *If so then update using prepearmenuoption
         */

        postSomethingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    showPost = true;

                    onPrepareOptionsMenu(m);
                } else {

                    showPost = false;
                    onPrepareOptionsMenu(m);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /*
     *opens intent for choosing file
     * Allows user to get to their gallery
     */
    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    /*
     *We check if image pick request was successful
     * If successful then set it to our imageview
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageURi = data.getData();
            postImageView.setImageURI(imageURi);


        }
    }







    /*
     *Menu Item post and its control
     * After anything is written or image has been added post enables
     * Its disabled in xml
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_button, menu);
        m = menu;
        return super.onCreateOptionsMenu(menu);
    }


    /*
     *onClickListener for the menu option post
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.groupPostMenuItem:

                if (postImageView.getDrawable() != null) {

                }


                return true;
        }

        return false;

    }


    /*
     *This is for enabling if there is text
     * Disabling if there is not text
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (showPost) {
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /*
     *uploading image fies to firebase storage
     *get the link for the storage location
     */

    public void uploadImage() {

    }
}