package com.cocodev.TDUCManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Article;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Article_details extends AppCompatActivity {

    private Article article;
    public static final String key = "article";
    public static final String key_status = "article_status";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        Intent intent = getIntent();
        String UID = intent.getStringExtra(key);
        int status = intent.getIntExtra(key_status,-5643);

        if(status==-5643){
            Toast.makeText(this, "Sorry This Article Has Been Deleted", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TextView titleView= (TextView) findViewById(R.id.article_title);;
        final TextView timeView= (TextView) findViewById(R.id.article_time);;
        final TextView authorView= (TextView) findViewById(R.id.article_author);;
        final TextView descriptionView= (TextView) findViewById(R.id.article_description);

        ImageView imageView = (ImageView) findViewById(R.id.articleImage);

        DatabaseReference dbref ;

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if(article==null)
                    return;

                titleView.setText(article.getTitle());
                descriptionView.setText(article.getDescription());
                authorView.setText(article.getAuthor());
                timeView.setText(Long.toString(article.getTime()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbref2;
        if(status==0){
            dbref = firebaseDatabase.getReference().child("PendingArticles")
                    .child(MainActivity.CollegeName).child("Pending").child(UID);
        }else if(status==-1){
            dbref = firebaseDatabase.getReference().child("PendingArticles")
                    .child(MainActivity.CollegeName).child("Rejected").child(UID);
        }else{
            dbref = firebaseDatabase.getReference().child("Articles").child(UID);
            dbref2 = firebaseDatabase.getReference().child("College Content").child(MainActivity.CollegeName).child("Articles").child(UID);
            dbref2.addListenerForSingleValueEvent(valueEventListener);
        }
        dbref.addValueEventListener(valueEventListener);




    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
