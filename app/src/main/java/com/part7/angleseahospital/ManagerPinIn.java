package com.part7.angleseahospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.Person;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ManagerPinIn extends AppCompatActivity {
    private int DELAY = 1500;
    private static final String TAG = "ManagerPinIn";
    private ArrayList<User> mUsers;
    private ArrayList<Staff> mStaff;
    private StorageReference mStorageRef;
    private Button button;

    DrawerLayout drawerLayout;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_pin_in);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("TimeLogs");
        drawerLayout = findViewById(R.id.drawer_layout);

        button = findViewById(R.id.refresh);
        mUsers = new ArrayList<>();

        UpdateList();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickAdd(View view) {
        MainActivity.redirectActivity(this, AddPerson.class);
    }

    public void ClickEdit(View view) {
        MainActivity.redirectActivity(this, EditPerson.class);
    }

    public void ClickRemove(View view) {
        MainActivity.redirectActivity(this, RemovePerson.class);
    }

    public void ClickExit(View view) {
        MainActivity.exit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }

    public void UpdateList() {
        mStaff = new ArrayList<>();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ManagerPinIn.this, "There are no Users", Toast.LENGTH_SHORT).show();
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
        if (!(mUsers.size() == 0)) {
            for (int i = 0; i < mUsers.size(); i++) {
                download(mUsers.get(i).getID() + ".txt", mUsers.get(i).getID());
            }
        }
    }

    private void download(String s, int id) {
        StorageReference sr = FirebaseStorage.getInstance().getReference().child("TimeLogs/" + id + ".txt");
        String Path = null;

        try {
            File localFile = File.createTempFile("text", ".txt");

            Uri.Builder uri = new Uri.Builder();
            uri.appendEncodedPath(ManagerPinIn.this.getFilesDir() + "/" + id + ".txt");
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
        openFile(Path, id);
    }

    private void openFile(String path, int id) {
        FileInputStream fis = null;
        String Shift = "";
        long LineCount = 0;
        String[] strNums;
        try {
            LineCount = Files.lines(Paths.get(path)).count();
            fis = openFileInput(id + ".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String Note = "Note: ";

            Log.d(String.valueOf(LineCount), "LOL");
            if (LineCount >= 0) {
                for (int i = 0; i < LineCount; i++) {

                    strNums = br.readLine().split(",");

                    Log.d("Line: " + i, strNums[0] + " " + strNums[1] + " " + strNums[2] + " " + strNums[3] + " " + strNums[4] + " " + strNums[5] + " ");


                    String ID = "ID: " + strNums[0];
                    String Name = "Name: " + strNums[1] + " " + strNums[2];
                    if (strNums[5].matches("ClockedIn")) {
                        Shift = "Clocked In: " + strNums[3];
                    } else if (strNums[5].matches("ClockedOut")) {
                        Shift = "Clocked Out: " + strNums[3];
                    } else {
                        Shift = "Something Went Wrong";
                    }
                    String Date = "Date: " + strNums[4];

                    if (strNums.length > 6 && strNums[6] != null) {
                        Note = "Note: " + strNums[6];
                    }

                    Staff staff = new Staff(ID, Name, Shift, Date, Note);
                    mStaff.add(staff);
                }
            }
            //Log.d("Output", sb.toString());
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

            ListView mListView = (ListView) findViewById(R.id.listView);
            StaffListAdapter adapter = new StaffListAdapter(ManagerPinIn.this, R.layout.adapter_view_layout_custom, mStaff);
            mListView.setAdapter(adapter);
        }
    }
}