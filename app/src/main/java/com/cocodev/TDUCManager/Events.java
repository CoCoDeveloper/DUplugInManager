package com.cocodev.TDUCManager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Events extends AppCompatActivity {
    DatabaseReference mEventRef;
    EditText mTitle,mDesc,mVenue,mImageUrl;
    Button mSubmit;
    Event event;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mEventRef = FirebaseDatabase.getInstance().getReference().child("events");
        mTitle = (EditText)findViewById(R.id.editText_event_title);
        mDesc = (EditText)findViewById(R.id.editText_event_desc);
        mVenue = (EditText)findViewById(R.id.editText_event_venue);
        mImageUrl = (EditText)findViewById(R.id.editText_event_image);
        mSubmit = (Button) findViewById(R.id.button_event_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindData();
            }
        });
    }
    private void bindData()
    {
        String description = mDesc.getText().toString();
        String time = getCurrentTime();
        String title = mTitle.getText().toString();
        String image = mImageUrl.getText().toString();
        String venue = mVenue.getText().toString();
        event = new Event(venue,time,description,image,title);
        mEventRef.push().setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Events.this,"Event Uploaded!", Toast.LENGTH_LONG).show();
            }
        });

    }
    private static String getCurrentTime() {
        Long time = System.currentTimeMillis();
        String ts = time.toString();
        return  ts;
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
