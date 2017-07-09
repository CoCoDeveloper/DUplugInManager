package com.cocodev.TDUCManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Article;
import com.cocodev.TDUCManager.Utility.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class Articles extends AppCompatActivity {
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
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    private String Tagline;
    private String Author;
    private String Title;
    private String Content;
    private String Date;
    private String Department;
    private String Image;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

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
        actionBar.setTitle("Upload Articles");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        writerUID = mFirebaseUser.getUid();
        mArticleRef = FirebaseDatabase.getInstance().getReference().child("articles");
        mTagline = (EditText) findViewById(R.id.editText_tagline);
        mTitle = (EditText) findViewById(R.id.editText_article_title);
        mFullArticle = (EditText) findViewById(R.id.editText_article);
        mAuthor = (EditText) findViewById(R.id.editText_author);
        mDepartment = (EditText) findViewById(R.id.editText_department);
        mImageUrl = (EditText) findViewById(R.id.editText_image);
        imgView = (ImageView) findViewById(R.id.image_view_show_article);
        mImagePicker = (Button) findViewById(R.id.button_image_picker);
        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
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
        if (filePath != null) {
            progressDialog.show();

            StorageReference childRef = storageReference.child("Articles").child(filePath.getLastPathSegment());
            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri imageurl = taskSnapshot.getDownloadUrl();
                    Image = imageurl.toString();
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_SHORT).show();
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

        Tagline = mTagline.getText().toString();
        Author = mAuthor.getText().toString();
        Title = mTitle.getText().toString();
        Content = mFullArticle.getText().toString();
        Date = getCurrentTime();
        Department = mDepartment.getText().toString();
        mImageUrl.setText(Image);

        if (checkFields()) {
            article = new Article(Author, Content, Date, Tagline, Image, Title, writerUID, Department);
            mArticleRef.push().setValue(article).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Articles.this, "Article Uploaded!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private boolean checkFields() {
        if (TextUtils.isEmpty(Tagline)) {
            mTagline.setError("Field must not be empty");
            return false;
        }
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
        if (TextUtils.isEmpty(Department)) {
            mDepartment.setError("Field must not be empty");
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
