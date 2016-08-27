package com.parametris.iteng.asdf.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parametris.iteng.asdf.R;

public class AmmunitionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    protected Spinner spinner;
    protected ArrayAdapter<CharSequence> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ammunition, container, false);

        mAdapter = ArrayAdapter.createFromResource(getContext()
                , R.array.soulja_conditions
                , R.layout.support_simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.spinner_ammunition);
        spinner.setAdapter(mAdapter);

        int spinnerPosition = uYeah(mAdapter, "ammunition");

        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("asdf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ammunition", Integer.valueOf(parent.getItemAtPosition(position).toString()));
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int uYeah(ArrayAdapter<CharSequence> mAdapter, String apa) {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("asdf", Context.MODE_PRIVATE);
        int ammunition = sharedPreferences.getInt(apa, 100);
        int mod = ammunition % 20;
        ammunition -= mod;

        return mAdapter.getPosition(String.valueOf(ammunition));
    }
}
