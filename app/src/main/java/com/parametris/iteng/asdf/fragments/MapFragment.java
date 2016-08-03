package com.parametris.iteng.asdf.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.parametris.iteng.asdf.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    String TAG = "MapFragment";
    GoogleMap myGoogleMap;
    MapView mapView;
    boolean mapSupported;
    LocationManager locationManager;
    Location here;
    TextView textViewHereIAm;
    CameraUpdate cameraUpdate;

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
        if (myGoogleMap == null && mapSupported) {
            mapView = (MapView) getActivity().findViewById(R.id.map_view);
            mapView.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        textViewHereIAm = (TextView) view.findViewById(R.id.text_view_here_i_am);

        // Here be dragons.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("asdf", Context.MODE_PRIVATE);

        float lat = sharedPreferences.getFloat("prevLat", 0.0f);
        float lon = sharedPreferences.getFloat("prevLong", 0.0f);
        Log.d(TAG, "onCreateView: lat : " + Float.toString(lat) + ", lon : " + Float.toString(lon));
        LatLng here = new LatLng(lat, lon);
        textViewHereIAm.setText(here.toString());
        initializeMap();
        cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(here, 11);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myGoogleMap = googleMap;
        myGoogleMap.animateCamera(cameraUpdate);
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
