package com.cocodev.TDUCManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import customfonts.MyTextView;

/**
 * Created by TEC Staff on 11-05-2017.
 */
public class Login extends AppCompatActivity {
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView sin;
    LinearLayout circle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        circle = (LinearLayout)findViewById(R.id.circle);
        sin = (TextView)findViewById(R.id.signup_submit);
        MyTextView myTextView = (MyTextView) findViewById(R.id.signupforfree);
        myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle.callOnClick();
            }
        });
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(Login.this,signup.class);
                startActivity(it);

            }
        });
        sin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this,signin.class);
                startActivity(it);
            }
        });

        if (mAuth.getCurrentUser() != null) {
            // already signed in

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(Login.this, "dataSnapshot does not exist", Toast.LENGTH_SHORT).show();
                        MainActivity.currentUser = new User(mAuth.getCurrentUser().getUid(),0,"");
                        databaseReference.setValue(MainActivity.currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(Login.this,MainActivity.class));
                                finish();
                            }
                        });
                    }else{
                        MainActivity.currentUser = dataSnapshot.getValue(User.class);
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    finish();
                }
            });

        }
    }

}
