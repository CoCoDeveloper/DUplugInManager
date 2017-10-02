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

import com.cocodev.TDUCManager.PendingEventDetails;
import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.SubmitArticle;
import com.cocodev.TDUCManager.SubmitEvent;
import com.cocodev.TDUCManager.SubmitNotice;
import com.cocodev.TDUCManager.Utility.Event;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import static com.cocodev.TDUCManager.Utility.Utility.getTimeAgo;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingEvents extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ListView mListView;
    private  FirebaseListAdapter<Event> mAdapter;

    public PendingEvents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("PendingEvents");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_pending_events, container, false);
        setUpFloatingActionButtons(view);
        mListView = (ListView) view.findViewById(R.id.listView_eventHolder);
        TextView textView = (TextView) view.findViewById(R.id.eventHolder_emptyView);
        textView.setText("There are currently no articles under this Category.");
        mListView.setEmptyView(textView);
         mAdapter = new FirebaseListAdapter<Event>(getActivity(),Event.class, R.layout.events_item,databaseReference) {
            @Override
            protected void populateView(View v, Event model, int position) {
                final TextView UID  = (TextView) v.findViewById(R.id.event_UID);
                final TextView title = (TextView) v.findViewById(R.id.event_title);
                final TextView venue = (TextView) v.findViewById(R.id.event_venue);
                final TextView time = (TextView) v.findViewById(R.id.event_time);
                final ImageView image = (ImageView) v.findViewById(R.id.event_image);
                title.setText(model.getTitle());
                venue.setText(model.getVenue());
                UID.setText(model.getUID());
                time.setText(getTimeAgo(getContext(), model.getTime()));
                if(!model.getUrl().equals("")) {
                    Picasso.with(getContext()).load(model.getUrl()).placeholder(R.drawable.event_place_holder).fit().centerInside().into(image);
                }
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView UID  = (TextView) view.findViewById(R.id.event_UID);
                String uid = (String) UID.getText();
                Intent intent = new Intent(getContext(), PendingEventDetails.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }

        });
        return view;
    }

    private void setUpFloatingActionButtons(View view) {
        FloatingActionButton submitArticle = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item1);
        FloatingActionButton submitEvent = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item2);
        FloatingActionButton submitNotice = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item3);
        FloatingActionButton submitFest = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item3);

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
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pending Events");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
