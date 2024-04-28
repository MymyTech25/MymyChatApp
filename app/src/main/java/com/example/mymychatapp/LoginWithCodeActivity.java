package com.example.mymychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginWithCodeActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verifCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    EditText codeInput;
    ProgressBar progressBar;
    TextView resendcode;
    Button nextBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_code);

        codeInput = findViewById(R.id.login_codeId);
        progressBar = findViewById(R.id.login_progressBar);
        nextBtn = findViewById(R.id.login_NextBtn);
        resendcode = findViewById(R.id.resent_codeTV);

        phoneNumber = getIntent().getExtras().getString("phone");

        sendCode(phoneNumber,false);

        nextBtn.setOnClickListener(v -> {
            String enteredCode = codeInput.getText().toString();
           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifCode, enteredCode);
           signIn(credential);
           setInProgressBar(true);

        });

        resendcode.setOnClickListener((v) -> {
            sendCode(phoneNumber,true);
        });
    }

    void sendCode(String phoneNumber, boolean isResend){
        startResend();
        setInProgressBar(true);
        PhoneAuthOptions.Builder builder=
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgressBar(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Utility.showToast(getApplicationContext(),"Your code is incorrect :-( Verification failed");
                                setInProgressBar(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verifCode = s;
                                resendingToken = forceResendingToken;
                                Utility.showToast(getApplicationContext(),"Code sent successfully");
                                setInProgressBar(false);

                            }
                        });
    if (isResend){
        PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
    } else {
        PhoneAuthProvider.verifyPhoneNumber(builder.build());

    }

    }

    void setInProgressBar(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
      //login part and navigate to the next activity
        setInProgressBar(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginWithCodeActivity.this, LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);

            } else {
                    Utility.showToast(getApplicationContext(), "Your code is wrong :-( Verification failed");
                }
            }
        });
    }

    void startResend(){
        resendcode.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendcode.setText("Resend your code in " +timeoutSeconds + "seconds");
                if (timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                       resendcode.setEnabled(true);
                    });
                }

            }
        },0,1000);
    }
}