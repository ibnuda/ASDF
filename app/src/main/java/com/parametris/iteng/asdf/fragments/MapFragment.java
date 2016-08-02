package com.parametris.iteng.asdf.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parametris.iteng.asdf.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap myGoogleMap;
    MapView mapView;
    boolean mapSupported;
    LocationManager locationManager;
    Location here;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
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
        initializeMap();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        here = locationManager.getLastKnownLocation(bestProvider);

        LatLng currentPosition = new LatLng(here.getLatitude(), here.getLongitude());

        myGoogleMap = googleMap;
        myGoogleMap.addMarker(new MarkerOptions().position(currentPosition).title("Position at the moment"));
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
    }

}
