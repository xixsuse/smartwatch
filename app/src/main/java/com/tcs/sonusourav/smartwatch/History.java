package com.tcs.sonusourav.smartwatch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by SONU SOURAV on 5/13/2018.
 */

public class History extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RESULT_PICK_CONTACT = 1;
    FirebaseUser user;
    android.support.v7.app.ActionBar actionbar;
    ListView hisListView;


    ArrayList<HistoryClass> hisList;
    CustomAdapter customAdapter;
    private FirebaseDatabase hisInstance;
    private DatabaseReference hisRootRef;
    private DatabaseReference hisEmailRef;
    private DatabaseReference chatHistory;

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        hisListView = findViewById(R.id.history_list_view);
        hisList = new ArrayList<>();


        FirebaseAuth hisAuth = FirebaseAuth.getInstance();
        hisInstance = FirebaseDatabase.getInstance();
        hisRootRef = hisInstance.getReference("Users");
        user = hisAuth.getCurrentUser();
        String testEmail = encodeUserEmail(user.getEmail());
        hisEmailRef = hisRootRef.child(testEmail).getRef();
        chatHistory = hisEmailRef.child("chats").getRef();


        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        if (user != null) {
            customAdapter = new CustomAdapter(getApplicationContext(), hisList);
            hisListView.setAdapter(customAdapter);

            chatHistory.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Log.d(TAG, "Key" + key);
                        DatabaseReference keyReference = chatHistory.getRef().child(key).getRef();
                        Log.d(TAG, "onDataChange: reached");

                        keyReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot childDataSnapshot) {
                                String chatData = childDataSnapshot.child("text").getValue(String.class);
                                String chatDate = childDataSnapshot.child("date").getValue(String.class);
                                String chatTime = childDataSnapshot.child("time").getValue(String.class);
                                int chatSrNumber = childDataSnapshot.child("srNumber").getValue(Integer.class);
                                String chatDeviceName = childDataSnapshot.child("deviceName").getValue(String.class);

                                if (childDataSnapshot != null) {
                                    hisList.add(new HistoryClass(chatData, chatDate, chatTime, chatDeviceName, chatSrNumber));
                                    customAdapter.notifyDataSetChanged();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });


        }
    }


    class CustomAdapter extends BaseAdapter {

        private Context hContext;
        private ArrayList<HistoryClass> HisList = new ArrayList<>();

        public CustomAdapter(Context context, ArrayList<HistoryClass> historyClassArrayList) {
            this.hContext = context;
            this.HisList = historyClassArrayList;
        }

        @Override
        public int getCount() {
            return HisList.size();
        }

        @Override
        public HistoryClass getItem(int position) {
            return HisList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = LayoutInflater.from(hContext).inflate(R.layout.chat_list_view, parent, false);
            }

            HistoryClass h = HisList.get(position);

            final TextView tvData = convertView.findViewById(R.id.chat_tv1);
            final TextView tvDate = convertView.findViewById(R.id.chat_tv2);
            final TextView tvTime = convertView.findViewById(R.id.chat_tv3);
            final TextView tvDeviceName = convertView.findViewById(R.id.chat_tv4);
            final ImageView tvSR = convertView.findViewById(R.id.his_image);
            tvData.setText(h.getText());
            tvDate.setText(h.getDate());
            tvTime.setText(h.getTime());
            tvDeviceName.setText(h.getDeviceName());

            if (h.getSrNumber() == 0) {
                tvSR.setImageResource(R.drawable.icon_send);
            } else {
                tvSR.setImageResource(R.drawable.icon_received);
            }


            return convertView;

        }
    }


}
