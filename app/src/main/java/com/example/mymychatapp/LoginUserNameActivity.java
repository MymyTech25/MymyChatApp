package com.example.mymychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.mymychatapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.ktx.Firebase;

public class LoginUserNameActivity extends AppCompatActivity {
    EditText usernameInput;
    Button letmeinBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        usernameInput = findViewById(R.id.login_username);
        letmeinBtn = findViewById(R.id.login_letMeIn);
        progressBar = findViewById(R.id.login_progressBar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        letmeinBtn.setOnClickListener((v -> {
            setUsername();
        }));

    }

    void setUsername(){
        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length()<5){
            usernameInput.setError("Username length should be at least 5 chars");
            return;
        }
        setInProgressBar(true);
        if (userModel!=null){
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber,username, Timestamp.now(), Utility.currentUserId());
        }

        Utility.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgressBar(false);
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginUserNameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    void getUsername(){
        setInProgressBar(true);
        Utility.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgressBar(false);
                if (task.isSuccessful()){
                     userModel = task.getResult().toObject(UserModel.class);
                    if (userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                    }

                }
            }
        });

    }
    void setInProgressBar(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letmeinBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letmeinBtn.setVisibility(View.VISIBLE);
        }
    }
}