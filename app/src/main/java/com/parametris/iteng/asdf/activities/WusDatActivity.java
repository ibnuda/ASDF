package com.parametris.iteng.asdf.activities;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.parametris.iteng.asdf.fragments.PickFileFragment;

import java.io.File;

public class WusDatActivity extends AbstractFilePickerActivity {

    public WusDatActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            @Nullable String startPath,
            int mode,
            boolean allowMultiple,
            boolean allowCreateDir,
            boolean allowExistingFile,
            boolean singleClick) {

        AbstractFilePickerFragment<File> fragment = new PickFileFragment();
        fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
        return fragment;
    }

        /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wus_dat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
        */

}
