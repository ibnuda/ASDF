package com.parametris.iteng.asdf.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parametris.iteng.asdf.R;

public class AmmunitionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AmmunitionFragment.class.getName();
    protected Spinner ammunitionSpinner;
    protected Spinner healthSpinner;
    protected ArrayAdapter<CharSequence> ammunitionAdapter;
    protected ArrayAdapter<CharSequence> healthAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ammunition, container, false);

        ammunitionAdapter = ArrayAdapter.createFromResource(getContext()
                , R.array.soulja_conditions
                , R.layout.support_simple_spinner_dropdown_item);
        healthAdapter = ArrayAdapter.createFromResource(getContext()
                , R.array.soulja_conditions
                , R.layout.support_simple_spinner_dropdown_item);

        ammunitionSpinner = (Spinner) view.findViewById(R.id.spinner_ammunition);
        ammunitionSpinner.setAdapter(ammunitionAdapter);

        healthSpinner = (Spinner) view.findViewById(R.id.spinner_health);
        healthSpinner.setAdapter(healthAdapter);

        int spinnerAmmunitionPosition = uYeah(ammunitionAdapter, "ammunition");
        int spinnerHealthPosition = uYeah(healthAdapter, "health");

        ammunitionSpinner.setSelection(spinnerAmmunitionPosition);
        ammunitionSpinner.setOnItemSelectedListener(this);

        healthSpinner.setSelection(spinnerHealthPosition);
        healthSpinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("asdf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Spinner spinner = (Spinner) parent;
        String what = "anything_goes_here";

        switch (spinner.getId()) {
            case R.id.spinner_ammunition:
                what = "ammunition";
                break;
            case R.id.spinner_health:
                what = "health";
                break;
        }
        Log.d(TAG, "onItemSelected: what :" + what);
        editor.putInt(what, Integer.valueOf(parent.getItemAtPosition(position).toString()));
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
