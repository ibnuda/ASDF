package com.parametris.iteng.asdf.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView

import com.parametris.iteng.asdf.R

class HealthFragment : Fragment(), AdapterView.OnItemSelectedListener { // implements SeekBar.OnSeekBarChangeListener {

    internal var spinner: Spinner? = null
    internal var mAdapter: ArrayAdapter<CharSequence>? = null
    internal var textViewCondition: TextView? = null
    internal var textViewAmmunition: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        val view = inflater!!.inflate(R.layout.fragment_health, container, false)
        mAdapter = ArrayAdapter.createFromResource(context,
                R.array.soulja_conditions,
                R.layout.support_simple_spinner_dropdown_item)
        spinner = view.findViewById(R.id.spinner_health) as Spinner
        spinner!!.adapter = mAdapter

        val spinnerPosition = uYeah(mAdapter!!, "health")

        spinner!!.setSelection(spinnerPosition)

        spinner!!.onItemSelectedListener = this
        return view
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val sharedPreferences = activity.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("health", Integer.valueOf(parent.getItemAtPosition(position).toString())!!)
        editor.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    fun uYeah(mAdapter: ArrayAdapter<CharSequence>, apa: String): Int {
        val sharedPreferences = activity.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        var ammunition = sharedPreferences.getInt(apa, 100)
        val mod = ammunition % 20
        ammunition -= mod

        return mAdapter.getPosition(ammunition.toString())
    }

    companion object {
        private val seekValue = 10
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
