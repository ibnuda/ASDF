package com.parametris.iteng.asdf.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.parametris.iteng.asdf.R

class AmmunitionFragment : Fragment(), AdapterView.OnItemSelectedListener {

    protected var spinner: Spinner? = null
    protected var mAdapter: ArrayAdapter<CharSequence>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_ammunition, container, false)

        mAdapter = ArrayAdapter.createFromResource(context, R.array.soulja_conditions, R.layout.support_simple_spinner_dropdown_item)
        spinner = view.findViewById(R.id.spinner_ammunition) as Spinner
        spinner!!.adapter = mAdapter

        val spinnerPosition = uYeah(mAdapter!!, "ammunition")

        spinner!!.setSelection(spinnerPosition)

        spinner!!.onItemSelectedListener = this

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val sharedPreferences = activity.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("ammunition", Integer.valueOf(parent.getItemAtPosition(position).toString())!!)
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
}
