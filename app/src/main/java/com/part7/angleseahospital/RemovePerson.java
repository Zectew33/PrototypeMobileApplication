package com.part7.angleseahospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RemovePerson extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;

    private Button mButton, btnDelete;
    DrawerLayout drawerLayout;
    private ArrayList<User> mUsers;

    EditText firstName, lastName;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_person);

        drawerLayout = findViewById(R.id.drawer_layout);
        mUsers = new ArrayList<>();

        mButton = findViewById(R.id.button4);
        btnDelete = findViewById(R.id.btnDelete);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Toast.makeText(RemovePerson.this, "There are no Users", Toast.LENGTH_SHORT).show();
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

                                mUsers.add(new User(ID, fName, lName, DoB, email, phoneNum, pin, notes, photo));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });

                int UserID;

                String fName = firstName.getText().toString().trim();
                String lName = lastName.getText().toString().trim();

                if (mUsers != null || !(mUsers.size() == 0)) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        if (fName.matches(mUsers.get(i).getFName()) && lName.matches(mUsers.get(i).getLName())) {
                            UserID = mUsers.get(i).getID();

                            AlertDialog.Builder dialog = new AlertDialog.Builder(RemovePerson.this);
                            dialog.setTitle("Are you sure?");
                            dialog.setMessage("Deleting this account will result in completely removing your account from the database.");

                            int finalUserID = UserID;
                            dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reference.child("User" + finalUserID).removeValue();
                                    Toast.makeText(RemovePerson.this, "Account Deleted", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RemovePerson.this, ManagerPinIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                            dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                        }
                    }
                }
            }
        });
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
        recreate();
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