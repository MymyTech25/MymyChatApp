package com.example.mymychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    ProgressBar progressBar;
    Button sendCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.login_countryCode);
        phoneInput = findViewById(R.id.login_mobileNumber);
        progressBar = findViewById(R.id.login_progressBar);
        sendCodeBtn = findViewById(R.id.send_codeBtn);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendCodeBtn.setOnClickListener((v)->{
            if (!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number invalid :-(");
                return;
            }
            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginWithCodeActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);

        });
    }
}