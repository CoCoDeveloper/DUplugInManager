package com.cocodev.TDUCManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_COLLEGE    = "com.cocodev.myapplication.collegechoices";
    public static final String KEY_DEPARTMENT = "com.cocodev.myapplication.departmentchoices";
    public static final String fileName_HP    = "com.cocodev.myapplication.homepreferences";
    public static final String fileName_EP    = "com.cocodev.myapplication.eventpreferences";

    //if College or Department has changed
    private boolean RELOAD_PARENT_ACTIVITY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sa);
        DatabaseReference hpDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child("SubmitArticle");
        hpDatabaseReference.addListenerForSingleValueEvent(hpValueEventListener);
        DatabaseReference epDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Categories").child("SubmitEvent");
        epDatabaseReference.addListenerForSingleValueEvent(epValueEventListener);

        initCollegeSpinner();
        initDepartmentSpinner();

    }

    private void initDepartmentSpinner() {
        final Spinner departmentChoices = (Spinner) findViewById(R.id.spinner_course);
        final ArrayList<String> departments =new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,departments);
        DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CollegeList").child("Keshav Mahavidyalya");
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        final String selection = sharedpref.getString(KEY_DEPARTMENT,"");
        collegesDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot temp = iterator.next();
                    String department = temp.getKey().toString();
                    departments.add(department);
                    arrayAdapter.notifyDataSetChanged();
                    if(department.equals(selection)){
                        departmentChoices.setSelection(arrayAdapter.getPosition(department));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this,"There was some error establishing connection with the server",Toast.LENGTH_SHORT).show();
            }

        });

        departmentChoices.setAdapter(arrayAdapter);
        departmentChoices.setOnItemSelectedListener(departmentSelectedListener);



    }

    private void initCollegeSpinner() {
        final Spinner collegeChoices = (Spinner) findViewById(R.id.spinner_college);
        final ArrayList<String> colleges =new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,colleges);
        DatabaseReference collegesDR = FirebaseDatabase.getInstance().getReference().child("CollegeList");

        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        final String selection = sharedpref.getString(KEY_COLLEGE,"");

        collegesDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot temp = iterator.next();

                    //get name of the department
                    String department = temp.getKey().toString();
                    colleges.add(department);
                    //to reflect changes in the ui
                    arrayAdapter.notifyDataSetChanged();

                    if(department.equals(selection)){
                        collegeChoices.setSelection(arrayAdapter.getPosition(department));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //We will see this later
                Toast.makeText(SettingsActivity.this,"There has been some problem connecting to the server.",Toast.LENGTH_SHORT).show();
            }
        });
        collegeChoices.setAdapter(arrayAdapter);
        collegeChoices.setOnItemSelectedListener(collegeSelectedListener);


    }

    //whenever the selection changes update the shared preferences`
    AdapterView.OnItemSelectedListener collegeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String college = (String) parent.getItemAtPosition(position);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_COLLEGE,college);
            editor.commit();
            initDepartmentSpinner();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //whenever the selection changes update the shared preferences
    AdapterView.OnItemSelectedListener departmentSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String department = (String) parent.getItemAtPosition(position);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_DEPARTMENT,department);
            editor.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //Add Switches to Home Preferences
    ValueEventListener hpValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            SharedPreferences sharedPreferences = getSharedPreferences(fileName_HP,MODE_PRIVATE);
            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.homePreferences);
            while (iterator.hasNext()){
                DataSnapshot temp = iterator.next();
                Switch aSwitch = new Switch(getApplicationContext());
                if(!sharedPreferences.contains(temp.getKey())){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(temp.getKey().toString(),true);
                    editor.commit();
                }
                aSwitch.setChecked(sharedPreferences.getBoolean(temp.getKey().toString(),true));
                aSwitch.setText(temp.getKey());
                aSwitch.setTextColor(Color.BLACK);
                aSwitch.setOnCheckedChangeListener(hpOnCheckedChangeListener);
                linearLayout.addView(aSwitch);

            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(SettingsActivity.this,"There has been some problem connecting to the server.",Toast.LENGTH_SHORT).show();
        }
    };

    //add switches to SubmitEvent Preferences
    ValueEventListener epValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            SharedPreferences sharedPreferences = getSharedPreferences(fileName_EP,MODE_PRIVATE);
            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.eventPreferences);
            while (iterator.hasNext()){
                DataSnapshot temp = iterator.next();
                Switch aSwitch = new Switch(getApplicationContext());
                if(!sharedPreferences.contains(temp.getKey())){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(temp.getKey().toString(),true);
                    editor.commit();
                }
                aSwitch.setChecked(sharedPreferences.getBoolean(temp.getKey().toString(),true));
                aSwitch.setText(temp.getKey());
                aSwitch.setTextColor(Color.BLACK);
                aSwitch.setOnCheckedChangeListener(epOnCheckedChangeListener);
                linearLayout.addView(aSwitch);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this,"There has been some problem connecting to the server.",Toast.LENGTH_SHORT).show();
        }
    };

    //whenever the selection changes update the shared preferences
    CompoundButton.OnCheckedChangeListener hpOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences sharedPreferences = getSharedPreferences(fileName_HP,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(buttonView.getText().toString(),isChecked);
            editor.commit();
            //Home.setHomePreferencesChanged(true);
        }
    };


    //whenever the selection changes update the shared preferences
    CompoundButton.OnCheckedChangeListener epOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences sharedPreferences = getSharedPreferences(fileName_EP,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(buttonView.getText().toString(),isChecked);
            editor.commit();
            //SubmitEvent.setEventsPreferencesChanged(true);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(RELOAD_PARENT_ACTIVITY){
            RELOAD_PARENT_ACTIVITY=false;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

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
}
