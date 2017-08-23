package com.cocodev.TDUCManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cocodev.TDUCManager.Utility.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
/**
 * Created by TEC Staff on 11-05-2017.
 */
public class Login extends AppCompatActivity {
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mAuth.getCurrentUser() != null) {
            // already signed in
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        MainActivity.currentUser = new User(mAuth.getCurrentUser().getUid(),10);
                        databaseReference.setValue(MainActivity.currentUser);
                    }else{
                        MainActivity.currentUser = dataSnapshot.getValue(User.class);
                    }
                    startActivity(new Intent(Login.this,MainActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    finish();
                }
            });

        } else {
            // not signed in
            Intent intent =  AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setProviders(Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                    ))
                    .setIsSmartLockEnabled(false)
                    .build();
             startActivityForResult(
                    intent,
                    RC_SIGN_IN);

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                String Uid = mAuth.getCurrentUser().getUid();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            MainActivity.currentUser = new User(mAuth.getCurrentUser().getUid(),0);
                            databaseReference.setValue(MainActivity.currentUser);
                        }else{
                            MainActivity.currentUser = dataSnapshot.getValue(User.class);
                        }
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                            finish();
                    }
                });

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    finish();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }
}
