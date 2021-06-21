package com.part7.angleseahospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.concurrent.Executor;

public class Clockin extends AppCompatActivity {

    DrawerLayout drawerLayout;
    EditText mEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clockin);

        drawerLayout = findViewById(R.id.drawer_layout);
        TextView msg_txt = findViewById(R.id.txt_msg);
        Button login_btn = findViewById(R.id.login_btn);
        Button login_btn1 = (Button) findViewById(R.id.login_btn1);
        mEdittext = findViewById(R.id.noteIn);

        //Create the BiometricManager and check if user can use fingerprint sensor
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) { //Different possibilities
            case BiometricManager.BIOMETRIC_SUCCESS: //We can use biometric sensor
                msg_txt.setText("You can use the fingerprint sensor to clock in!");
                msg_txt.setTextColor(Color.parseColor("#000000"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: //Device don't have a fingerprint sensor
                msg_txt.setText("The device doesn't have a fingerprint sensor!");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msg_txt.setText("The biometric sensor is currently unavailable!");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msg_txt.setText("Your device don't have any fingerprint saved, please check your security settings!");
                login_btn.setVisibility(View.GONE);
                break;
        }

        //Create our biometric sensor
        //First we need to create an executor
        Executor executor = ContextCompat.getMainExecutor(this);
        //Create a biometric prompt callback
        //This will give us the result of the authentication and if we can login or not
        BiometricPrompt biometricPrompt = new BiometricPrompt(Clockin.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override //It's called if there is an error while the authentication
            public void onAuthenticationError(int errorCode, @NonNull @org.jetbrains.annotations.NotNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override //It's called when the authentication is success
            public void onAuthenticationSucceeded(@NonNull @org.jetbrains.annotations.NotNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(getApplicationContext(), "Clock in successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override //It's called if we have failed authentication
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        //Create our biometric dialog
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Clock in")
                .setDescription("Use your fingerprint to clock in!")
                .setNegativeButtonText("Cancel")
                .build();

        //Call the dialog when the user press the login button
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 10000);
            }
        });

        login_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
    }

    public void openNewActivity() {
        Intent intent = new Intent(this, NurseLogin.class);
        intent.putExtra("noteIn", mEdittext.getText().toString().trim());
        Log.d("Text", mEdittext.getText().toString().trim());
        startActivity(intent);
    }

    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickLogin(View view) {
        MainActivity.redirectActivity(this, Login.class);
    }

    public void ClickExit(View view) {
        MainActivity.exit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
}