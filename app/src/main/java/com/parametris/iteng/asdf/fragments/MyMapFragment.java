package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parametris.iteng.asdf.R;


public class MyMapFragment extends Fragment implements OnMapReadyCallback {
    String TAG = MyMapFragment.class.getName();
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
        // mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        textViewHereIAm = (TextView) view.findViewById(R.id.text_view_here_i_am);

        float lat = sharedPreferences.getFloat("prevLat", 0.0f);
        float lon = sharedPreferences.getFloat("prevLong", 0.0f);

        LatLng here = new LatLng(lat, lon);
        textViewHereIAm.setText(here.toString());

        initializeMap();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (googleMap != null) {
            Log.d(TAG, "onMapReady: googleMap is not null.");
        }
        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        if (googleMap != null) {
            googleMap.getUiSettings().setCompassEnabled(true);
        }
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        float lat = sharedPreferences.getFloat("prevLat", 0.0f);
        float lon = sharedPreferences.getFloat("prevLong", 0.0f);
        LatLng here = new LatLng(lat, lon);
        Log.d(TAG, "onMapReady: here : " + here.toString());
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 12);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(here).zoom(12).build();
        Log.d(TAG, "onMapReady: cameraUpdate : " + cameraUpdate);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(here).draggable(true));
        Log.d(TAG, "onMapReady: marker : " + marker.getTitle());
        Log.d(TAG, "onMapReady: marker : " + marker.getPosition());
        marker.setVisible(true);
        // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.moveCamera(cameraUpdate);
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
