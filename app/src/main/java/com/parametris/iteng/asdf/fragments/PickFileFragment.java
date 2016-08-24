package com.parametris.iteng.asdf.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parametris.iteng.asdf.R;

public class PickFileFragment extends Fragment {

    Button buttonPickFile;
    Button buttonSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_file, container, false);
        buttonPickFile = (Button) view.findViewById(R.id.button_pick_file);
        buttonSend = (Button) view.findViewById(R.id.button_send);
        return view;
    }
}
