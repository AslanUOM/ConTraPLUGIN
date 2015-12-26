package com.aslan.contra.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aslan.contra.R;
import com.aslan.contra.services.ActivityRecognitionService;
import com.aslan.contra.services.EnvironmentMonitorService;
import com.aslan.contra.services.LocationTrackingService;
import com.aslan.contra.services.NearbyTerminalTrackingService;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.RunningServices;
import com.aslan.contra.util.Utility;
import com.aslan.contra.view.fragment.HomeFragment;
import com.aslan.contra.view.fragment.OnFragmentInteractionListener;
import com.aslan.contra.view.fragment.PermissionFragment;
import com.aslan.contra.view.fragment.ProfileFragment;
import com.aslan.contra.view.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private final HomeFragment HOME_FRAGMENT = new HomeFragment();

    private final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();

    // UI components
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private boolean showProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Set the default fragment
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            int command = extras.getInt(Constants.COMMAND);
            showProfile = command == Constants.SHOW_PROFILE;
        }

        if (showProfile) {
            changeFragment(new ProfileFragment());
            // Start sensors for the first time run.
            startSensors();
        } else {
            boolean nonGrantedPermissionsExists = checkNonGrantedPermissions();
            if (!nonGrantedPermissionsExists) {
                changeFragment(HOME_FRAGMENT);
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Start the sensors to collect the data.
     */
    private void startSensors() {
        //TODO comment when you don't want to auto start the tracking service at app start
        if (!RunningServices.getInstance().isLocationServiceRunning(getApplicationContext())) {
            Intent serviceIntent = new Intent(MainActivity.this, LocationTrackingService.class);
            serviceIntent.addCategory(LocationTrackingService.TAG);
            startService(serviceIntent);
        }
        if (!RunningServices.getInstance().isActivityRecognitionServiceRunning(getApplicationContext())) {
            Intent serviceIntent = new Intent(MainActivity.this, ActivityRecognitionService.class);
            serviceIntent.addCategory(ActivityRecognitionService.TAG);
            startService(serviceIntent);
        }
        if (!RunningServices.getInstance().isEnvironmentMonitorServiceRunning(getApplicationContext())) {
            Intent serviceIntent = new Intent(MainActivity.this, EnvironmentMonitorService.class);
            serviceIntent.addCategory(EnvironmentMonitorService.TAG);
            startService(serviceIntent);
        }
        if (!RunningServices.getInstance().isNearbyTerminalTrackingServiceRunning(getApplicationContext())) {
            Intent serviceIntent = new Intent(MainActivity.this, NearbyTerminalTrackingService.class);
            serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
            startService(serviceIntent);
        }
    }


    /**
     * Check for the non granted permissions and if there are any, move to the PermissionFramgment.
     *
     * @return true if there is atleast a non granted permission, false if all the permissions are granted.
     */
    private boolean checkNonGrantedPermissions() {
        String[] nonGrantedPermissions = Utility.nonGrantedPermissions(getApplicationContext());

        if (nonGrantedPermissions.length > 0) {
            // There are some non granted permissions

            // Disable user from moving to any other fragments without providing permissions
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);

            // Show permission fragment
            changeFragment(new PermissionFragment());

            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;

        if (id == R.id.nav_permission) {
            fragment = new PermissionFragment();
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_settings) {
            fragment = SETTINGS_FRAGMENT;
        } else if (id == R.id.nav_sign_out) {
            Utility.saveUserId(getApplicationContext(), null);
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            return true;
        } else {
            fragment = HOME_FRAGMENT;
        }

        changeFragment(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Change the active fragment to the given one.
     *
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }


    @Override
    public void onFragmentInteraction(Fragment fragment, String command) {
        if (fragment instanceof PermissionFragment) {
            if (Constants.ALL_PERMISSIONS_GRANTED.equals(command)) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                toggle.setDrawerIndicatorEnabled(true);
                startSensors();
            }
        } else if (fragment instanceof ProfileFragment) {
            if (showProfile) {  // Only for the first time it will be true
                showProfile = false;
                checkNonGrantedPermissions();
            }
        }
    }
}
