package com.cocodev.TDUCManager;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cocodev.TDUCManager.Utility.User;
import com.cocodev.TDUCManager.articles.PendingArticles;
import com.cocodev.TDUCManager.articles.SubmittedArticles;
import com.cocodev.TDUCManager.events.PendingEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.leakcanary.RefWatcher;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String CollegeName;
    private final String KEY_CLEARENCE_LEVEL = "key_clearence_level";
    private final int REQUEST_CODE_SETTINGS_ACTIVITY = 1001;
    String[] submenus = {"Articles","PostNotice","SubmitEvent"};
    private String CURRENT_FRAGMENT = "currentFragment";
    private final int HOME_FRAGMENT =0;
    private final int NOTICE_BOARD_FRAGMENT =1;
    private Menu menu;


    public static RefWatcher getRefWatcher(Context context) {
        MainActivity application = (MainActivity) context.getApplicationContext();
        return application.refWatcher;
    }


    private RefWatcher refWatcher;
    public static final String TAG = "check";
    DatabaseReference databaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static User currentUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCollegeName();
        // Normal app init code...
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mProgressDialog = new ProgressDialog(this);


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        if(currentUser.getClearenceLevel()>=10){
            populateMenu();
        }

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_layout)==null){
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_layout,
                    new SubmittedArticles()
            ).commit();
            navigationView.setCheckedItem(R.id.home);
        }

    }

    private void populateMenu() {
        MenuItem pendingArticles = menu.add("Pending Articles");
        pendingArticles.setOnMenuItemClickListener(menuItemClickListener);
        pendingArticles.setCheckable(true);
        MenuItem pendingEvent = menu.add("Pending Events");
        pendingEvent.setOnMenuItemClickListener(menuItemClickListener);
        pendingEvent.setCheckable(true);


    }

    private void setCollegeName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        CollegeName = sharedPreferences.getString(SA.KEY_COLLEGE,"");
    }

    private MenuItem.OnMenuItemClickListener menuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getTitle().equals("Pending Articles")) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_layout,
                        new PendingArticles()
                ).commit();

            }
            if(item.getTitle().equals("Pending Events")) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_layout,
                        new PendingEvents()
                ).commit();

            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent;
        switch(id){
            case R.id.action_settings:
                intent = new Intent(this,SA.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                return true;
            case R.id.action_logout:
                mProgressDialog.show();
                currentUser=null;
                mFirebaseUser=null;
                mFirebaseAuth.signOut();
                intent = new Intent(this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mProgressDialog.hide();
                finish();
                startActivity(intent);
                return true;
            default:
                return true;
        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
            SubmittedArticles submittedArticles = new SubmittedArticles();
            Bundle bundle = new Bundle();
            getSupportActionBar().setTitle("Home");
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_layout,submittedArticles,null).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }
}
