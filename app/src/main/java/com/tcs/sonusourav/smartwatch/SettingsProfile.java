package com.tcs.sonusourav.smartwatch;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SONU SOURAV on 3/17/2018.
 */

public class SettingsProfile extends AppCompatActivity {

    android.support.v7.app.ActionBar settingsActionbar;
    Button editButton;
    MenuItem save;
    Calendar profileCalender;
    FirebaseUser user;
    private EditText profileName, profileDOB, profilePhone, profileEmail, profileMedical;
    private Spinner profileGender;
    private FirebaseAuth profileauth;
    private FirebaseDatabase profilefirebaseInstance;
    private FirebaseFirestore profilefirestoreInstance;
    private DatabaseReference profilerootRef;
    private DatabaseReference profileuserIdRef;
    private DatabaseReference profileEmailRef;
    private String userEmail;
    private ImageView verifyImage;
    ProgressBar settingsProfileProgressbar;


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_profile);
        profileName = findViewById(R.id.profile_et1);
        profileDOB = findViewById(R.id.profile_et2);
        profileGender = findViewById(R.id._profile_spin_gender);
        profilePhone = findViewById(R.id.profile_et4);
        profileEmail = findViewById(R.id.profile_et5);
        profileMedical = findViewById(R.id.profile_et6);
        editButton = findViewById(R.id.profile_edit_button);
        profileCalender = Calendar.getInstance();
        verifyImage = findViewById(R.id.iv_verify);
        settingsProfileProgressbar=findViewById(R.id.profile_progressBar);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        settingsProfileProgressbar.setVisibility(View.VISIBLE);
        profileGender.setEnabled(false);
        profileauth = FirebaseAuth.getInstance();
        profilefirestoreInstance = FirebaseFirestore.getInstance();
        profilefirebaseInstance = FirebaseDatabase.getInstance();
        profilerootRef = profilefirebaseInstance.getReference("Users");
        profileuserIdRef = profilefirebaseInstance.getReference("UserId");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String testEmail = encodeUserEmail(user.getEmail());
        profileEmailRef = profilerootRef.child(testEmail).getRef();


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName.setEnabled(true);
                profileDOB.setEnabled(true);
                profileDOB.setFocusable(false);
                profileGender.setEnabled(true);
                profileMedical.setEnabled(true);
                save.setEnabled(true);
            }
        });


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                profileCalender.set(Calendar.YEAR, year);
                profileCalender.set(Calendar.MONTH, monthOfYear);
                profileCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        profileDOB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SettingsProfile.this, date, profileCalender.get(Calendar.YEAR), profileCalender.get(Calendar.MONTH),
                        profileCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        profileEmailRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nvalue = dataSnapshot.getValue(String.class);
                profileName.setText(nvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("DOB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dvalue = dataSnapshot.getValue(String.class);
                profileDOB.setText(dvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("gender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gvalue = dataSnapshot.getValue(String.class);

                if (gvalue.toString().equals("Male"))
                    profileGender.setSelection(1);
                else
                    profileGender.setSelection(2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String evalue = dataSnapshot.getValue(String.class);
                profileEmail.setText(evalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pvalue = dataSnapshot.getValue(String.class);
                profilePhone.setText(pvalue);
                verifyImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("medicalIssue").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mvalue = dataSnapshot.getValue(String.class);
                profileMedical.setText(mvalue);
                settingsProfileProgressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                settingsProfileProgressbar.setVisibility(View.INVISIBLE);

            }
        });


    }

    //updateLabel Method
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        profileDOB.setText(sdf.format(profileCalender.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_button_profile, menu);*/
        assert settingsActionbar != null;
        settingsActionbar = getSupportActionBar();
        settingsActionbar.setHomeButtonEnabled(true);
        settingsActionbar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                this.finish();
                return true;
            case R.id.profile_save:
                Update(item);
                Toast.makeText(getBaseContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_button_profile, menu);

        save = menu.getItem(0);
        menu.getItem(0).setEnabled(false); // here pass the index of save menu item
        return super.onPrepareOptionsMenu(menu);

    }

    public void Update(MenuItem item) {

        final String pname = profileName.getText().toString().trim();
        final String pdob = profileDOB.getText().toString().trim();
        final String pgender = profileGender.getSelectedItem().toString();
        final String pmedicalIssue = profileMedical.getText().toString().trim();

        if (profileauth.getCurrentUser() != null) {

            userEmail = user.getEmail();
            String encodeEmail = encodeUserEmail(userEmail);
            profileEmailRef = profilerootRef.child(encodeEmail).getRef();


            profileEmailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    dataSnapshot.getRef().child("name").setValue(pname);
                    dataSnapshot.getRef().child("DOB").setValue(pdob);
                    dataSnapshot.getRef().child("gender").setValue(pgender);
                    dataSnapshot.getRef().child("medicalIssue").setValue(pmedicalIssue);
                    profileName.setEnabled(false);
                    profileDOB.setEnabled(false);
                    profileDOB.setFocusable(false);
                    profileGender.setEnabled(false);
                    profileMedical.setEnabled(false);
                    save.setEnabled(false);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }

            });


            Intent intent = new Intent(SettingsProfile.this, FragmentSwitch.class);
            startActivity(intent);

        }


    }

}
