package com.chromastone.donatesmile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chromastonetech.librarymodule.Constants;
import com.chromastonetech.librarymodule.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView profilePic;
    TextView profileName;
    TextView profileEmail;
    FirebaseUser mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        profilePic = header.findViewById(R.id.drawer_profile_image);
        profileName = header.findViewById(R.id.drawer_profile_name);
        profileEmail = header.findViewById(R.id.drawer_profile_email);
        ImageView view = header.findViewById(R.id.drawer_profile_edit);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null){
            if (mAuth.getPhotoUrl() != null){
                Log.d("PP",mAuth.getPhotoUrl().toString());
                Picasso.with(MainActivity.this).load(mAuth.getPhotoUrl()).error(R.drawable.ic_account_circle_black_24dp).into(profilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        profilePic.setVisibility(View.VISIBLE);
                        Constants.makeImageCircular(MainActivity.this,profilePic);
                    }

                    @Override
                    public void onError() {
                        profilePic.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        if (mAuth.getDisplayName() != null){
            profileName.setText(mAuth.getDisplayName());
        }else{
            profileName.setVisibility(View.GONE);
        }
        if (mAuth.getEmail() != null){
            profileEmail.setText(mAuth.getEmail());
        }else{
            profileEmail.setVisibility(View.GONE);
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }else if (id == R.id.nav_profile){

        }else if (id == R.id.nav_sign_out){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
