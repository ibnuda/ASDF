package com.parametris.iteng.asdf.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.track.LokService;

import java.util.ArrayList;
import java.util.List;


public class MyMapFragment extends Fragment implements OnMapReadyCallback {
    String TAG = MyMapFragment.class.getName();
    GoogleMap googleMap;
    MapView mapView;
    boolean mapSupported;
    public TextView textViewHereIAm;
    CameraUpdate cameraUpdate;
    SharedPreferences sharedPreferences;
    Marker marker;

    List<LatLng> routePoints = new ArrayList<LatLng>();
    Polyline route;

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

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (marker != null) {
                            marker.remove();
                        }
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lon = intent.getDoubleExtra("lon", 0);
                        LatLng here = new LatLng(lat, lon);

                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(here).title("Here I am."));
                        marker.setVisible(true);
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 15);
                        googleMap.animateCamera(cameraUpdate);
                        googleMap.moveCamera(cameraUpdate);
                        if (route != null) {
                            route.remove();
                        }
                        routePoints.add(here);
                        route = googleMap.addPolyline(new PolylineOptions()
                                .width(2)
                                .color(Color.BLACK)
                                .geodesic(true));
                        route.setPoints(routePoints);
                        textViewHereIAm.setText(here.toString());
                    }
                }, new IntentFilter(LokService.YUHU)
        );
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
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
        }
        float lat = sharedPreferences.getFloat("prevLat", 0.0f);
        float lon = sharedPreferences.getFloat("prevLong", 0.0f);
        LatLng here = new LatLng(lat, lon);
        routePoints.add(here);
        Log.d(TAG, "onMapReady: here : " + here.toString());
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 15);

        // these two lines shows the marker.
        marker = googleMap.addMarker(new MarkerOptions()
                .position(here)
                .title("Here I am.")
                .draggable(true));
        marker.setVisible(true);

        // while these ones shows the tracks.
        route = googleMap.addPolyline(new PolylineOptions().width(2).color(Color.BLACK).geodesic(true));

        route.setPoints(routePoints);

        googleMap.animateCamera(cameraUpdate);
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

    /*
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: location changed.");
        LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(here).title("Here I am."));
        marker.setVisible(true);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 15);
        googleMap.animateCamera(cameraUpdate);
        googleMap.moveCamera(cameraUpdate);
        textViewHereIAm.setText(here.toString());
    }
    */
}
