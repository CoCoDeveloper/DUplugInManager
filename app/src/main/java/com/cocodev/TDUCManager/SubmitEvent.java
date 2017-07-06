package com.cocodev.TDUCManager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;


public class SubmitEvent extends Fragment {
    private static boolean EVENTS_PREFERENCES_CHANGED = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    TabLayoutHelper tabLayoutHelper;
    public SubmitEvent() {
        // Required empty public constructor

    }

    ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        getActivity().setTitle("Submit Event");

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(tabLayoutHelper!=null) {
            tabLayoutHelper.release();
        }
    }

}
