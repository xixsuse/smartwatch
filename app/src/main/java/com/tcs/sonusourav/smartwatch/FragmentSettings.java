package com.tcs.sonusourav.smartwatch;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

import static com.tcs.sonusourav.smartwatch.SignInActivity.PREFS_NAME;


public class FragmentSettings extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    private SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    SettingsCustomAdapter settingsListViewAdapter;
    Switch settingsSwitch;
    String[] settingsListItem = {"Profile",
            "Configuration",
            "Privacy Policy",
            "About Us",
            "Sign out"};
    boolean[] settingsStatus = {false, false, false, false};
    int[] settingsImage = {R.drawable.icon_my_profile,
            R.drawable.icon_configuration,
            R.drawable.icon_privacy_policy,
            R.drawable.icon_about_us,
            R.drawable.icon_sign_out};
    int[] settingsImageArrow = {R.drawable.icon_forward_arrow,
            R.drawable.icon_down_arrow,
            R.drawable.icon_down_arrow,
            R.drawable.icon_down_arrow,
            R.drawable.icon_down_arrow
    };
    FirebaseUser signoutUser;
    private BluetoothAdapter myBluetoothAdapter;
    private ListView settingsListView;
    private View mView;
    private FirebaseAuth signoutAuth;
    private BroadcastReceiver mybroadcast;

    public static Fragment newInstance(Bundle savedInstanceState) {

        Fragment fragment = new FragmentSettings();
        if (savedInstanceState != null) {
            fragment.setArguments(savedInstanceState);
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        signoutAuth = FirebaseAuth.getInstance();
        signoutUser = FirebaseAuth.getInstance().getCurrentUser();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pref = getContext().getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        mybroadcast = new BTStateChangedBroadcastReceiver();
        getActivity().registerReceiver(mybroadcast,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.settings_screen, null);

        if (savedInstanceState != null) {
            settingsStatus = savedInstanceState.getBooleanArray("status");
        }

        settingsListView = mView.findViewById(R.id.settings_list_view);
        settingsListViewAdapter = new SettingsCustomAdapter();
        settingsListView.setAdapter(settingsListViewAdapter);

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Settings");
        assert actionBar != null;

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                Fragment home_fragment = FragmentHome.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, home_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mybroadcast);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (settingsListViewAdapter != null) {
            Log.d("notify", "reached");
            settingsListViewAdapter.notifyDataSetChanged();
        }


        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Settings");

    }

    @Override
    public void onPause() {
        super.onPause();

    }


    public void bluetoothOn() {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, 1);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
            listPairedDevice();

        }
    }

    public void bluetoothOff() {
        if (myBluetoothAdapter.isEnabled()) {
            myBluetoothAdapter.disable();
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth turned off", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is already off", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == 1) {

            }
        } else {
            myBluetoothAdapter.disable();
            settingsSwitch.setChecked(false);
        }
    }

    public void listPairedDevice() {
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        Toast.makeText(getActivity().getApplicationContext(), "Paired device : " + pairedDevices, Toast.LENGTH_SHORT).show();
    }

    class SettingsCustomAdapter extends BaseAdapter {
        int i = 0;

        @Override
        public int getCount() {
            return settingsListItem.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.settings_list_view, null);
            ImageView imageView = convertView.findViewById(R.id.settings_iv);
            settingsSwitch = convertView.findViewById(R.id.settings_switch);


            TextView textViewListItem = convertView.findViewById(R.id.settings_tv1);
            final ImageButton imagebutton = convertView.findViewById(R.id.settings_arrow_button);
            final TextView bluetooth_tv = convertView.findViewById(R.id.settings_tv2);
            final Switch settingsSwitch = convertView.findViewById(R.id.settings_switch);
            imageView.setImageResource(settingsImage[position]);
            textViewListItem.setText(settingsListItem[position]);
            imagebutton.setImageResource(settingsImageArrow[position]);

            Log.d("myBluetoothAdapter", "reached");

            if (myBluetoothAdapter.isEnabled()) {
                settingsSwitch.setChecked(true);
            } else
                settingsSwitch.setChecked(false);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 4) {


                        if (signoutAuth.getCurrentUser() != null) {

                            AuthUI.getInstance()
                                    .signOut(getActivity())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // user is now signed out

                                            editor.putString(SignInActivity.isLoggedIn, "false");
                                            editor.remove(SignInActivity.PREF_USERNAME);
                                            editor.remove(SignInActivity.PREF_PASSWORD);
                                            editor.apply();
                                            startActivity(new Intent(getActivity(), SignInActivity.class));
                                        }
                                    });
                        }
                    }
                }
            });

            if (position == 2 || position == 3 || position == 4) {
                imagebutton.setVisibility(View.GONE);
            }

            imagebutton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (position == 1) {
                        if (i % 2 == 0) {
                            settingsSwitch.setVisibility(View.VISIBLE);
                            settingsSwitch.setFocusable(true);
                            settingsSwitch.setClickable(true);
                            bluetooth_tv.setVisibility(View.VISIBLE);
                            imagebutton.setImageResource(R.drawable.icon_up_arrow);

                        } else {
                            settingsSwitch.setVisibility(View.GONE);
                            bluetooth_tv.setVisibility(View.GONE);
                            settingsSwitch.setFocusable(false);
                            settingsSwitch.setClickable(false);
                            imagebutton.setImageResource(R.drawable.icon_down_arrow);

                        }
                        i++;
                    }
                    if (position == 0) {
                        Intent intent = new Intent(getActivity(), SettingsProfile.class);
                        getActivity().startActivity(intent);

                    }
                }
            });

            settingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (position == 1) {
                        if (myBluetoothAdapter == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Your device does not support Bluetooth", Toast.LENGTH_SHORT).show();
                            settingsSwitch.setChecked(false);
                        } else {
                            if (settingsSwitch.isChecked()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                            } else {
                                myBluetoothAdapter.disable();

                            }

                        }


                    }

                }
            });


            return convertView;


        }

    }


}