package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parametris.iteng.asdf.R;

public class AmmunitionFragment extends Fragment {

    Spinner spinner;
    String[] healthValues;
    ArrayAdapter<CharSequence> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ammunition, container, false);

        mAdapter = ArrayAdapter.createFromResource(getContext(), R.array.soulja_conditions, R.layout.support_simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.spinner_ammunition);
        spinner.setAdapter(mAdapter);
        return view;
    }
}
