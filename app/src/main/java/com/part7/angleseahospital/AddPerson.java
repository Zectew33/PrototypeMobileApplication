package com.part7.angleseahospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class AddPerson extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText mFirstName, mLastname, mDateOfBirth, mEmail, mPhoneNumber, mPhotoName, mPin, mNotes;
    private Button mSubmit, mChooseFile, mTakePhoto;
    //private FirebaseAuth fAuth;

    private Uri mImageUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    EditText editText;
    DatePickerDialog.OnDateSetListener setListener;

    User user;
    DrawerLayout drawerLayout;
    long id;

    final int[] count = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        drawerLayout = findViewById(R.id.drawer_layout);
        editText = findViewById(R.id.DateOfBirth);
        mFirstName = findViewById(R.id.firstname);
        mLastname = findViewById(R.id.lastname);
        mDateOfBirth = findViewById(R.id.DateOfBirth);
        mEmail = findViewById(R.id.EmailAddress);
        mPhoneNumber = findViewById(R.id.PhoneNumber);
        mPin = findViewById(R.id.PinCode);

        mNotes = findViewById(R.id.Note);

        mSubmit = findViewById(R.id.Submit);
        mTakePhoto = findViewById(R.id.TakePhoto);
        mChooseFile = findViewById(R.id.ChooseFile);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        user = new User();

        mTakePhoto.setOnClickListener(new PhotoTaker());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Staff");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int autoincrementid = 0;
                do {
                    autoincrementid++;
                } while (snapshot.hasChild("User" + autoincrementid));
                id = autoincrementid;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        mChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddPerson.this, new DatePickerDialog.OnDateSetListener() {
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

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirstName.getText().toString().trim().length() > 0) {
                    Log.d("This", "Fname > 0");
                    if (mLastname.getText().toString().trim().length() > 0) {
                        Log.d("This", "Lname > 0");
                        if (mPin.getText().toString().trim().length() == 4) {
                            if (mUploadTask != null && mUploadTask.isInProgress()) {
                                Log.d("This", "Pin is 4");
                                Toast.makeText(AddPerson.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                            } else {
                                CheckInformation();
                                count[0] = 0;
                                Log.d("This", "Photo accepted");
                            }

                        } else {
                            Log.d("This", "Pin is to short");
                            Toast.makeText(AddPerson.this, "Pin Need To Be 4 Digits", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("This", "Lname is empty");
                        Toast.makeText(AddPerson.this, "Last Name is Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPerson.this, "First Name is Required", Toast.LENGTH_SHORT).show();
                    Log.d("This", "Fname is empty");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }
    }

    private void AddInformation() {
        int phoneNum = Integer.parseInt(mPhoneNumber.getText().toString().trim());
        int pinNum = Integer.parseInt(mPin.getText().toString().trim());
        int Id = Integer.parseInt(String.valueOf(id));

        user.setID(Id);
        user.setFName(mFirstName.getText().toString().trim());
        user.setLName(mLastname.getText().toString().trim());
        user.setDOB(mDateOfBirth.getText().toString().trim());
        user.setEmail(mEmail.getText().toString().trim());
        user.setPhoneNumber(phoneNum);
        //Need to add Picture
        user.setPin(pinNum);

        user.setNotes(mNotes.getText().toString().trim());

        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(AddPerson.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Log.d("Image", "Uploaded");

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            Log.d("", "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                            Upload upload = new Upload(mFirstName.getText().toString().trim() + mLastname.getText().toString().trim(), downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();

                            user.setPhoto(uploadId);
                            //mDatabaseRef.child(uploadId).setValue(upload);
                            mDatabaseRef.child("User" + String.valueOf(id)).setValue(user);
                            Toast.makeText(AddPerson.this, "Added User", Toast.LENGTH_SHORT).show();
                            HomeActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPerson.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            //Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            user.setPhoto("NoPhoto");
            mDatabaseRef.child("User" + String.valueOf(id)).setValue(user);
            Toast.makeText(AddPerson.this, "Added User", Toast.LENGTH_SHORT).show();
            HomeActivity();
        }
    }

    private void CheckInformation() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Staff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean passed = false;
                if (!snapshot.exists()) {
                    AddInformation();
                } else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (count[0] == 0) {
                            Log.d("HERE1", String.valueOf(count[0]));
                            if (snapshot1.child("fname").getValue().toString().toLowerCase().matches(mFirstName.getText().toString().trim().toLowerCase()) && snapshot1.child("lname").getValue().toString().toLowerCase().matches(mLastname.getText().toString().trim().toLowerCase())) {
                                Log.d("Log", "Name Matched");
                                Toast.makeText(AddPerson.this, "First and Last Name Already Taken", Toast.LENGTH_SHORT).show();
                                passed = false;
                                break;
                            } else if (snapshot1.child("phoneNumber").getValue().toString().matches(mPhoneNumber.getText().toString().trim())) {
                                Log.d("Log", "Phone Number Matched");
                                Toast.makeText(AddPerson.this, "Phone Already Taken", Toast.LENGTH_SHORT).show();
                                passed = false;
                                break;

                            } else if (snapshot1.child("email").getValue().toString().toLowerCase().matches(mEmail.getText().toString().trim().toLowerCase())) {
                                Log.d("Log", "Email Number Matched");
                                Toast.makeText(AddPerson.this, "Email Already Taken", Toast.LENGTH_SHORT).show();
                                passed = false;
                                break;

                            } else if (snapshot1.child("pin").getValue().toString().matches(mPin.getText().toString().trim())) {
                                Log.d("Log", "Pin Number Matched");
                                Toast.makeText(AddPerson.this, "Pin Already Taken", Toast.LENGTH_SHORT).show();
                                passed = false;
                                break;

                            } else {
                                Log.d("Log", "No Match");
                                passed = true;
                            }
                        }
                    }
                }
                if (passed) {
                    Log.d("Log", "Added");
                    AddInformation();
                    count[0] += 1;
                    Log.d("HERE2", String.valueOf(count[0]));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        recreate();
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

    class PhotoTaker implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }

    }

    public void HomeActivity() {
        Intent intent = new Intent(this, ManagerPinIn.class);
        startActivity(intent);
    }
}