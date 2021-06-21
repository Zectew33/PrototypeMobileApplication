package com.part7.angleseahospital;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NurseLogout extends AppCompatActivity {
    private static final String TAG = "";
    private static final int STORAGE_PERMISSION_CODE = 1;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    private ArrayList<User> mUsers;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);

        mIndicatorDots.setBackgroundColor(getColor(R.color.greyish));
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(getColor(R.color.black));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("TimeLogs");
        mUsers = new ArrayList<>();

        GetInfo();

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d("Console", "Completed");
                GetInfo();
                if (mUsers != null || !(mUsers.size() == 0)) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        if (pin.matches(String.valueOf(mUsers.get(i).getPin()))) {
                            Toast.makeText(NurseLogout.this, "Clocked Out", Toast.LENGTH_SHORT).show();

                            startShift();
                            writeLog(mUsers.get(i).getID(), mUsers.get(i).getFName(), mUsers.get(i).getLName());
                            Intent intent = new Intent(NurseLogout.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        } else {
                            mPinLockView.resetPinLockView();
                        }
                    }
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                GetInfo();
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });
    }

    public void GetInfo() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                if (!snapshot.exists()) {
                    Toast.makeText(NurseLogout.this, "There are no Users", Toast.LENGTH_SHORT).show();
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
                            Log.d("***************", "Being Changed!!!!");
                            Log.d("Added 1", mUsers.get(0).toString());
                        } else {
                            boolean isFound = false;
                            for (int i = 0; i < mUsers.size(); i++) {
                                if (mUsers.get(i).getID() == ID) {

                                    isFound = true;
                                }
                            }
                            if (!isFound) {
                                mUsers.add(new User(ID, fName, lName, DoB, email, phoneNum, pin, notes, photo));
                                Log.d("***************", "Being Changed!!!!");
                                Log.d("Added 2", mUsers.get(counter).toString());
                            }
                        }
                        counter += 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    public int getTime() {
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmm");
        String str = currentTime.format(new Date());
        return Integer.parseInt(str);
    }

    public String getLog() {
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss,dd/MM/yyyy");
        String str = currentTime.format(new Date());
        return str;
    }

    public void startShift() {
        int result = 0;
        if (getTime() >= 0630 && getTime() <= 1600) { //Giving them +- 30 mins
            Log.d("Time", "You are AM Shift");
        } else if (getTime() >= 1330 && getTime() <= 2300) { //Giving them +- 30 mins
            Log.d("Time", "You are PM Shift");
        } else if (getTime() >= 2130 && getTime() <= 0750) { //Giving them +- 20 mins
            Log.d("Time", "You are Night");
        }
        //if(getTime() >= maxShiftStart && getTime() <= maxShiftStart{ //Giving them +- 30 mins
        //Log.d("Time", "You are MixedShift");
        //return result;
    }

    public void writeLog(int id, String fName, String lName) {
        Intent intent = getIntent();
        String note = intent.getExtras().getString("noteOut");
        String text = id + "," + fName + "," + lName + "," + getLog() + ",ClockedOut," + note;

        FileOutputStream fos = null;
        try {
            File file = new File(NurseLogout.this.getFilesDir() + "/" + id + ".txt");
            Log.d("File Path", file.getPath());
            if (file.length() == 0) {
                fos = openFileOutput(String.valueOf(id) + ".txt", MODE_PRIVATE);
                fos.write(text.getBytes());
                Log.d(TAG, "writeLog: New File");
            } else {
                fos = openFileOutput(String.valueOf(id) + ".txt", MODE_APPEND);
                fos.write("\n".getBytes());
                fos.write(text.getBytes());
                Log.d(TAG, "writeLog: Append File");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Uri file = Uri.fromFile(new File(NurseLogout.this.getFilesDir() + "/" + id + ".txt"));
        UploadTask uploadTask = mStorageRef.child(id + ".txt").putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Failed", "So BAd");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("GOOD WORK!", "Lol jk");
            }
        });
    }
}