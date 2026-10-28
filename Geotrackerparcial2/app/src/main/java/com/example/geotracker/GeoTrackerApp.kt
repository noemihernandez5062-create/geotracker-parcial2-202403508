package com.example.geotracker

import android.app.Application
import com.example.geotracker.data.local.TokenManager

class GeoTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // El TokenManager necesita un Context para usar SharedPreferences
        TokenManager.init(this)
    }
}
