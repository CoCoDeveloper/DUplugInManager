package com.cocodev.TDUCManager.EH;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.Utility.Event;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsHolder extends Fragment {

    public static String key = "type";
    public final static String TYPE_HOME = "All";
    private final String LAST_SCROLL_STATE = "lastScrollState";
    private String typeString;
    private ListView mListView;
    private View view;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseListAdapter mAdapter;

    public EventsHolder() {
        // Required empty public constructor
    }
    public static EventsHolder newInstance(String type){
        EventsHolder e = new EventsHolder();
        e.setTypeString(type);
        return  e;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            typeString = savedInstanceState.getString(key);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        if(typeString!=TYPE_HOME) {
            databaseReference = firebaseDatabase.getReference()
                    .child("Categories").child("SubmitEvent").child(getTypeString());
        }else{
            databaseReference = firebaseDatabase.getReference()
                    .child("SubmitEvent");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_events_holder, container, false);

        if(typeString!=TYPE_HOME) {
            mAdapter = new FirebaseListAdapter<String>(
                    getActivity(),
                    String.class,
                    R.layout.events_adapter,
                    databaseReference
            ) {

                @Override
                protected void populateView(View v, String model, int position) {
                    final TextView title = (TextView) v.findViewById(R.id.event_title);
                    final TextView venue = (TextView) v.findViewById(R.id.event_venue);
                    final TextView time  = (TextView) v.findViewById(R.id.event_time);
                    final TextView UID  = (TextView) v.findViewById(R.id.event_UID);

                    DatabaseReference dbref = firebaseDatabase.getReference().child("SubmitEvent")
                            .child(model);
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);
                            time.setText(event.getTime());
                            venue.setText(event.getVenue());
                            title.setText(event.getTitle());
                            UID.setText(event.getUID());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("TAG", "onCancelled --> addValueEventListener --> populateView" + databaseError.toString());
                        }
                    });
                }
            };
        }else{
            mAdapter = new FirebaseListAdapter<Event>(
                    getActivity(),
                    Event.class,
                    R.layout.events_adapter,
                    databaseReference
            ) {

                @Override
                protected void populateView(View v, Event event, int position) {
                     TextView title = (TextView) v.findViewById(R.id.event_title);
                     TextView venue = (TextView) v.findViewById(R.id.event_venue);
                     TextView time  = (TextView) v.findViewById(R.id.event_time);
                     TextView UID  = (TextView) v.findViewById(R.id.event_UID);
                    time.setText(event.getTime());
                    venue.setText(event.getVenue());
                    title.setText(event.getTitle());
                    UID.setText(event.getUID());
                }
            };
        }

        mListView = (ListView) view.findViewById(R.id.eventsHolder_listView);

        TextView textView = (TextView) view.findViewById(R.id.eventsHolder_emptyView);
        textView.setText("There are currently no SubmitEvent under this Category.");
        mListView.setEmptyView(textView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String UID = (String) ((TextView) view.findViewById(R.id.event_UID)).getText();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View childView = getLayoutInflater(null).inflate(R.layout.event_details,null);
            builder.setView(childView);
            AlertDialog alertDialog = builder.create();
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            alertDialog.getWindow().setWindowAnimations(R.style.DialogTheme);
            alertDialog.show();
            alertDialog.getWindow().setLayout(dm.widthPixels,(int)(dm.heightPixels*0.6));



        }
    };

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString(){
        return typeString;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(key,typeString);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListView = null;
        mAdapter.cleanup();
        databaseReference =null;
    }
}
