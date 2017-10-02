package com.cocodev.TDUCManager.events;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cocodev.TDUCManager.MainActivity;
import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.SubmitArticle;
import com.cocodev.TDUCManager.SubmitEvent;
import com.cocodev.TDUCManager.SubmitFest;
import com.cocodev.TDUCManager.SubmitNotice;
import com.cocodev.TDUCManager.Utility.EmployeeContentEvents;
import com.cocodev.TDUCManager.Utility.Event;
import com.cocodev.TDUCManager.submittedEventDetails;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.cocodev.TDUCManager.Utility.Utility.getTimeAgo;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubmittedEvents extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private View mView;
    private ListView mListView;
    private FirebaseListAdapter<EmployeeContentEvents> mAdapter;
    private DatabaseReference databaseReference;

    public SubmittedEvents() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("EmployeeContent")
                .child(MainActivity.currentUser.getUid())
                .child("Events");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_submitted_events, container, false);
        setUpFloatingActionButtons(mView);
        mListView = (ListView) mView.findViewById(R.id.listView_eventHolder);
        TextView textView = (TextView) mView.findViewById(R.id.eventHolder_emptyView);
        textView.setText("There are currently no articles under this Category.");
        mListView.setEmptyView(textView);
        mAdapter = new FirebaseListAdapter<EmployeeContentEvents>(getActivity(),EmployeeContentEvents.class, R.layout.events_item,databaseReference) {
            @Override
            protected void populateView(final View v, EmployeeContentEvents model, int position) {
                int status = model.getStatus();
                String uid = model.getE_uid();
                String college = model.getCollege();
                DatabaseReference reference;
                if(status==0){
                    databaseReference = firebaseDatabase.getReference().child("PendingEvents").child(uid);
                }else if(status==1){
                    if(model.getCollege()==null || model.getCollege().equals("")){
                        databaseReference = firebaseDatabase.getReference().child("Events").child(uid);
                    }else{
                        databaseReference = firebaseDatabase.getReference().child("College Content")
                                .child(model.getCollege()).child("Events").child(uid);
                    }
                }else if(status==-1){
                    databaseReference = firebaseDatabase.getReference().child("RejectedEvents").child(uid);
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        if(event==null)
                            return;
                        final TextView UID  = (TextView) v.findViewById(R.id.event_UID);
                        final TextView title = (TextView) v.findViewById(R.id.event_title);
                        final TextView venue = (TextView) v.findViewById(R.id.event_venue);
                        final TextView time = (TextView) v.findViewById(R.id.event_time);
                        final ImageView image = (ImageView) v.findViewById(R.id.event_image);
                        title.setText(event.getTitle());
                        venue.setText(event.getVenue());
                        UID.setText(event.getUID());
                        time.setText(getTimeAgo(getContext(), event.getTime()));
                        if(!event.getUrl().equals("")) {
                            Picasso.with(getContext()).load(event.getUrl()).placeholder(R.drawable.event_place_holder).fit().centerInside().into(image);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView UID  = (TextView) view.findViewById(R.id.event_UID);
                String uid = (String) UID.getText();
                Intent intent = new Intent(getContext(), submittedEventDetails.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }

        });

        return mView;
    }

    private void setUpFloatingActionButtons(View view) {
        FloatingActionButton submitArticle = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item1);
        FloatingActionButton submitEvent = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item2);
        FloatingActionButton submitNotice = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item3);
        FloatingActionButton submitFest = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item4);

        submitArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SubmitArticle.class);
                startActivity(i);
            }
        });
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SubmitEvent.class);
                startActivity(i);
            }
        });
        submitNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SubmitNotice.class);
                startActivity(i);
            }
        });

        submitFest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SubmitFest.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Submitted Events");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.cleanup();
    }
}
