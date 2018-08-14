package com.tcs.sonusourav.smartwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentHome extends Fragment {
    private View homeView;
    private ImageButton heartRateImageBt;
    private ImageButton bloodPressureImageBt;
    private ImageButton historyImageBt;
    private ImageButton emergencyConImageBt;

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    public static Fragment newInstance(Bundle savedInstanceState) {

        Fragment fragment = new FragmentHome();
        if (savedInstanceState != null) {
            fragment.setArguments(savedInstanceState);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_screen, null);
        heartRateImageBt = homeView.findViewById(R.id.imagebt_hr);
        bloodPressureImageBt = homeView.findViewById(R.id.imagebt_bp);
        historyImageBt = homeView.findViewById(R.id.imagebt_his);
        emergencyConImageBt = homeView.findViewById(R.id.image_ec);

        heartRateImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hrintent = new Intent(getActivity(), HeartbeatConnect.class);
                getActivity().startActivity(hrintent);
            }
        });

        historyImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hisintent = new Intent(getActivity(), HistorySwitcher.class);
                getActivity().startActivity(hisintent);
            }
        });

        bloodPressureImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hrintent = new Intent(getActivity(), BloodpressureConnect.class);
                getActivity().startActivity(hrintent);
            }
        });

        emergencyConImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ecintent = new Intent(getActivity(), EmergencyContactsActivity.class);
                getActivity().startActivity(ecintent);
            }
        });


        return homeView;
    }
}