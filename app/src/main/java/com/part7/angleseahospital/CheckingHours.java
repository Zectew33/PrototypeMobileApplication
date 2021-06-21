package com.part7.angleseahospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckingHours extends AppCompatActivity {

    DrawerLayout drawerLayout;

    EditText mPin;
    EditText editText;
    Button butt;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_hours);

        mPin = findViewById(R.id.pinInput);
        editText = findViewById(R.id.et_date);
        butt = findViewById(R.id.button);
        drawerLayout = findViewById(R.id.drawer_layout);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CheckingHours.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        editText.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listcheckinghour();
            }
        });
    }

    public void Listcheckinghour() {
        Intent intent = new Intent(this, ListCheckingHours.class);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        int week = 0;

        try {
            Date date = format.parse(editText.getText().toString().trim());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            week = cal.get(Calendar.WEEK_OF_YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        intent.putExtra("Pin", mPin.getText().toString().trim());
        intent.putExtra("Week", String.valueOf(week));
        intent.putExtra("Date", editText.getText().toString().trim());
        startActivity(intent);
    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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