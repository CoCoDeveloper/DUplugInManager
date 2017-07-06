package com.cocodev.TDUCManager;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Article;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Articles extends AppCompatActivity {

    DatabaseReference mArticleRef;
    EditText mTagline,mTitle,mFullArticle,mAuthor,mDepartment,mImageUrl;
    Article article;
    Button mSubmit;
    FirebaseUser user;
    String writerUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        user = FirebaseAuth.getInstance().getCurrentUser();
        writerUID = user.getUid();
        mArticleRef = FirebaseDatabase.getInstance().getReference().child("articles");
        mTagline = (EditText)findViewById(R.id.editText_tagline);
        mTitle = (EditText)findViewById(R.id.editText_article_title);
        mFullArticle = (EditText)findViewById(R.id.editText_article);
        mAuthor = (EditText)findViewById(R.id.editText_author);
        mDepartment = (EditText)findViewById(R.id.editText_department);
        mImageUrl = (EditText)findViewById(R.id.editText_image);
        mSubmit = (Button) findViewById(R.id.button_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindData();
            }
        });

    }

    private void bindData()
    {

        String Tagline = mTagline.getText().toString();
        String Author = mAuthor.getText().toString();
        String Title = mTitle.getText().toString();
        String Content = mFullArticle.getText().toString();
        String Date = getCurrentTime();
        String Department = mDepartment.getText().toString();
        String Image = mImageUrl.getText().toString();
        article = new Article(Author,Content,Date,Tagline,Image,Title,writerUID,Department);
        mArticleRef.push().setValue(article).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Articles.this,"Article Uploaded!",Toast.LENGTH_LONG).show();
            }
        });

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
}
