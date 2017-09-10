package com.cocodev.TDUCManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import static android.text.Html.fromHtml;
import static com.cocodev.TDUCManager.Utility.Utility.getTimeAgo;

public class EventDetails extends AppCompatActivity {
    private String uid;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        final ImageView imageView = (ImageView) findViewById(R.id.event_image);
        final TextView titleView = (TextView) findViewById(R.id.event_detail_title);
        final TextView timeView = (TextView) findViewById(R.id.event_detail_time);
        final TextView eventPlace = (TextView) findViewById(R.id.event_detail_place);
        final TextView descriptionView = (TextView) findViewById(R.id.event_detail_desc);
        final Button buttonReject = (Button) findViewById(R.id.button_event_reject);
        final Button buttonAccept = (Button) findViewById(R.id.button_event_accept);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("PendingEvents").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 event = dataSnapshot.getValue(Event.class);
                timeView.setText(getTimeAgo(EventDetails.this,event.getTime()));
                titleView.setText(event.getTitle());
                eventPlace.setText(event.getVenue());
                descriptionView.setText(fromHtml(event.getDescription()));
                if(!event.getUrl().equals("")) {
                    Picasso.with(EventDetails.this).load(event.getUrl()).placeholder(R.drawable.event_place_holder).fit().centerInside().into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("PendingEvents")
                        .child(uid)
                        .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(EventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EventDetails.this, "Clicked", Toast.LENGTH_SHORT).show();
                if(event!=null){
                    Toast.makeText(EventDetails.this, "Not null", Toast.LENGTH_SHORT).show();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    ArrayList<String> categories = event.getCategoryList();
                    String college = event.getCollege();
                    String department = event.getDepartment();
                    if((!college.equals("")) && college!=null && (!college.equals("University of Delhi"))){

                        databaseReference
                                .child("College Content")
                                .child(college)
                                .child("Events")
                                .child(event.getUID())
                                .setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("PendingEvents")
                                        .child(uid)
                                        .removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Toast.makeText(EventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                finish();
                            }
                        });

                        Iterator<String> iterator = categories.iterator();
                        while(iterator.hasNext()){
                            String category = iterator.next();
                            databaseReference
                                    .child("College Content")
                                    .child("Categories")
                                    .child("Events")
                                    .child(category)
                                    .child(event.getUID())
                                    .setValue(event.getUID());
                        }



                    }else{
                        databaseReference
                                .child("Events")
                                .child(event.getUID())
                                .setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("PendingEvents")
                                        .child(uid)
                                        .removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Toast.makeText(EventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                finish();
                            }
                        });

                        Iterator<String> iterator = categories.iterator();
                        while(iterator.hasNext()){
                            String category = iterator.next();
                            databaseReference
                                    .child("Categories")
                                    .child("Events")
                                    .child(category)
                                    .child(event.getUID())
                                    .setValue(event.getUID());
                        }

                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
