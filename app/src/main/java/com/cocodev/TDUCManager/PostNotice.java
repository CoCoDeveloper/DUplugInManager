package com.cocodev.TDUCManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Notice;
import com.cocodev.TDUCManager.Utility.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostNotice extends AppCompatActivity {
    static int CHECKER = 0;
    DatabaseReference mNoticeRef;
    EditText mDesc,mDepartment,mDeadline;
    Button mSubmit;
    Notice notice;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    String description;
    String department;
    String deadline;
    String time;

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
        mNoticeRef = FirebaseDatabase.getInstance().getReference().child("notice");
        mDesc = (EditText)findViewById(R.id.editText_notice_desc);
        mDepartment = (EditText)findViewById(R.id.editText_notice_department);
        mDeadline = (EditText)findViewById(R.id.editText_notice_deadline);
        mSubmit = (Button)findViewById(R.id.button_notice_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindData();
            }
        });
    }
    private void bindData()
    {
        description = mDesc.getText().toString();
        department = mDepartment.getText().toString();
        deadline = mDeadline.getText().toString();
        time = getCurrentTime();
        if(checkFields()) {
            notice = new Notice(department, time, deadline, description);
            mNoticeRef.push().setValue(notice).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PostNotice.this, "Notice Uploaded!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private boolean checkFields()
    {
        if(TextUtils.isEmpty(description)) {
            mDesc.setError("Field must not be empty");
            return false;
        }
        if(TextUtils.isEmpty(department)) {
            mDepartment.setError("Field must not be empty");
            return false;
        }
        if(TextUtils.isEmpty(deadline)) {
            mDeadline.setError("Field must not be empty");
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
