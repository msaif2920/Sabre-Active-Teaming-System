package com.example.prototypesabre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.AuthenticatedUserFragment.Actions.AuthenticateduserActivity;
import com.example.prototypesabre.GuestUser.guestUserActvity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //All the necessary variables
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    String SupEmail = "";
    //String supPassWord = "";
    Boolean intial = false;

    ArrayList<String> blockList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        checkIfSuperUser();
        getBlockList();

    }


    //This is the register now activity intnet
    public void register(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisternowActivity.class);
        startActivity(intent);
    }


    //Guest Mode Intent
    public void enterGuestMode(View view) {
        Intent intent = new Intent(getApplicationContext(), guestUserActvity.class);
        startActivity(intent);
    }


    //Sign in Button
    public void signIn(View view) {
        //making sure all the emails are upper class or else there will be duplicate data
        String email = emailEditText.getText().toString().toUpperCase().trim();
        String password = passwordEditText.getText().toString();




        //if no input then send a toast message
        if (email.trim().equals("") || password.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter your credentials", Toast.LENGTH_SHORT).show();


            //if its super User email then take it to super user activity
        } else {
            for (int i = 0; i < blockList.size(); i++) {

                if (email.equals(blockList.get(i))) {
                    Toast.makeText(this, "Sorry you are permanently blocked", Toast.LENGTH_SHORT).show();
                    break;
                } else if (i == blockList.size() - 1) {
                    intial = true;

                }
                if (intial) {
                    signInExistingUser(email, password);
                    emailEditText.setText("");
                    passwordEditText.setText("");
                }
            }

        }


    }



    //Here signing in users if they exist in the system
    public void signInExistingUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in Successful", "signInWithEmail:success");

                            if (SupEmail.equals(email)) {
                                Intent intent = new Intent(getApplicationContext(), SuperUserActivity.class);
                                startActivity(intent);
                            } else {


                                DocumentReference docRef = db.collection("Users").document(email);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                //checking if its first time login
                                                if (document.get("Login").equals("1")) {
                                                    Intent intent = new Intent(getApplicationContext(), firstTimelogin.class);
                                                    intent.putExtra("Email", email);
                                                    startActivity(intent);
                                                }
                                                //else sign in regularly
                                                else {
                                                    Intent intent = new Intent(getApplicationContext(), AuthenticateduserActivity.class);
                                                    startActivity(intent);
                                                }

                                                //third task
                                            } else {
                                                if (!SupEmail.isEmpty()) {


                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }

                                            //second task
                                        } else {
                                            Toast.makeText(getApplicationContext(), "No Internet Connection failed to retrieve data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            //first task
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), authenticatedUserForgotPassword.class);
        startActivity(intent);
    }

    public void checkIfSuperUser() {
        DocumentReference docRef = db.collection("SuperUser").document("Credential");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SupEmail = document.get("email1").toString();
                        // supPassWord = (String) document.get("password");
                    }
                } else {

                    Log.d("DATA", "get failed with ", task.getException());
                }
            }
        });
    }

    public void getBlockList() {
        db.collection("Block List").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        blockList.add(document.getId());
                    }
                } else {

                }
            }
        });

    }
}
