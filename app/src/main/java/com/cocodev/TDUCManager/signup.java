package com.cocodev.TDUCManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import customfonts.MyTextView;

public class signup extends AppCompatActivity
{
    ImageView sback;
    EditText fname;
    EditText email;
    EditText password;
    MyTextView submit;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        sback = (ImageView)findViewById(R.id.sback);
        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               onBackPressed();
            }
        });

        fname = (EditText) findViewById(R.id.fname);
        email = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.pswrd);
        submit = (MyTextView) findViewById(R.id.signup_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        updateUI(mAuth.getCurrentUser());




                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(signup.this,task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }

                                    // ...
                                }
                            });

                }
            }
        });

    }

    private void updateUI(final FirebaseUser user) {
        if(user==null)
            return;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    MainActivity.currentUser = new User(user.getUid(),10,fname.getText().toString());
                    databaseReference.setValue(MainActivity.currentUser);
                }else{
                    MainActivity.currentUser = dataSnapshot.getValue(User.class);
                }
                startActivity(new Intent(signup.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
            }
        });
    }

    private boolean checkFields() {
        if(TextUtils.isEmpty(fname.getText())){
            fname.setError("This Field Can Not Be Empty");
            return false;
        }
        if(TextUtils.isEmpty(email.getText())){
            email.setError("This Field Can Not Be Empty");
            return false;
        }
        if(TextUtils.isEmpty(password.getText())){
            password.setError("This Field Can Not Be Empty");
            return false;
        }

        return true;
    }
}
