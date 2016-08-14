package com.parametris.iteng.asdf.track

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.TextView

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.server.converter.StringToIntConverter
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.parametris.iteng.asdf.R

import cz.msebera.android.httpclient.Header

import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class LokService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var processNow = false
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null

    private val context: Context? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!processNow) {
            processNow = true
            startTracking()
        }
        return Service.START_NOT_STICKY
    }

    private fun startTracking() {
        // Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS)
            Log.d(TAG, "unable to connect.")
        else {
            // Log.d(TAG, "startTracking, creating googleApiClient.");
            googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()

            // Log.d(TAG, "startTracking, connecting googleApiClient to the server.");
            if (!googleApiClient!!.isConnecting || !googleApiClient!!.isConnected)
                googleApiClient!!.connect()
            // Log.d(TAG, "startTracking, googleApiClient connected.");
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onConnected(bundle: Bundle?) {
        // Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create()
        locationRequest!!.interval = 1000
        locationRequest!!.fastestInterval = 1000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "Klien di suspend")
    }

    override fun onLocationChanged(location: Location?) {
        // Log.d(TAG, "Checking location.");
        if (location == null) {
            Log.e(TAG, "Couldn't find the location.")
            return
        }
        Log.e(TAG, "Posisi : " + location.latitude + ", " + location.longitude + ". Akurasi : " + location.accuracy)
        if (location.accuracy > 500.0f) return
        stopLocationUpdate()
        sendData(location)
        // here be dragons
        val intent = Intent(YUHU)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lon", location.longitude)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // FIXME: 2016-01-29 Refaktor method sendData
    private fun sendData(location: Location) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getDefault()
        val date = Date(location.time)

        val prefs = this.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        var totalDistance = prefs.getFloat("totalDistance", 0f)
        val firstTimePosition = prefs.getBoolean("firstTimePosition", true)

        if (firstTimePosition)
            editor.putBoolean("firstTimePosition", false)
        else {
            val prevLocation = Location("")
            prevLocation.latitude = prefs.getFloat("prevLat", 0f).toDouble()
            prevLocation.longitude = prefs.getFloat("prevLong", 0f).toDouble()

            val distance = location.distanceTo(prevLocation)
            totalDistance += distance
            editor.putFloat("totalDistance", totalDistance)
        }
        editor.putFloat("prevLat", location.latitude.toFloat())
        editor.putFloat("prevLong", location.longitude.toFloat())
        editor.apply()

        val requestParams = RequestParams()
        requestParams.put("latitude", java.lang.Double.toString(location.latitude))
        requestParams.put("longitude", java.lang.Double.toString(location.longitude))
        requestParams.put("speed", java.lang.Float.toString(location.speed))
        requestParams.put("date", URLEncoder.encode(dateFormat.format(date)))

        if (totalDistance > 0)
            requestParams.put("distance", totalDistance)
        else
            requestParams.put("distance", 0.0)

        requestParams.put("username", prefs.getString("username", "TODO"))
        requestParams.put("phonenumber", prefs.getString("appId", "TODO"))
        requestParams.put("sessionid", prefs.getString("sessionId", "TODO"))
        requestParams.put("accuracy", java.lang.Float.toString(location.accuracy))
        requestParams.put("altitude", java.lang.Double.toString(location.altitude))
        requestParams.put("password", "parametrik2016")

        val client = AsyncHttpClient()

        client.get(defaultUploadSitePancanaka, requestParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                Log.d(TAG, "sukses.")
                // LoopjHttpClient.debugLoop(TAG, "sendData sukses", defaultUploadSite, requestParams, responseBody, headers, statusCode, null);
                stopSelf()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Log.d(TAG, "gagal.")
                // LoopjHttpClient.debugLoop(TAG, "sendData gagal", defaultUploadSite, requestParams, responseBody, headers, statusCode, error);
                client.get(defaultUploadSite, requestParams, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                        Log.d(TAG, "sent to default web.")
                        // LoopjHttpClient.debugLoop(TAG, "sendData sukses ke websmithing.", defaultUploadSite, requestParams, responseBody, headers, statusCode, null);
                        stopSelf()
                    }

                    override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                        Log.d(TAG, "you dun goofed, m8.")
                        // LoopjHttpClient.debugLoop(TAG, "sendData gagal ke websmithing.", defaultUploadSite, requestParams, responseBody, headers, statusCode, null);
                        stopSelf()
                    }
                })
                stopSelf()
            }
        })
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed")

        stopLocationUpdate()
        stopSelf()
    }

    private fun stopLocationUpdate() {
        if (googleApiClient != null && googleApiClient!!.isConnected)
            googleApiClient!!.disconnect()
    }

    companion object {

        private val TAG = "LokService"
        private val defaultUploadSitePancanaka = "http://pancanaka.net/gpstracker/updatelocation.php"
        private val defaultUploadSite = "https://www.websmithing.com/gpstracker/updatelocation.php"

        val YUHU = LokService::class.java.name + "YUHU"
    }
}

