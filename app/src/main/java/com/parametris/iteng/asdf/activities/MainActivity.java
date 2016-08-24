package com.parametris.iteng.asdf.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.fragments.AmmunitionFragment;
import com.parametris.iteng.asdf.fragments.ChatFragment;
import com.parametris.iteng.asdf.fragments.HealthFragment;
import com.parametris.iteng.asdf.fragments.MyMapFragment;
import com.parametris.iteng.asdf.fragments.PickFileFragment;
import com.parametris.iteng.asdf.models.Utils;
import com.parametris.iteng.asdf.track.LokAlarmReceiver;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView nvDrawer;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle drawerToggle;

    boolean trackingNow;
    AlarmManager alarmManager;
    Intent trackIntent;
    PendingIntent pendingIntent;

    Realm realm;
    RealmConfiguration realmConfiguration;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = setupDrawerToggle();

        dlDrawer.setDrawerListener(drawerToggle);

        nvDrawer.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new MyMapFragment()).commit();

        SharedPreferences sharedPreferences = this.getSharedPreferences("asdf", MODE_PRIVATE);
        trackingNow = sharedPreferences.getBoolean("trackingNow", false);
        boolean firstTime = sharedPreferences.getBoolean("firstTime", true);

        if (firstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.putString("username", "TODO");
            editor.apply();
        }
        trackLocation();
    }

    private void trackLocation() {
        if (!googlePlayEnabled()) return;
        startAlarmManager();
    }

    private void startAlarmManager() {
        Context context = getBaseContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        trackIntent = new Intent(context, LokAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, trackIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 3000, pendingIntent);
    }

    private boolean googlePlayEnabled() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home_map:
                fragmentClass = MyMapFragment.class;
                break;
            case R.id.nav_condition_health:
                fragmentClass = HealthFragment.class;
                break;
            case R.id.nav_condition_ammunition:
                fragmentClass = AmmunitionFragment.class;
                break;
            case R.id.nav_communication_chat:
                fragmentClass = ChatFragment.class;
                break;
            case R.id.nav_communicator_send_file:
                // fragmentClass = SendFileFragment.class;
                startActivity(new Intent(this, WusDat.class));
                return;
                // fragmentClass = PickFileFragment.class;
                // break;
            /*
            case R.id.nav_condition:
                fragmentClass = HealthFragment.class;
                break;
            */
            default:
                fragmentClass = MyMapFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        dlDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this,
                dlDrawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
