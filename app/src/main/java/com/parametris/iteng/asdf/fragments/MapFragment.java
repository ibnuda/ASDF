package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.parametris.iteng.asdf.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    String TAG = "MapFragment";
    GoogleMap googleMap;
    MapView mapView;
    boolean mapSupported;
    TextView textViewHereIAm;
    CameraUpdate cameraUpdate;
    SharedPreferences sharedPreferences;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            mapSupported = false;
        }

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        initializeMap();
    }

    private void initializeMap() {
        if (googleMap == null && mapSupported) {
            mapView = (MapView) getActivity().findViewById(R.id.map_view);
            mapView.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        sharedPreferences = getActivity().getSharedPreferences("asdf", Context.MODE_PRIVATE);
        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        textViewHereIAm = (TextView) view.findViewById(R.id.text_view_here_i_am);

        float lat = sharedPreferences.getFloat("prevLat", 0.0f);
        float lon = sharedPreferences.getFloat("prevLong", 0.0f);

        LatLng here = new LatLng(lat, lon);
        textViewHereIAm.setText(here.toString());

        initializeMap();
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 12);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
