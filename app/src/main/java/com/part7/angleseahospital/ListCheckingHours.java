package com.part7.angleseahospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListCheckingHours extends AppCompatActivity {
    private int DELAY = 1500;
    private static final String TAG = "ListCheckingHours";
    private ArrayList<User> mUsers;
    private ArrayList<Hours> hoursList;
    private ArrayList<Staff> mStaff;
    private StorageReference mStorageRef;
    private Button mButton;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("TimeLogs");
        mUsers = new ArrayList<>();
        hoursList = new ArrayList<>();
        setContentView(R.layout.activity_list_checking_hours);
        Log.d(TAG, "onCreate: Started");
        mButton = findViewById(R.id.button5);
        drawerLayout = findViewById(R.id.drawer_layout);
        UpdateList();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView mListView = (ListView) findViewById(R.id.listView);
                HoursListAdapter adapter = new HoursListAdapter(ListCheckingHours.this, R.layout.custom_view_list_check_hour, hoursList);
                adapter.clear();
                mListView.setAdapter(adapter);
                UpdateList();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateList();
            }
        }, DELAY);

    }

    public String GetDay(String InputDate) {
        String Date = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = format.parse(InputDate);
            DateFormat format2 = new SimpleDateFormat("EEEE");
            Date = format2.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Date;
    }

    public Integer GetHours(String Time1, String Time2) {
        int Hours = 0;

        try {
            LocalTime t1 = LocalTime.parse(Time1);
            LocalTime t2 = LocalTime.parse(Time2);
            Duration diff = Duration.between(t2, t1);
            Hours = Integer.parseInt(String.valueOf(diff.toHours()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Hours;
    }

    public void UpdateList() {
        mStaff = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ListCheckingHours.this, "There are no Users", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Integer ID = Integer.parseInt(snapshot1.child("id").getValue().toString());
                        String fName = snapshot1.child("fname").getValue().toString();
                        String lName = snapshot1.child("lname").getValue().toString();
                        String DoB = snapshot1.child("dob").getValue().toString();
                        String email = snapshot1.child("email").getValue().toString();
                        Integer phoneNum = Integer.parseInt(snapshot1.child("phoneNumber").getValue().toString());
                        Integer pin = Integer.parseInt(snapshot1.child("pin").getValue().toString());
                        String notes = snapshot1.child("notes").getValue().toString();
                        String photo = snapshot1.child("photo").getValue().toString();
                        if (mUsers.size() == 0) {
                            mUsers.add(new User(ID, fName, lName, DoB, email, phoneNum, pin, notes, photo));
                        } else {
                            boolean isFound = false;
                            for (int i = 0; i < mUsers.size(); i++) {
                                if (mUsers.get(i).getID() == ID) {
                                    isFound = true;
                                }
                            }
                            if (!isFound) {
                                mUsers.add(new User(ID, fName, lName, DoB, email, phoneNum, pin, notes, photo));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
        Intent intent = getIntent();
        String Pin = intent.getExtras().getString("Pin");

        if (!(mUsers.size() == 0)) {
            for (int i = 0; i < mUsers.size(); i++) {
                if (Pin.matches(String.valueOf((mUsers.get(i).getPin())))) {
                    download(mUsers.get(i).getID() + ".txt", mUsers.get(i).getID(), mUsers.get(i).getFName(), mUsers.get(i).getFName());
                }
            }
        }
    }

    private void download(String s, int id, String fName, String lName) {
        StorageReference sr = FirebaseStorage.getInstance().getReference().child("TimeLogs/" + id + ".txt");
        String Path = null;

        try {
            File localFile = File.createTempFile("text", ".txt");

            Uri.Builder uri = new Uri.Builder();
            uri.appendEncodedPath(ListCheckingHours.this.getFilesDir() + "/" + id + ".txt");
            Uri u = uri.build();

            Path = u.getPath();
            sr.getFile(u)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(sr.getName(), "Found Text");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        openFile(Path, id, fName, lName);
    }

    private void openFile(String path, int id, String fName, String lName) {
        Intent intent1 = getIntent();
        int WeekCheck = 0;

        if (!(intent1.getExtras().getString("Week").equals(null))) {
            WeekCheck = Integer.parseInt(intent1.getExtras().getString("Week"));
        }

        Intent intent2 = getIntent();
        String DateCheck = intent2.getExtras().getString("Date");
        String[] getYear1 = DateCheck.split("/");

        FileInputStream fis = null;
        String Shift = "";
        int TotalTime = 0;
        long LineCount = 0;
        String[] strNums;
        try {
            LineCount = Files.lines(Paths.get(path)).count();
            fis = openFileInput(id + ".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String Time1 = null;
            String Time2 = null;
            Boolean pass = false;
            int counter = 0;

            String Date = "Date: ";
            String Note = "Note: ";

            if (LineCount >= 0) {
                for (int i = 0; i < LineCount; i++) {
                    strNums = br.readLine().split(",");
                    String[] getYear2 = strNums[4].split("/");
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    int week = 0;

                    try {
                        Date dateGet = format.parse(strNums[4]);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateGet);
                        week = cal.get(Calendar.WEEK_OF_YEAR);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (WeekCheck == week && getYear1[2].matches(getYear2[2])) {
                        if (i % 2 == 0) {

                            Time2 = strNums[3];
                        } else {

                            String ID = "ID: " + strNums[0];
                            String Name = "Name: " + strNums[1] + " " + strNums[2];
                            if (strNums[5].matches("ClockedIn")) {
                                Shift = "Clocked In: " + strNums[3];
                            } else if (strNums[5].matches("ClockedOut")) {
                                Shift = "Clocked Out: " + strNums[3];
                            } else {
                                Shift = "Something Went Wrong";
                            }
                            Date = "Date: " + strNums[4];

                            if (strNums.length > 6 && strNums[6] != null) {
                                Note = "Note: " + strNums[6];
                            }
                            pass = true;
                            Time1 = strNums[3];
                        }

                        if (pass) {

                            Log.d("TRYING WITH", Time1 + " AND " + Time2);
                            Log.d("Time", GetHours(Time1, Time2).toString());
                            TotalTime += GetHours(Time1, Time2);
                            Hours log = new Hours(GetDay(strNums[4]), Date, GetHours(Time1, Time2).toString());

                            hoursList.add(log);
                            pass = false;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            TextView textViewID = findViewById(R.id.ID);
            textViewID.setText("ID: " + id);

            TextView textViewName = findViewById(R.id.name);
            textViewName.setText("Name: " + fName + " " + lName);

            TextView textViewDate = findViewById(R.id.period);
            textViewDate.setText("Period: " + DateCheck);

            Hours total = new Hours("", "", "Total hours: " + TotalTime);
            hoursList.add(total);

            ListView mListView = (ListView) findViewById(R.id.listView);
            HoursListAdapter adapter = new HoursListAdapter(ListCheckingHours.this, R.layout.custom_view_list_check_hour, hoursList);
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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