package com.parametris.iteng.asdf.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.parametris.iteng.asdf.R
import com.parametris.iteng.asdf.models.LatLangAtTheTime
import com.parametris.iteng.asdf.models.Utils
import com.parametris.iteng.asdf.track.LokService

import java.util.ArrayList

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults


class MyMapFragment : Fragment(), OnMapReadyCallback {
    internal var TAG = MyMapFragment::class.java.name
    internal var googleMap: GoogleMap? = null
    internal var mapView: MapView? = null
    internal var mapSupported: Boolean = false
    var textViewHereIAm: TextView? = null
    internal var cameraUpdate: CameraUpdate? = null
    internal var sharedPreferences: SharedPreferences? = null
    internal var marker: Marker? = null

    internal var routePoints: MutableList<LatLng> = ArrayList()
    internal var route: Polyline? = null

    // TODO : Save the track history.
    // STILL HAVEN'T SAVED YET!!!
    internal var realm: Realm? = null
    internal var utils: Utils? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(this.activity)
        } catch (e: Exception) {
            mapSupported = false
        }

        if (mapView != null) {
            mapView!!.onCreate(savedInstanceState)
        }
        initializeMap()
    }

    private fun initializeMap() {
        if (googleMap == null && mapSupported) {
            mapView = activity.findViewById(R.id.map_view) as MapView
            mapView!!.getMapAsync(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_map, container, false)
        sharedPreferences = activity.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        mapView = view.findViewById(R.id.map_view) as MapView
        // mapView.onCreate(savedInstanceState);
        mapView!!.getMapAsync(this)
        textViewHereIAm = view.findViewById(R.id.text_view_here_i_am) as TextView

        realm = Realm.getDefaultInstance()
        utils = Utils()

        val lat = sharedPreferences!!.getFloat("prevLat", 0.0f)
        val lon = sharedPreferences!!.getFloat("prevLong", 0.0f)

        val here = LatLng(lat.toDouble(), lon.toDouble())
        textViewHereIAm!!.text = here.toString()

        initializeMap()

        if (routePoints.isEmpty()) {
            routePoints = getAllRoutePoints(realm!!)
        }

        // TODO : Refactor this
        LocalBroadcastManager.getInstance(context).registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (marker != null) {
                            marker!!.remove()
                        }
                        val lat = intent.getDoubleExtra("lat", 0.0)
                        val lon = intent.getDoubleExtra("lon", 0.0)
                        val here = LatLng(lat, lon)

                        marker = googleMap!!.addMarker(MarkerOptions().position(here).title("Here I am."))
                        marker!!.isVisible = true
                        cameraUpdate = CameraUpdateFactory.newLatLng(here)
                        googleMap!!.animateCamera(cameraUpdate)
                        googleMap!!.moveCamera(cameraUpdate)
                        if (route != null) {
                            route!!.remove()
                        }
                        addRoutePoint(here)
                        routePoints.add(here)
                        route = googleMap!!.addPolyline(PolylineOptions().width(10f).color(Color.BLACK).geodesic(true))
                        route!!.points = routePoints
                        textViewHereIAm!!.text = here.toString()
                    }
                }, IntentFilter(LokService.YUHU))
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (googleMap != null) {
            Log.d(TAG, "onMapReady: googleMap is not null.")
        }
        if (googleMap != null) {
            googleMap!!.uiSettings.isMyLocationButtonEnabled = true
            googleMap!!.uiSettings.isZoomControlsEnabled = true
            googleMap!!.uiSettings.isCompassEnabled = true
        }
        val lat = sharedPreferences!!.getFloat("prevLat", 0.0f)
        val lon = sharedPreferences!!.getFloat("prevLong", 0.0f)
        val here = LatLng(lat.toDouble(), lon.toDouble())
        routePoints.add(here)
        Log.d(TAG, "onMapReady: here : " + here.toString())
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 19f)

        // these two lines shows the marker.
        marker = googleMap!!.addMarker(MarkerOptions().position(here).title("Here I am.").draggable(true))
        marker!!.isVisible = true

        // while these ones shows the tracks.
        route = googleMap!!.addPolyline(PolylineOptions().width(10f).color(Color.BLACK).geodesic(true))

        route!!.points = routePoints

        googleMap!!.animateCamera(cameraUpdate)
        googleMap!!.moveCamera(cameraUpdate)
    }

    override fun onResume() {
        mapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    private fun addRoutePoint(latLng: LatLng) {
        utils!!.addLatLangAtTheTime(realm!!, latLng)
    }

    private fun getAllRoutePoints(realm: Realm): MutableList<LatLng> {
        val results = utils!!.getAllLatLangAtTheTime(realm)
        val temp = ArrayList<LatLng>()
        for (result in results) {
            temp.add(result.latLng)
        }
        return temp
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
