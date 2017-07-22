package com.cocodev.TDUCManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.Notice;
import com.cocodev.TDUCManager.Utility.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostNotice extends AppCompatActivity {
    DatabaseReference mNoticeRef;
    EditText mDesc,mDepartment;
    TextView mDeadline;
    Button mSubmit,mDatePicker;
    Notice notice;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    private String description,department,deadline,time,date;
    private Calendar calendar;
    private int day,month,year;
    private long epoch;

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
        mDeadline = (TextView)findViewById(R.id.textView_notice_deadline);
        mSubmit = (Button)findViewById(R.id.button_notice_submit);
        mDatePicker = (Button)findViewById(R.id.button_datePicker);

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
                bindData();
            }
        });

    }
    private void bindData()
    {
        description = mDesc.getText().toString();
        department = mDepartment.getText().toString();
        deadline = String.valueOf(epoch);
        time = getCurrentTime();
        if(checkFields()) {
            notice = new Notice(department, time, deadline, description);
            mNoticeRef.push().setValue(notice).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Notices.this, "Notice Uploaded!", Toast.LENGTH_SHORT).show();
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
        date  = new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
        mDeadline.setText(date);
        epochGenerator();
    }
    private  void epochGenerator ()
    {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = (Date) format.parse(date);
            epoch =  date1.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
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