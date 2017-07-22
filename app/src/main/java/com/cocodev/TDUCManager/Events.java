package com.cocodev.TDUCManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Event;
import com.cocodev.TDUCManager.Utility.User;
import com.cocodev.TDUCManager.adapter.NothingSelectedSpinnerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Events extends AppCompatActivity {
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog progressDialog;
    DatabaseReference mEventRef;
    EditText mTitle, mDesc, mVenue, mImageUrl;
    Spinner departmentChoices;
    Spinner collegeChoices;
    TextView mDate;
    Button mSubmit, mImagePicker,mDatePicker;

    Event event;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;

    private Calendar calendar;
    private int day,month,year;
    private long epoch;
    String uid,description,title,image,venue,department;
    Long time,date;


    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    private static String DEFAULT_SPINNER_TEXT = "[Select a College..]";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upload Events");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        actionBar.setDisplayHomeAsUpEnabled(true);




        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        mEventRef = FirebaseDatabase.getInstance().getReference().child("Events");

        mTitle = (EditText) findViewById(R.id.editText_event_title);
        mDesc = (EditText) findViewById(R.id.editText_event_desc);
        mVenue = (EditText) findViewById(R.id.editText_event_venue);
        mImageUrl = (EditText) findViewById(R.id.editText_event_image);
        departmentChoices = (Spinner)findViewById(R.id.spinner_event_department);
        collegeChoices = (Spinner) findViewById(R.id.spinner_college_events);
        mSubmit = (Button) findViewById(R.id.button_event_submit);
        imgView = (ImageView) findViewById(R.id.image_view_show);
        mImagePicker = (Button) findViewById(R.id.button_image_picker_event);
        mDatePicker = (Button)findViewById(R.id.button_event_datePicker);
        mDate = (TextView)findViewById(R.id.textView_event_date);

        initCollegeSpinner();

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        showDate(day,month+1,year);

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1337);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void uploadImage() {
        if (filePath != null) {
            progressDialog.show();

            StorageReference childRef = storageReference.child("Events").child(filePath.getLastPathSegment());
            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri imageurl = taskSnapshot.getDownloadUrl();
                    image = imageurl.toString();
                    Toast.makeText(Events.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    bindData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            image = "";
            bindData();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==1337)
            return new DatePickerDialog(this,myDateListener,year,month,day);
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            /*
            i = year
            i1 = month
            i2 = day
             */
            showDate(i2,i1+1,i);
        }
    };

    private void showDate(int day, int month, int year)
    {
        String date  = new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
        mDate.setText(date);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = (Date) format.parse(date);
            epoch =  date1.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindData() {
        description = mDesc.getText().toString();
        time = getCurrentTime();
        title = mTitle.getText().toString();
        venue = mVenue.getText().toString();
        if(departmentChoices.getSelectedItemPosition()==0){
            department="";
        }else {
            department = (String) departmentChoices.getSelectedItem();
        }
        date = epoch;

        uid = mEventRef.push().getKey();


        mImageUrl.setText(image);

        if (checkFields()) {

            event = new Event(uid,venue, time, description, image, title,department,date);




            if(collegeChoices.getSelectedItemPosition()>1){
                FirebaseDatabase.getInstance().getReference().child("College Content")
                        .child((String)collegeChoices.getSelectedItem())
                        .child("Events")
                        .child(uid)
                        .setValue(event);


                if(!department.equals("")){
                    FirebaseDatabase.getInstance().getReference().child("College Content")
                            .child((String)collegeChoices.getSelectedItem())
                            .child("Department")
                            .child(department)
                            .child(uid)
                            .setValue(uid);
                }
                Toast.makeText(this,"Event Uploaded!",Toast.LENGTH_SHORT).show();
            }else{
                mEventRef.child(uid).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Events.this, "Event Uploaded!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    }
    private void initCollegeSpinner() {

        final ArrayList<String> colleges =new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,colleges);
        collegeChoices.setAdapter(new NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.contact_spinner_row_nothing_selected,
                this));
        DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CollegeList");
        arrayAdapter.add("University of Delhi");
        collegesDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot temp = iterator.next();
                    //get name of the department
                    String college = temp.getKey().toString();
                    colleges.add(college);
                    //to reflect changes in the ui
                    arrayAdapter.notifyDataSetChanged();
                    //collegeChoices.setSelection(arrayAdapter.getPosition(department));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //We will see this later

            }
        });
        collegeChoices.setOnItemSelectedListener(collegeSelectedListener);
    }

    private boolean checkFields() {
        if (TextUtils.isEmpty(description)) {
            mDesc.setError("Field must not be empty");
            return false;
        }
        if (TextUtils.isEmpty(title)) {
            mTitle.setError("Field must not be empty");
            return false;
        }

        if (TextUtils.isEmpty(venue)) {
            mVenue.setError("Field must not be empty");
            return false;
        }
        return true;
    }
    AdapterView.OnItemSelectedListener collegeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e("tag","Position  = " + Integer.toString(position));
            if(position==0 || position==1){
                departmentChoices.setVisibility(View.GONE);
                departmentChoices.setSelection(0);
            }else {
                departmentChoices.setVisibility(View.VISIBLE);
                initDepartmentSpinner();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initDepartmentSpinner() {
        final ArrayList<String> departments =new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,departments);
        departmentChoices.setAdapter(new NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.contact_spinner_row_nothing_selected_department,
                this));

        DatabaseReference departmensDR = FirebaseDatabase.getInstance().getReference().child("CollegeList")
                .child((String)collegeChoices.getSelectedItem());

        departmensDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot temp = iterator.next();
                    //get name of the department
                    String department = temp.getKey().toString();
                    departments.add(department);
                    //to reflect changes in the ui
                    arrayAdapter.notifyDataSetChanged();
                    //collegeChoices.setSelection(arrayAdapter.getPosition(department));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //We will see this later

            }
        });

    }

    private static Long getCurrentTime() {
        Long time = System.currentTimeMillis();
        return time;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                currentUser = null;
                mFirebaseUser = null;
                mFirebaseAuth.signOut();
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
