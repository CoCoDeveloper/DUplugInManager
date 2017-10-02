package com.cocodev.TDUCManager;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cocodev.TDUCManager.Gallery.Adapters.ImagesAdapter;
import com.cocodev.TDUCManager.Utility.Article;
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
import java.util.Iterator;
import java.util.List;

public class SubmitArticle extends AppCompatActivity {
    ImageView imgView;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    DatabaseReference mArticleRef;
    ProgressDialog progressDialog;
    EditText mTagline, mTitle, mFullArticle, mAuthor, mDepartment, mImageUrl;
    Article article;
    Button mSubmit, mImagePicker;
    FirebaseUser user;
    String writerUID;

    Spinner departmentChoices,collegeChoices,categoryChoices;
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
    private String Uid;
    private String Tagline;
    private String Author;
    private String Title;
    private String Content;

    private String Department;
    private String Image;
    private ArrayAdapter<String> collegeAdapter;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    private static String DEFAULT_SPINNER_TEXT = "[Select a College..]";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upload SubmitArticle");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        actionBar.setDisplayHomeAsUpEnabled(true);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        writerUID = mFirebaseUser.getUid();

        mArticleRef = FirebaseDatabase.getInstance().getReference().child("SubmitArticle");

        mTagline = (EditText) findViewById(R.id.editText_tagline);
        mTitle = (EditText) findViewById(R.id.editText_article_title);
        mFullArticle = (EditText) findViewById(R.id.editText_article);
        mAuthor = (EditText) findViewById(R.id.editText_author);
        mImageUrl = (EditText) findViewById(R.id.editText_image);
        imgView = (ImageView) findViewById(R.id.image_view_show_article);
        mImagePicker = (Button) findViewById(R.id.button_image_picker);
        collegeChoices = (Spinner) findViewById(R.id.spinner_college_articles);
        departmentChoices = (Spinner) findViewById(R.id.spinner_department_articles);
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.setLayoutManager(gridLayoutManager);
        mMultiImageSelector = MultiImageSelector.create();

        categoryChoices = (Spinner) findViewById(R.id.spinner_category_articles);


        initCollegeSpinner();
        initCategorySpinner();


        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions()) {
                    mMultiImageSelector.showCamera(true);
                    mMultiImageSelector.count(MAX_IMAGE_SELECTION_LIMIT);
                    mMultiImageSelector.multi();
                    mMultiImageSelector.origin(mSelectedImagesList);
                    mMultiImageSelector.start(SubmitArticle.this, REQUEST_IMAGE);

                }
            }
        });

        mSubmit = (Button) findViewById(R.id.button_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

    }

    private void uploadImage() {
        Uid = mArticleRef.push().getKey();
        Tagline = mTagline.getText().toString();
        Author = mAuthor.getText().toString();
        Title = mTitle.getText().toString();
        Content = mFullArticle.getText().toString();
        if(!checkFields())
            return;
        Uid = mArticleRef.push().getKey();
        if (mSelectedImagesList != null && mSelectedImagesList.size()!=0) {
            progressDialog.show();
            Iterator<Uri> iterator = mSelectedImagesUri.iterator();

            while (iterator.hasNext()){
                StorageReference childRef = storageReference.child("SubmitArticle").child(Uid).child(mArticleRef.push().getKey());
                //uploading the image

                UploadTask uploadTask = childRef.putFile((iterator.next()));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Uri imageurl = taskSnapshot.getDownloadUrl();
                        mSelectedImagesUrls.add(imageurl.toString());
                        bindData();

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

    private void initCategorySpinner() {

        final ArrayList<String> category =new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,category);
        categoryChoices.setAdapter(new NothingSelectedSpinnerAdapter(
                arrayAdapter,
                R.layout.category_spinner_row_nothing_selected,
                this));
        DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CategoryList").child("SubmitArticle");
        collegesDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
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
        // TODO :categoryChoices.setOnItemSelectedListener(categorySelectedListener);
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

    private void bindData() {


        if(departmentChoices.getSelectedItemPosition()==0){
            Department="";
        }else {
            Department = (String) departmentChoices.getSelectedItem();
        }
        mImageUrl.setText(Image);

        article = new Article(Uid,Author, Content, System.currentTimeMillis(), Tagline, Image, Title, writerUID, Department, (String) collegeChoices.getSelectedItem());

        if(collegeChoices.getSelectedItemPosition()>1){
            Toast.makeText(this, "selected", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference()
                    .child("College Content")
                    .child((String) collegeChoices.getSelectedItem())
                    .child("SubmitArticle")
                    .child(Uid)
                    .setValue(article);


            if(departmentChoices.getSelectedItemPosition()>0){
                FirebaseDatabase.getInstance().getReference()
                        .child("College Content")
                        .child(MainActivity.CollegeName)
                        .child("Department")
                        .child((String) departmentChoices.getSelectedItem())
                        .child(Uid)
                        .setValue(Uid);
            }



        }

    }

    private boolean checkFields() {

        if (TextUtils.isEmpty(Author)) {
            mAuthor.setError("Field must not be empty");
            return false;
        }
        if (TextUtils.isEmpty(Title)) {
            mTitle.setError("Field must not be empty");
            return false;
        }
        if (TextUtils.isEmpty(Content)) {
            mFullArticle.setError("Field must not be empty");
            return false;
        }

        return true;
    }


    private static String getCurrentTime() {
        Long time = System.currentTimeMillis();
        String ts = time.toString();
        return ts;
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
}
