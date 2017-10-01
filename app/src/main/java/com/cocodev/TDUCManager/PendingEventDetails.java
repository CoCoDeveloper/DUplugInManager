package com.cocodev.TDUCManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import io.github.mthli.knife.KnifeText;

import static com.cocodev.TDUCManager.Utility.Utility.getTimeAgo;



public class PendingEventDetails extends AppCompatActivity {
    private String uid;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Event event=null;
    private ArrayList<String> categoryList;
    private LinearLayout Layout_categoryList;
    private KnifeText mDesc;
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
//        final Button buttonReject = (Button) findViewById(R.id.button_event_reject);
//        final Button buttonAccept = (Button) findViewById(R.id.button_event_accept);
        Layout_categoryList = (LinearLayout) findViewById(R.id.layout_categoryList);
        mDesc = (KnifeText) findViewById(R.id.pendingEventDetails_knifeText);

        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView temp = (TextView) v;
                final AlertDialog.Builder builder = new AlertDialog.Builder(PendingEventDetails.this);
                builder.setTitle("Edit Text");
                // Set up the input
                final EditText input = new EditText(PendingEventDetails.this);
                input.setText(temp.getText());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                if(temp.getId()==R.id.event_detail_title){
                    builder.setTitle("Edit Title");
                }else if(temp.getId()==R.id.event_detail_place){
                    builder.setTitle("Edit Venue");
                }
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(event!=null){
                            if(temp.getId()==R.id.event_detail_title){
                                event.setTitle(input.getText().toString());
                                temp.setText(input.getText().toString());
                            }else if(temp.getId()==R.id.event_detail_place){
                                event.setVenue(input.getText().toString());
                                temp.setText(input.getText().toString());
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        };
        titleView.setOnClickListener(onClickListener);
        eventPlace.setOnClickListener(onClickListener);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("PendingEvents").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                timeView.setText(getTimeAgo(PendingEventDetails.this,event.getTime()));
                titleView.setText(event.getTitle());
                eventPlace.setText(event.getVenue());
                mDesc.fromHtml(event.getDescription());
                if(!event.getUrl().equals("")) {
                    Picasso.with(PendingEventDetails.this).load(event.getUrl()).placeholder(R.drawable.event_place_holder).fit().centerInside().into(imageView);
                }
                categoryList = event.getCategoryList();
                if(categoryList!=null) {
                    Iterator<String> iterator = categoryList.iterator();
                    while (iterator.hasNext()) {
                        final String category = iterator.next();
                        final View view = LayoutInflater.from(PendingEventDetails.this).inflate(R.layout.category_list_view, null);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Layout_categoryList.removeView(view);
                                categoryList.remove(category);
                            }
                        });
                        Layout_categoryList.addView(view);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        buttonReject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase.getInstance().getReference()
//                        .child("RejectedEvents")
//                        .child(uid)
//                        .setValue(event);
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("PendingEvents")
//                        .child(uid)
//                        .removeValue(new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("EmployeeContent")
//                        .child(event.getOrganiser_uid())
//                        .child("Events")
//                        .child(event.getUID())
//                        .child("status")
//                        .setValue(-1);
//
//
//                finish();
//            }
//        });

//        buttonAccept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                event.setDescription(mDesc.toHtml());
//                Toast.makeText(PendingEventDetails.this, "Clicked", Toast.LENGTH_SHORT).show();
//                if(event!=null){
//                    Toast.makeText(PendingEventDetails.this, "Not null", Toast.LENGTH_SHORT).show();
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                    ArrayList<String> categories = event.getCategoryList();
//                    String college = event.getCollege();
//                    if((!college.equals("")) && college!=null && (!college.equals("University of Delhi"))){
//
//                        databaseReference
//                                .child("College Content")
//                                .child(college)
//                                .child("Events")
//                                .child(event.getUID())
//                                .setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                    Toast.makeText(PendingEventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("PendingEvents")
//                                            .child(uid)
//                                            .removeValue(new DatabaseReference.CompletionListener() {
//                                                @Override
//                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                    Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("EmployeeContent")
//                                            .child(event.getOrganiser_uid())
//                                            .child("Events")
//                                            .child(event.getUID())
//                                            .child("status")
//                                            .setValue(1);
//
//                                    finish();
//                            }
//                        });
//
//                        Iterator<String> iterator = categories.iterator();
//                        while(iterator.hasNext()){
//                            String category = iterator.next();
//                            databaseReference
//                                    .child("College Content")
//                                    .child("Categories")
//                                    .child("Events")
//                                    .child(category)
//                                    .child(event.getUID())
//                                    .setValue(event.getUID());
//                        }
//
//
//
//                    }else{
//                        databaseReference
//                                .child("Events")
//                                .child(event.getUID())
//                                .setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(PendingEventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("PendingEvents")
//                                        .child(uid)
//                                        .removeValue(new DatabaseReference.CompletionListener() {
//                                            @Override
//                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("EmployeeContent")
//                                        .child(event.getOrganiser_uid())
//                                        .child("Events")
//                                        .child(event.getUID())
//                                        .child("status")
//                                        .setValue(1);
//
//                                finish();
//                            }
//                        });
//
//                        Iterator<String> iterator = categories.iterator();
//                        while(iterator.hasNext()){
//                            String category = iterator.next();
//                            databaseReference
//                                    .child("Categories")
//                                    .child("Events")
//                                    .child(category)
//                                    .child(event.getUID())
//                                    .setValue(event.getUID());
//                        }
//
//                    }
//                }
//            }
//        });
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNavigationView.setItemIconTintList(null);
        MenuItem menuItem1=bottomNavigationView.getMenu().getItem(0);
        MenuItem menuItem2=bottomNavigationView.getMenu().getItem(1);
        menuItem1.setIcon(R.drawable.accept);
        menuItem2.setIcon(R.drawable.reject);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_accept:
                                event.setDescription(mDesc.toHtml());
                                Toast.makeText(PendingEventDetails.this, "Clicked", Toast.LENGTH_SHORT).show();
                                if(event!=null){
                                    Toast.makeText(PendingEventDetails.this, "Not null", Toast.LENGTH_SHORT).show();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    ArrayList<String> categories = event.getCategoryList();
                                    String college = event.getCollege();
                                    if((!college.equals("")) && college!=null && (!college.equals("University of Delhi"))){

                                        databaseReference
                                                .child("College Content")
                                                .child(college)
                                                .child("Events")
                                                .child(event.getUID())
                                                .setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PendingEventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("PendingEvents")
                                                        .child(uid)
                                                        .removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("EmployeeContent")
                                                        .child(event.getOrganiser_uid())
                                                        .child("Events")
                                                        .child(event.getUID())
                                                        .child("status")
                                                        .setValue(1);

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
                                                Toast.makeText(PendingEventDetails.this, "Event Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("PendingEvents")
                                                        .child(uid)
                                                        .removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("EmployeeContent")
                                                        .child(event.getOrganiser_uid())
                                                        .child("Events")
                                                        .child(event.getUID())
                                                        .child("status")
                                                        .setValue(1);

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
                                break;
                            case R.id.action_reject:
                                FirebaseDatabase.getInstance().getReference()
                                        .child("RejectedEvents")
                                        .child(uid)
                                        .setValue(event);

                                FirebaseDatabase.getInstance().getReference()
                                        .child("PendingEvents")
                                        .child(uid)
                                        .removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Toast.makeText(PendingEventDetails.this, "Event Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                FirebaseDatabase.getInstance().getReference()
                                        .child("EmployeeContent")
                                        .child(event.getOrganiser_uid())
                                        .child("Events")
                                        .child(event.getUID())
                                        .child("status")
                                        .setValue(-1);


                                finish();
                                break;
                        }
                        return true;
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
    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.bold(!mDesc.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.italic(!mDesc.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.underline(!mDesc.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.strikethrough(!mDesc.contains(KnifeText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.bullet(!mDesc.contains(KnifeText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.quote(!mDesc.contains(KnifeText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(PendingEventDetails.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = mDesc.getSelectionStart();
        final int end = mDesc.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                mDesc.link(link, start, end);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }
}
