package com.tcs.sonusourav.smartwatch;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class EmergencyContactsActivity extends AppCompatActivity {

    private static final String TAG = EmergencyContactsActivity.class.getSimpleName();
    private static final int RESULT_PICK_CONTACT = 1;
    FirebaseUser user;
    android.support.v7.app.ActionBar actionbar;
    ListView listView;

    ArrayList<EmergencyContactsList> ecList;
    CustomAdapter customAdapter;
    private FirebaseDatabase ecInstance;
    private DatabaseReference ecRootRef;
    private DatabaseReference ecEmailRef;
    private DatabaseReference ecContacts;
    private FloatingActionButton fabButton;

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts);

        listView = findViewById(R.id.ec_list_view);
        ecList = new ArrayList<>();
        fabButton = findViewById(R.id.fab);



        FirebaseAuth ecAuth = FirebaseAuth.getInstance();
        ecInstance = FirebaseDatabase.getInstance();
        ecRootRef = ecInstance.getReference("Users");
        user = ecAuth.getCurrentUser();
        String testEmail = encodeUserEmail(user.getEmail());
        ecEmailRef = ecRootRef.child(testEmail).getRef();
        ecContacts = ecEmailRef.child("emergencyContacts").getRef();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        if (user != null) {
            customAdapter = new CustomAdapter(getApplicationContext(), ecList);
            listView.setAdapter(customAdapter);


            ecContacts.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: reached");
                        String ecname = snapshot.getKey();
                        String ecphone = snapshot.getValue().toString();
                        ecList.add(new EmergencyContactsList(ecname, ecphone));
                    }
                    customAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });


            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(i, RESULT_PICK_CONTACT);

                }

            });

            Log.d(TAG, "onCreate: reached");


        }

    }

    protected void onResume() {
        super.onResume();

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            final String phoneNo = cursor.getString(phoneIndex);
            final String name = cursor.getString(nameIndex);


            ecContacts.orderByChild(name).equalTo(name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //name already exists in Database
                            } else {
                                //name doesn't exists.
                                ecContacts.child(name).setValue(phoneNo);
                                ecList.clear();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

            customAdapter.notifyAll();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private void confirmDialogDemo(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Contact");
        builder.setMessage("You are about to delete " + (ecList.get(pos).getECname()) + ". Do you really want to proceed ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), (ecList.get(pos).getECname()) + " successfully removed from your contact.", Toast.LENGTH_SHORT).show();
                ecContacts.child(ecList.get(pos).getECname()).removeValue();
                ecList.remove(pos);
                ecList.clear();


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You've changed your mind to delete " + (ecList.get(pos).getECname()), Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void sendToPhoneDial(int pos) {
        String phone = ecList.get(pos).getECphone();
        Log.i("Make call", "Make call >>>>>>" + phone);

        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + phone));
        try {
            startActivity(phoneIntent);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            Log.i("Finished making a call", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    class CustomAdapter extends BaseAdapter {

        private Context mcontext;
        private ArrayList<EmergencyContactsList> ECList = new ArrayList<>();

        public CustomAdapter(Context context, ArrayList<EmergencyContactsList> ecList) {
            this.mcontext = context;
            this.ECList = ecList;
        }

        @Override
        public int getCount() {
            return ECList.size();
        }

        @Override
        public EmergencyContactsList getItem(int position) {
            return ECList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.ec_list_view, parent, false);
            }

            EmergencyContactsList m = ECList.get(position);

            final TextView ecName = convertView.findViewById(R.id.lv_text_view);
            final ImageView ecProfileImage = convertView.findViewById(R.id.lv_image_view);
            final ImageView ecCallImage = convertView.findViewById(R.id.lv_call);
            final ImageView ecDeleteImage = convertView.findViewById(R.id.lv_delete);
            ecName.setText(m.getECname());
            ecProfileImage.setImageResource(R.drawable.icon_contact);
            ecCallImage.setImageResource(R.drawable.icon_call);
            ecDeleteImage.setImageResource(R.drawable.icon_delete);

            ecCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToPhoneDial(position);

                    Log.d("onClick:sendToPhoneDial", "Reached");
                }
            });

            ecDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialogDemo(position);
                    Log.d("onClick:confirmDialog", "Reached");
                }
            });

            Log.d(TAG, "getView: convertView=null");


            return convertView;

        }
    }
}
