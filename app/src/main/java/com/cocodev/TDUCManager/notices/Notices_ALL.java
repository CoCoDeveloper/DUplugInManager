package com.cocodev.TDUCManager.notices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.Utility.Notice;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Notices_ALL extends Fragment  {

    public static final String key = "type";
    private String typeString ;
    private View mView;
    private final int NOTICE_LOADER =1;
    private ListView mListView;



    public Notices_ALL() {
        setTypeString(Notices.TYPE_HOME);
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("Notices");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView  = inflater.inflate(R.layout.fragment_notices, container, false);
        mListView = (ListView) mView.findViewById(R.id.list_notices);
        TextView textView = (TextView) mView.findViewById(R.id.notices_emptyView);
        textView.setText("There are currently no Notices under this Category.");
        mListView.setEmptyView(textView);
        mAdapter = new FirebaseListAdapter<Notice>(
                getActivity(),
                Notice.class,
                R.layout.adapter_notice,
                reference
        ) {

            @Override
            protected void populateView(View v, Notice model, int position) {
                final TextView description = (TextView) v.findViewById(R.id.notice_description);
                final TextView time = (TextView) v.findViewById(R.id.notice_time);
                final TextView deadline = (TextView) v.findViewById(R.id.notice_deadline);

                description.setText(model.getDescription());
                time.setText(model.getTime());
                deadline.setText(model.getDeadline());
            }


        };

        mListView.setAdapter(mAdapter);
        return mView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString(){
        return typeString;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.cleanup();
    }
}
