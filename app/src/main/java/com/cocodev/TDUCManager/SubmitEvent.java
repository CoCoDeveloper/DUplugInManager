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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cocodev.TDUCManager.Gallery.Adapters.ImagesAdapter;
import com.cocodev.TDUCManager.Utility.EmployeeContentEvents;
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

import io.github.mthli.knife.KnifeText;

public class SubmitEvent extends AppCompatActivity {
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath = null;
    private ArrayList<String> selectedCategoriesList = new ArrayList<String>();
    LinearLayout linearLayout;

    ProgressDialog progressDialog;
    DatabaseReference mEventRef;
    EditText mTitle,mVenue, mImageUrl;
    Spinner collegeChoices, categoryChoices;
    TextView mDate, mTime;
    Button mSubmit, mImagePicker, mDatePicker, mTimePicker;
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
    private KnifeText mDesc;

    String uid, description, title, image, venue, newCategory;
    Long time, date;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    private static String DEFAULT_SPINNER_TEXT = "[Select a College..]";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upload Event");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        actionBar.setDisplayHomeAsUpEnabled(true);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        mEventRef = FirebaseDatabase.getInstance().getReference().child("PendingEvents"); //Changed Target Location

        mTitle = (EditText) findViewById(R.id.editText_event_title);
        mDesc = (KnifeText) findViewById(R.id.editText_event_desc);
        mVenue = (EditText) findViewById(R.id.editText_event_venue);
        mImageUrl = (EditText) findViewById(R.id.editText_event_image);

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

       // ImageGetter coming soon...

        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();

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
                    mMultiImageSelector.start(SubmitEvent.this, REQUEST_IMAGE);

                }
            }
        });
    }

    private void uploadImage() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            title = mTitle.getText().toString();
            venue = mVenue.getText().toString();
            description = mDesc.toHtml();
        } else {
            title = mTitle.getText().toString();
            venue = mVenue.getText().toString();
            description = mDesc.toHtml();
        }

        time = calendar.getTimeInMillis();
        if (filePath == null)
            image = mImageUrl.getText().toString();


        if (!checkFields())
            return;
        uid = mEventRef.push().getKey();
        if (mSelectedImagesList != null && mSelectedImagesList.size()!=0) {
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

        categoryChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    addNewCategory();
                } else if (position > 1) {
                    if (selectedCategoriesList.contains(categoryChoices.getSelectedItem().toString())) {
                        Toast.makeText(SubmitEvent.this, "This category has already been selected.", Toast.LENGTH_SHORT).show();
                    } else {
                        final String category = categoryChoices.getSelectedItem().toString();
                        selectedCategoriesList.add(category);
                        final View childView = LayoutInflater.from(SubmitEvent.this).inflate(R.layout.category_list_view,null);
                        childView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                linearLayout.removeView(childView);
                                selectedCategoriesList.remove(category);
                            }
                        });
                        childView.findViewById(R.id.deleteCategory).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                childView.callOnClick();
                            }
                        });
                        TextView textView = (TextView) childView.findViewById(R.id.category_title);
                        textView.setText(category);
                        linearLayout.addView(childView);
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

            event = new Event(selectedCategoriesList,uid, venue, time, description, image, date, title, college,MainActivity.currentUser.getUid());

            mEventRef.child(uid).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SubmitEvent.this, "Event has been submitted for Approval.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            EmployeeContentEvents temp = new EmployeeContentEvents(uid,0,college);
            FirebaseDatabase.getInstance().getReference().child("EmployeeContent").child(MainActivity.currentUser.getUid())
                    .child("Events")
                    .child(uid)
                    .setValue(temp);

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
                Toast.makeText(SubmitEvent.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitEvent.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
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
