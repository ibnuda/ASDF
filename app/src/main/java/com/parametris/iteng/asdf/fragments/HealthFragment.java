package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parametris.iteng.asdf.R;

public class HealthFragment extends Fragment implements AdapterView.OnItemSelectedListener { // implements SeekBar.OnSeekBarChangeListener {

    Spinner spinner;
    ArrayAdapter<CharSequence> mAdapter;
    private static int seekValue = 10;
    TextView textViewCondition;
    TextView textViewAmmunition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        final SeekBar seekBarCondition = (SeekBar) view.findViewById(R.id.seek_bar_condition);
        final SeekBar seekBarAmmunition = (SeekBar) view.findViewById(R.id.seek_bar_ammunition);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("asdf", Context.MODE_PRIVATE);
        int cond = sharedPreferences.getInt("condition", 100);
        int ammo = sharedPreferences.getInt("ammunition", 100);
        seekBarCondition.setProgress(cond);
        seekBarAmmunition.setProgress(ammo);

        textViewCondition = (TextView) view.findViewById(R.id.text_view_condition);
        textViewAmmunition = (TextView) view.findViewById(R.id.text_view_ammunition);
        textViewCondition.setText(String.valueOf(cond));
        textViewAmmunition.setText(String.valueOf(ammo));

        seekBarCondition.setOnSeekBarChangeListener(this);
        seekBarAmmunition.setOnSeekBarChangeListener(this);
        */
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        mAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.soulja_conditions,
                R.layout.support_simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.spinner_health);
        spinner.setAdapter(mAdapter);

        int spinnerPosition = uYeah(mAdapter, "health");

        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("asdf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("health", Integer.valueOf(parent.getItemAtPosition(position).toString()));
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
    /*
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("asdf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        seekValue = progress;
        switch (seekBar.getId()) {
            case R.id.seek_bar_condition:
                textViewCondition.setText(String.valueOf(seekValue));
                editor.putInt("condition", seekValue);
                break;
            case R.id.seek_bar_ammunition:
                textViewAmmunition.setText(String.valueOf(seekValue));
                editor.putInt("ammunition", seekValue);
                break;
            default:
                break;
        }
        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    */
}
