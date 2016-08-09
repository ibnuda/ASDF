package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parametris.iteng.asdf.R;

public class HealthFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static int seekValue = 10;
    TextView textViewCondition;
    TextView textViewAmmunition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);
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
        return view;
    }

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
}
