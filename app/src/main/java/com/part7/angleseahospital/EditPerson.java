package com.part7.angleseahospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class EditPerson extends AppCompatActivity {

    private int count = 0;
    DrawerLayout drawerLayout;

    private EditText mSearchField;
    private ImageButton mSearchButton;
    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;
    private ArrayList<User> mUsers;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        drawerLayout = findViewById(R.id.drawer_layout);
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Staff");

        mSearchField = (EditText) findViewById(R.id.editTextTextPersonName);
        mSearchButton = (ImageButton) findViewById(R.id.imageButton);
        mUsers = new ArrayList<>();


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);
            }
        });
    }

    private void firebaseUserSearch(String searchText) {
        GetInfo();

        Toast.makeText(EditPerson.this, "Started Search", Toast.LENGTH_LONG).show();
        if (mUsers != null) {
            for (int i = 0; i < mUsers.size(); i++) {
                if (searchText.matches(String.valueOf(mUsers.get(i).getID()))) {
                    int ID = mUsers.get(i).getID();
                    String fName = mUsers.get(i).getFName();
                    String lName = mUsers.get(i).getLName();
                    String DoB = mUsers.get(i).getDOB();
                    String Email = mUsers.get(i).getEmail();
                    int PhoneNum = mUsers.get(i).getPhoneNumber();
                    int Pin = mUsers.get(i).getPin();
                    String Notes = mUsers.get(i).getNotes();
                    String Photo = mUsers.get(i).getPhoto();

                    FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                            User.class,
                            R.layout.list_layout,
                            UserViewHolder.class,
                            mUserDatabase
                    ) {
                        @Override
                        protected void populateViewHolder(UserViewHolder usersViewHolder, User user, int i) {
                            usersViewHolder.setDetails(DoB, Email, fName, ID
                                    , lName, Notes, PhoneNum, Photo, Pin, count);
                        }
                    };

                    mResultList.setAdapter(firebaseRecyclerAdapter);
                }
            }


        } else {

        }

    }




    public void GetInfo(){


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Toast.makeText(EditPerson.this, "There are no Users", Toast.LENGTH_SHORT).show();
                }else {
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
                        if(mUsers == null){
                            mUsers.add(new User(ID, fName, lName, DoB, email, phoneNum, pin, notes, photo));
                        }else {
                            boolean isFound = false;
                            for (int i = 0; i < mUsers.size(); i++) {
                                if (mUsers.get(i).getID() == ID) {
                                    isFound = true;
                                }
                            }
                            if (!isFound){
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
    }


    //View Holder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setDetails(String dOB, String eMail, String fName, int iD, String lName,
                               String nOtes, int pHoneumber, String pHoto, int pIn, int count) {
            if (count == 0){
                Log.d(TAG, "firebaseUserSearch: HERE$$$$$$$$$$$$$$$$$$$$$");
                count += 1;
            }
            TextView dob = (TextView) mView.findViewById(R.id.dob_text);
            TextView email = (TextView) mView.findViewById(R.id.email_text);
            TextView fname = (TextView) mView.findViewById(R.id.fname_text);
            TextView id = (TextView) mView.findViewById(R.id.id_text);
            TextView lname = (TextView) mView.findViewById(R.id.lname_text);
            TextView notes = (TextView) mView.findViewById(R.id.notes_text);
            TextView phoneNumber = (TextView) mView.findViewById(R.id.phoneNumber_text);
            TextView photo = (TextView) mView.findViewById(R.id.photo_text);
            TextView pin = (TextView) mView.findViewById(R.id.pin_text);


            dob.setText("Date of Birth: " + dOB);
            email.setText("Email: " + eMail);
            fname.setText("First Name: " + fName);
            id.setText("ID: " + String.valueOf(iD));
            lname.setText("Last Name" + lName);
            notes.setText("Notes: " + nOtes);
            phoneNumber.setText("Phone Nubmer: " + String.valueOf(pHoneumber));
            photo.setText("Photo: " + pHoto);
            pin.setText("Pin: " + String.valueOf(pIn));




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

    public void ClickAdd(View view) {
        MainActivity.redirectActivity(this, AddPerson.class);
    }

    public void ClickEdit(View view) {
        recreate();
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


}