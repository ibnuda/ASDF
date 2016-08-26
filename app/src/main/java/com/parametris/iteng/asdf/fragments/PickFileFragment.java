package com.parametris.iteng.asdf.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nononsenseapps.filepicker.FilePickerFragment;
import com.parametris.iteng.asdf.R;

import java.io.File;

public class PickFileFragment extends FilePickerFragment {

    private static final int VIEW_CHECK = 11;
    private static final int VIEW_NONCH = 12;
    private static final String[] MULTIMEDIA_EXTENSION = new String[] {".png", ".jpg", ".gif", ".mp4"};

    protected boolean isMultimediaFile(File file) {
        if (isDir(file)) {
            return false;
        }

        String path = file.getPath().toLowerCase();
        for (String extension : MULTIMEDIA_EXTENSION) {
            if (path.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemViewType(int position, @NonNull File file) {
        if (isMultimediaFile(file)) {
            if (isCheckable(file)) {
                return VIEW_CHECK;
            } else {
                return VIEW_NONCH;
            }
        } else {
            return super.getItemViewType(position, file);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_CHECK:
                return new CheckableViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.list_item_banyak, parent, false));
            case VIEW_NONCH:
                return new DirViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.list_item, parent, false));
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DirViewHolder viewHolder, int position, @NonNull File file) {
        super.onBindViewHolder(viewHolder, position, file);
        final int viewType = getItemViewType(position, file);

        if (viewType == VIEW_CHECK || viewType == VIEW_NONCH) {
            viewHolder.icon.setVisibility(View.VISIBLE);
            Glide.with(this).load(file).into((ImageView) viewHolder.icon);
        }
    }
}
