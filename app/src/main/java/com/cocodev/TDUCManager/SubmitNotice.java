package com.cocodev.TDUCManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Gallery.Adapters.ImagesAdapter;
import com.cocodev.TDUCManager.Utility.MultiImageSelector;
import com.cocodev.TDUCManager.Utility.Notice;
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

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SubmitNotice extends ActionBarActivity {

    DatabaseReference mNoticeRef;
    EditText mDesc,mTitle;
    TextView mDeadline;
    Button mSubmit,mDatePicker;
    Notice notice;
    Spinner departmentChoices;
    Spinner collegeChoices;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    private String uid,description,department,date,title;
    Long time,deadline;
    private Calendar calendar;
    private int day,month,year;
    private long epoch;
    private RecyclerView recyclerViewImages;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> mSelectedImagesList = new ArrayList<>();
    private ArrayList<String> mSelectedImagesUrls = new ArrayList<>();
    private ArrayList<Uri> mSelectedImagesUri = new ArrayList<>();
    private final int MAX_IMAGE_SELECTION_LIMIT=100;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 401;
    private final int REQUEST_IMAGE=301;
    private MultiImageSelector mMultiImageSelector;
    private ImagesAdapter mImagesAdapter;
    private ImageButton imagePicker;
    private ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();


    private static String DEFAULT_SPINNER_TEXT = "[Select a College..]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upload Notices");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        mNoticeRef = FirebaseDatabase.getInstance().getReference().child("Notices");

        mDesc = (EditText)findViewById(R.id.editText_notice_desc);

        mDeadline = (TextView)findViewById(R.id.textView_notice_deadline);
        mSubmit = (Button)findViewById(R.id.button_notice_submit);
        collegeChoices = (Spinner) findViewById(R.id.spinner_department_notices);
        departmentChoices = (Spinner)findViewById(R.id.spinner_college_notices);
        mTitle  = (EditText) findViewById(R.id.editText_notice_title);
        imagePicker = (ImageButton) findViewById(R.id.image_button_notice);
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        mMultiImageSelector = MultiImageSelector.create();
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions()) {
                    mMultiImageSelector.showCamera(true);
                    mMultiImageSelector.count(MAX_IMAGE_SELECTION_LIMIT);
                    mMultiImageSelector.multi();
                    mMultiImageSelector.origin(mSelectedImagesList);
                    mMultiImageSelector.start(SubmitNotice.this, REQUEST_IMAGE);

                }
            }
        });


        mDatePicker = (Button)findViewById(R.id.button_datePicker);

        initCollegeSpinner();

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        showDate(day,month+1,year);

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();

            }
        });

    }

    private void uploadImage() {

        uid = mNoticeRef.push().getKey();
        if (mSelectedImagesList != null && mSelectedImagesList.size()!=0) {
            progressDialog.show();
            Iterator<Uri> iterator = mSelectedImagesUri.iterator();
            boolean  check=true;

            while (iterator.hasNext()){
                StorageReference childRef = storageReference.child("Notice").child(uid).child(mNoticeRef.push().getKey());
                //uploading the image

                UploadTask uploadTask = childRef.putFile((iterator.next()));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Uri imageurl = taskSnapshot.getDownloadUrl();
                        mSelectedImagesUrls.add(imageurl.toString());
                        if(mSelectedImagesList.size()==mSelectedImagesUrls.size()) {
                            bindData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }else{
            bindData();
        }

    }
    private void bindData()
    {
        title = mTitle.getText().toString();
        description = mDesc.getText().toString();
        deadline = epoch;

        time = System.currentTimeMillis();
        if(checkFields()) {
            notice = new Notice(uid,title,department, time, deadline, description,mSelectedImagesUrls);
            if(collegeChoices.getSelectedItemPosition()>1){
                FirebaseDatabase.getInstance().getReference().child("College Content")
                        .child((String)collegeChoices.getSelectedItem())
                        .child("Notices")
                        .child(uid)
                        .setValue(notice);


                Toast.makeText(this,"Notice Uploaded!",Toast.LENGTH_SHORT).show();
            }else{
                mNoticeRef.child(uid).setValue(notice).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SubmitNotice.this, "Notice Uploaded!", Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

    }


    AdapterView.OnItemSelectedListener collegeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e("tag","Position  = " + Integer.toString(position));
            if(position==0 || position==1){
                departmentChoices.setVisibility(View.GONE);

            }else {
                departmentChoices.setVisibility(View.VISIBLE);
                initDepartmentSpinner();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
    private boolean checkFields()
    {
        if(TextUtils.isEmpty(description)) {
            mDesc.setError("Field must not be empty");
            return false;
        }


        return true;

    }
    private static String getCurrentTime()
    {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==999)
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
        mDeadline.setText(date);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = (Date) format.parse(date);
            epoch =  date1.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private boolean checkAndRequestPermissions() {
        int externalStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (externalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            imagePicker.performClick();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            try {

                mSelectedImagesList = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                mImagesAdapter = new ImagesAdapter(this,mSelectedImagesList);
                recyclerViewImages.setAdapter(mImagesAdapter);
                Iterator<String> iterator = mSelectedImagesList.iterator();
                while (iterator.hasNext()) {
                    mSelectedImagesUri.add(Uri.fromFile(new File(iterator.next())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                currentUser=null;
                mFirebaseUser=null;
                mFirebaseAuth.signOut();
                Intent intent = new Intent(this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                return  true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
