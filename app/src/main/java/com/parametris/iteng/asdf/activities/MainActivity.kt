package com.parametris.iteng.asdf.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.parametris.iteng.asdf.R
import com.parametris.iteng.asdf.fragments.AmmunitionFragment
import com.parametris.iteng.asdf.fragments.ChatFragment
import com.parametris.iteng.asdf.fragments.HealthFragment
import com.parametris.iteng.asdf.fragments.MyMapFragment
import com.parametris.iteng.asdf.fragments.SendFileFragment
import com.parametris.iteng.asdf.models.Utils
import com.parametris.iteng.asdf.track.LokAlarmReceiver

import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity() {
    internal var toolbar: Toolbar? = null
    internal var nvDrawer: NavigationView? = null
    internal var dlDrawer: DrawerLayout? = null
    internal var drawerToggle: ActionBarDrawerToggle? = null

    internal var trackingNow: Boolean = false
    internal var alarmManager: AlarmManager? = null
    internal var trackIntent: Intent? = null
    internal var pendingIntent: PendingIntent? = null

    internal var realm: Realm? = null
    internal var realmConfiguration: RealmConfiguration? = null
    internal var utils: Utils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        nvDrawer = findViewById(R.id.nvView) as NavigationView?
        setupDrawerContent(nvDrawer!!)
        dlDrawer = findViewById(R.id.drawer_layout) as DrawerLayout?

        drawerToggle = setupDrawerToggle()

        dlDrawer!!.setDrawerListener(drawerToggle)

        nvDrawer!!.menu.getItem(0).isChecked = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, MyMapFragment()).commit()

        val sharedPreferences = this.getSharedPreferences("asdf", Context.MODE_PRIVATE)
        trackingNow = sharedPreferences.getBoolean("trackingNow", false)
        val firstTime = sharedPreferences.getBoolean("firstTime", true)

        if (firstTime) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstTime", false)
            editor.putString("username", "TODO")
            editor.apply()
        }
        trackLocation()
    }

    private fun trackLocation() {
        if (!googlePlayEnabled()) return
        startAlarmManager()
    }

    private fun startAlarmManager() {
        val context = baseContext
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        trackIntent = Intent(context, LokAlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, trackIntent, 0)
        alarmManager!!.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 3000, pendingIntent)
    }

    private fun googlePlayEnabled(): Boolean {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) {
        var fragment: Fragment? = null

        val fragmentClass: Class<*>
        when (menuItem.itemId) {
            R.id.nav_home_map -> fragmentClass = MyMapFragment::class.java
            R.id.nav_condition_health -> fragmentClass = HealthFragment::class.java
            R.id.nav_condition_ammunition -> fragmentClass = AmmunitionFragment::class.java
            R.id.nav_communication_chat -> fragmentClass = ChatFragment::class.java
            R.id.nav_communicator_send_file -> fragmentClass = SendFileFragment::class.java
        /*
            case R.id.nav_condition:
                fragmentClass = HealthFragment.class;
                break;
            */
            else -> fragmentClass = MyMapFragment::class.java
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()

        // Highlight the selected item, update the title, and close the drawer
        menuItem.isChecked = true
        title = menuItem.title
        dlDrawer!!.closeDrawers()
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this,
                dlDrawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle!!.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

}
