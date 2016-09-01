package com.parametris.iteng.asdf.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.fragment.AmmunitionFragment;
import com.parametris.iteng.asdf.fragment.ChatFragment;
import com.parametris.iteng.asdf.fragment.MyMapFragment;
import com.parametris.iteng.asdf.model.Utils;
import com.parametris.iteng.asdf.track.LokAlarmReceiver;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity implements UploadStatusDelegate, View.OnClickListener {
    public static final String TAG = "MainActivity";
    Toolbar toolbar;
    NavigationView nvDrawer;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle drawerToggle;
    LinearLayout linearLayout;

    boolean trackingNow;
    AlarmManager alarmManager;
    Intent trackIntent;
    PendingIntent pendingIntent;
    SharedPreferences sharedPreferences;

    Button buttonChat
            , buttonSend
            , buttonMap
            , buttonKondisi;

    Realm realm;
    RealmConfiguration realmConfiguration;
    Utils utils;

    Map<String, UploadProgressViewHolder> uploadProgressViewHolderMap = new HashMap<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.container);

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

        sharedPreferences = this.getSharedPreferences("asdf", MODE_PRIVATE);
        trackingNow = sharedPreferences.getBoolean("trackingNow", false);
        boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("imei", getDeviceId());

        if (firstTime) {
            editor.putBoolean("firstTime", false);
            editor.putString("username", "TODO");
        }
        editor.apply();
        trackLocation();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Button bawah.
        buttonChat = (Button) findViewById(R.id.button_chat);
        buttonSend = (Button) findViewById(R.id.button_send);
        buttonMap = (Button) findViewById(R.id.button_map);
        buttonKondisi = (Button) findViewById(R.id.button_kondisi);

        buttonChat.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        buttonMap.setOnClickListener(this);
        buttonKondisi.setOnClickListener(this);
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
        switch (menuItem.getItemId()) {
            case R.id.nav_home_map:
                fragmentClass = MyMapFragment.class;
                break;
            case R.id.nav_condition:
                fragmentClass = AmmunitionFragment.class;
                break;
            case R.id.nav_communication_chat:
                fragmentClass = ChatFragment.class;
                break;
            case R.id.nav_communicator_send_file:
                Intent pickFileIntent = new Intent(MainActivity.this, WusDatActivity.class);
                startActivityForResult(pickFileIntent, 0);
                return;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + data.toString());
            Uri lokasi = data.getData();
            File anuLah = new File(lokasi.getPath());
            // toolbar.setTitle(data.getData().toString());
            // uploadTheFile(data.getData().toString());
            toolbar.setTitle(anuLah.toString());
            uploadTheFile(anuLah.toString());
        } else {
            toolbar.setTitle("kosong");
        }
    }

    private UploadNotificationConfig getNotificationConfig(String name) {
        return new UploadNotificationConfig()
                .setIcon(R.drawable.ic_pause_dark)
                .setCompletedIcon(R.drawable.ic_play_dark)
                .setErrorIcon(R.drawable.quantum_ic_stop_grey600_36)
                .setTitle(name)
                .setInProgressMessage("Uploading")
                .setCompletedMessage("Uploaded")
                .setErrorMessage("Ada masalah, um.")
                .setClickIntent(new Intent(this, MainActivity.class))
                .setRingToneEnabled(true);
    }

    private void uploadTheFile(String filename) {
        // TODO: 8/26/2016 tambah server untuk upload
        final String server = "http://192.168.1.104/~ibnu/haro.php";
        Log.d(TAG, "uploadTheFile: filename : " + filename);
        final String nameOfFile = getNameOfFile(filename);
        Log.d(TAG, "uploadTheFile: nameOfFile : " + nameOfFile);
        String newPath = prepareFile(filename);
        Log.d(TAG, "uploadTheFile: newPath : " + newPath);
        try {
            MultipartUploadRequest request = new MultipartUploadRequest(this, server)
                    .addFileToUpload(newPath, nameOfFile)
                    .setNotificationConfig(getNotificationConfig(nameOfFile))
                    .setMaxRetries(4);
            String uploadID = request.setDelegate(this).startUpload();
            addUploadToList(uploadID, nameOfFile);
        } catch (FileNotFoundException | MalformedURLException e) {
            Log.e(TAG, "uploadTheFile: unable to upload.", e);
        }

        FileUtils.deleteQuietly(new File(newPath));
    }

    private String prepareFile(String pathFile) {
        // Get the "real name" of the file.
        String imei = getDeviceId();
        String username = sharedPreferences.getString("username", "seharusnya_username");
        String oldName = getNameOfFile(pathFile);
        String newName = imei + '_' + username + '_' + oldName;
        String newPathFile = pathFile.replaceAll(oldName, newName);

        // Copy the file;
        try {
            FileUtils.copyFile(new File(pathFile), new File(newPathFile));
        } catch (IOException e) {
            newPathFile = pathFile;
        }
        return newPathFile;
    }

    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private void addUploadToList(String uploadID, String nameOfFile) {
        View uploadProgress = getLayoutInflater().inflate(R.layout.upload_progress, null);
        UploadProgressViewHolder uploadProgressViewHolder = new UploadProgressViewHolder(uploadProgress, nameOfFile);
        uploadProgressViewHolder.uploadId = uploadID;
        linearLayout.addView(uploadProgressViewHolder.itemView, 0);
        uploadProgressViewHolderMap.put(uploadID, uploadProgressViewHolder);
    }

    private String getNameOfFile(String filename) {
        if (null == filename) {
            return null;
        }
        final String[] parts = filename.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {
        if (null == uploadProgressViewHolderMap.get(uploadInfo.getUploadId())) {
            return;
        }
        uploadProgressViewHolderMap
                .get(uploadInfo.getUploadId())
                .progressBar.setProgress(uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(UploadInfo uploadInfo, Exception exception) {
        if (null == uploadProgressViewHolderMap.get(uploadInfo.getUploadId())) {
            return;
        }

        linearLayout.removeView(uploadProgressViewHolderMap.get(uploadInfo.getUploadId()).itemView);
        uploadProgressViewHolderMap.remove(uploadInfo.getUploadId());
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
        linearLayout.removeView(uploadProgressViewHolderMap.get(uploadInfo.getUploadId()).itemView);
        uploadProgressViewHolderMap.remove(uploadInfo.getUploadId());
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {
        if (null == uploadProgressViewHolderMap.get(uploadInfo.getUploadId())) {
            return;
        }

        linearLayout.removeView(uploadProgressViewHolderMap.get(uploadInfo.getUploadId()).itemView);
        uploadProgressViewHolderMap.remove(uploadInfo.getUploadId());
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (v.getId()) {
            case R.id.button_chat:
                fragmentClass = ChatFragment.class;
                break;
            case R.id.button_send:
                Intent pickFileIntent = new Intent(MainActivity.this, WusDatActivity.class);
                startActivityForResult(pickFileIntent, 0);
                return;
            case R.id.button_map:
                fragmentClass = MyMapFragment.class;
                break;
            case R.id.button_kondisi:
                fragmentClass = AmmunitionFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .commit();
        } catch (InstantiationException | IllegalAccessException e) {
            Snackbar.make(v, "unable thingy whatever.", Snackbar.LENGTH_LONG).show();
        }
    }

    class UploadProgressViewHolder {
        View itemView;
        TextView textView;
        ProgressBar progressBar;
        Button button;
        String uploadId;

        UploadProgressViewHolder(View view, String filename) {
            itemView = view;
            textView = (TextView) itemView.findViewById(R.id.upload_title);
            progressBar = (ProgressBar) itemView.findViewById(R.id.upload_progress_bar);
            textView.setText(getString(R.string.upload_progress, filename));
            button = (Button) itemView.findViewById(R.id.cancel_upload_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == uploadId) {
                        return;
                    }

                    UploadService.stopUpload(uploadId);
                }
            });
        }


    }
}
