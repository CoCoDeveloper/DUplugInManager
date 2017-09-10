package com.cocodev.TDUCManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cocodev.TDUCManager.Gallery.Adapters.ImagesAdapter;
import com.cocodev.TDUCManager.Utility.Event;
import com.cocodev.TDUCManager.Utility.MultiImageSelector;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Events extends AppCompatActivity {
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath = null;
    private ArrayList<String> selectedCategoriesList = new ArrayList<String>();
    LinearLayout linearLayout;

    ProgressDialog progressDialog;
    DatabaseReference mEventRef;
    EditText mTitle, mDesc, mVenue, mImageUrl;
    Spinner departmentChoices, collegeChoices, categoryChoices;
    TextView mDate, mTime;
    Button mSubmit, mImagePicker, mDatePicker, mTimePicker, mAddCustomCategory;
    Event event;
    private RecyclerView recyclerViewImages;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> mSelectedImagesList = new ArrayList<>();
    private ArrayList<String> mSelectedImagesUrls = new ArrayList<>();
    private ArrayList<Uri> mSelectedImagesUri = new ArrayList<>();
    private final int MAX_IMAGE_SELECTION_LIMIT = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 401;
    private final int REQUEST_IMAGE = 301;
    private MultiImageSelector mMultiImageSelector;
    private ImagesAdapter mImagesAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    private Calendar calendar;
    private int day, month, year, hour, minute;
    private long epoch, timeEpoch;
    String uid, description, title, image, venue, department, newCategory;
    Long time, date;

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

        mEventRef = FirebaseDatabase.getInstance().getReference().child("PendingEvents"); //Changed Target Location

        mTitle = (EditText) findViewById(R.id.editText_event_title);
        mDesc = (EditText) findViewById(R.id.editText_event_desc);
        mVenue = (EditText) findViewById(R.id.editText_event_venue);
        mImageUrl = (EditText) findViewById(R.id.editText_event_image);
        departmentChoices = (Spinner) findViewById(R.id.spinner_event_department);
        collegeChoices = (Spinner) findViewById(R.id.spinner_college_events);
        categoryChoices = (Spinner) findViewById(R.id.spinner_event_category);
        mSubmit = (Button) findViewById(R.id.button_event_submit);
        imgView = (ImageView) findViewById(R.id.image_view_show);
        mImagePicker = (Button) findViewById(R.id.button_image_picker_event);
        mDatePicker = (Button) findViewById(R.id.button_event_datePicker);
        mDate = (TextView) findViewById(R.id.textView_event_date);
        mTimePicker = (Button) findViewById(R.id.button_event_timePicker);
        mTime = (TextView) findViewById(R.id.textView_event_time);
        linearLayout = (LinearLayout) findViewById(R.id.events_categoriesList);
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        mMultiImageSelector = MultiImageSelector.create();
        initCollegeSpinner();
        initCategorySpinner();

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        showDate(day, month + 1, year);
        showTime(hour, minute);

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1337);
            }
        });

        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1338);
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
                if (checkAndRequestPermissions()) {
                    mMultiImageSelector.showCamera(true);
                    mMultiImageSelector.count(MAX_IMAGE_SELECTION_LIMIT);
                    mMultiImageSelector.multi();
                    mMultiImageSelector.origin(mSelectedImagesList);
                    mMultiImageSelector.start(Events.this, REQUEST_IMAGE);

                }
            }
        });
    }

    private void uploadImage() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            title = mTitle.getText().toString();
            venue = mVenue.getText().toString();
            description = Html.toHtml(mDesc.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        } else {
            title = mTitle.getText().toString();
            venue = mVenue.getText().toString();
            description = Html.toHtml(mDesc.getText());
        }

        time = calendar.getTimeInMillis();
        if (filePath == null)
            image = mImageUrl.getText().toString();


        if (!checkFields())
            return;
        uid = mEventRef.push().getKey();
        if (mSelectedImagesList != null) {
            progressDialog.show();
            Iterator<Uri> iterator = mSelectedImagesUri.iterator();

            while (iterator.hasNext()){
                StorageReference childRef = storageReference.child("Events").child(uid).child(mEventRef.push().getKey());
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

    public void addNewCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newCategory = input.getText().toString();
                DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CategoryList").child("Events");
                collegesDR.child(newCategory).setValue("true");

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

    private void showTime(int hour, int minute) {
        String time = new StringBuilder().append(hour).append(":").append(minute).toString();
        epoch = calendar.getTimeInMillis();
        mTime.setText(String.valueOf(time));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1337)
            return new DatePickerDialog(this, myDateListener, year, month, day);
        if (id == 1338)
            return new TimePickerDialog(this, myTimeListener, hour, minute, false);
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
            year = i;
            month = i1;
            day = i2;
            calendar.set(i, i1, i2);
            showDate(i2, i1 + 1, i);
        }
    };
    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            /*
            i = hour
            i1 = minute
             */
            calendar.set(year, month, day, i, i1);
            showTime(i, i1);

        }
    };

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
            mImagePicker.performClick();
        }
    }

    private void showDate(int day, int month, int year) {
        String date = new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
        mDate.setText(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void initCategorySpinner() {

        final ArrayList<String> category = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, category);
        categoryChoices.setAdapter(new NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.category_spinner_row_nothing_selected,
                this));
        DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CategoryList").child("Events");

        arrayAdapter.add("Add New Category");
        arrayAdapter.add("University of Delhi");
        categoryChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    addNewCategory();
                } else if (position > 1) {
                    if (selectedCategoriesList.contains(categoryChoices.getSelectedItem().toString())) {
                        Toast.makeText(Events.this, "contains", Toast.LENGTH_SHORT).show();
                    } else {
                        String category = categoryChoices.getSelectedItem().toString();
                        selectedCategoriesList.add(category);
                        final TextView textView = new TextView(Events.this);
                        textView.setText(category);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        linearLayout.addView(textView);
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        collegesDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot temp = iterator.next();
                    //get name of the department
                    String college = temp.getKey().toString();
                    category.add(college);
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

    private void bindData() {

        if (departmentChoices.getSelectedItemPosition() == 0) {
            department = "";
        } else {
            department = (String) departmentChoices.getSelectedItem();
        }
        if(mSelectedImagesUrls.size()>0){
            image = mSelectedImagesUrls.get(0);
        }else{
            image ="";
        }
        date = epoch;
        uid = mEventRef.push().getKey();
        String college = "";
        if (collegeChoices.getSelectedItemPosition() > 1) {
            college = collegeChoices.getSelectedItem().toString();
        }
        mImageUrl.setText(image);
        if (checkFields()) {

            event = new Event(selectedCategoriesList, uid, venue, time, description, department, image, date, title, college);

            mEventRef.child(uid).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Events.this, "Event has been submitted for Approval.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

    }

    private void initCollegeSpinner() {

        final ArrayList<String> colleges = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colleges);
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
                while (iterator.hasNext()) {
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
            Log.e("tag", "Position  = " + Integer.toString(position));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initDepartmentSpinner() {
        final ArrayList<String> departments = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        departmentChoices.setAdapter(new NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.contact_spinner_row_nothing_selected_department,
                this));

        DatabaseReference departmensDR = FirebaseDatabase.getInstance().getReference().child("CollegeList")
                .child((String) collegeChoices.getSelectedItem());

        departmensDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
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
