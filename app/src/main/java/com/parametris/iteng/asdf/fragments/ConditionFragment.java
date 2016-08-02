package com.parametris.iteng.asdf.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parametris.iteng.asdf.R;

public class ConditionFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static int seekValue = 10;
    TextView textViewCondition;
    TextView textViewAmmunition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_condition, container, false);
        textViewCondition = (TextView) view.findViewById(R.id.text_view_condition);
        textViewAmmunition = (TextView) view.findViewById(R.id.text_view_ammunition);
        final SeekBar seekBarCondition = (SeekBar) view.findViewById(R.id.seek_bar_condition);
        final SeekBar seekBarAmmunition = (SeekBar) view.findViewById(R.id.seek_bar_ammunition);
        seekBarCondition.setOnSeekBarChangeListener(this);
        seekBarAmmunition.setOnSeekBarChangeListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekValue = progress;
        switch (seekBar.getId()) {
            case R.id.seek_bar_condition:
                textViewCondition.setText(String.valueOf(seekValue));
                break;
            case R.id.seek_bar_ammunition:
                textViewAmmunition.setText(String.valueOf(seekValue));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
