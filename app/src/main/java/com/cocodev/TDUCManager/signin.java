package com.cocodev.TDUCManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
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

import customfonts.MyEditText;
import customfonts.MyTextView;

public class signin extends AppCompatActivity {

    ImageView sback;
    MyEditText email;
    MyEditText password;
    MyTextView signIn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");

        sback = (ImageView)findViewById(R.id.sinb);
        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        email = (MyEditText) findViewById(R.id.signin_username);
        password = (MyEditText) findViewById(R.id.signin_password);

        signIn = (MyTextView) findViewById(R.id.signup_submit);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    }else{
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(signin.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
                    MainActivity.currentUser = new User(user.getUid(),10,"");
                    databaseReference.setValue(MainActivity.currentUser);
                }else{
                    MainActivity.currentUser = dataSnapshot.getValue(User.class);
                }
                startActivity(new Intent(signin.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
            }
        });
    }

    private boolean checkFields() {
        if(TextUtils.isEmpty(email.getText())){
            email.setError("This Field Can Not Be Empty");
            return false;
        }
        if(TextUtils.isEmpty(password.getText())){
            email.setError("This Field Can Not Be Empty");
            return false;
        }

        return true;
    }


}
