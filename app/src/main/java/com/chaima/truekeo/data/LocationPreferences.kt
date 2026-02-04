package com.chaima.truekeo.data

import android.content.Context
import android.content.SharedPreferences

class LocationPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PERMISSION_ASKED = "permission_asked"
        private const val KEY_PERMISSION_GRANTED = "permission_granted"
        private const val KEY_MANUAL_LAT = "manual_lat"
        private const val KEY_MANUAL_LNG = "manual_lng"
        private const val KEY_MANUAL_ZOOM = "manual_zoom"
        private const val KEY_USE_MANUAL = "use_manual_location"
    }

    var hasAskedPermission: Boolean
        get() = prefs.getBoolean(KEY_PERMISSION_ASKED, false)
        set(value) = prefs.edit().putBoolean(KEY_PERMISSION_ASKED, value).apply()

    var hasGrantedPermission: Boolean
        get() = prefs.getBoolean(KEY_PERMISSION_GRANTED, false)
        set(value) = prefs.edit().putBoolean(KEY_PERMISSION_GRANTED, value).apply()

    var useManualLocation: Boolean
        get() = prefs.getBoolean(KEY_USE_MANUAL, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_MANUAL, value).apply()

    fun saveManualLocation(lat: Double, lng: Double, zoom: Double) {
        prefs.edit()
            .putFloat(KEY_MANUAL_LAT, lat.toFloat())
            .putFloat(KEY_MANUAL_LNG, lng.toFloat())
            .putFloat(KEY_MANUAL_ZOOM, zoom.toFloat())
            .putBoolean(KEY_USE_MANUAL, true)
            .apply()
    }

    fun getManualLocation(): Triple<Double, Double, Double>? {
        val lat = prefs.getFloat(KEY_MANUAL_LAT, 0f).toDouble()
        val lng = prefs.getFloat(KEY_MANUAL_LNG, 0f).toDouble()
        val zoom = prefs.getFloat(KEY_MANUAL_ZOOM, 6.0f).toDouble()

        return if (lat != 0.0 && lng != 0.0) {
            Triple(lat, lng, zoom)
        } else {
            null
        }
    }

    fun clearManualLocation() {
        prefs.edit()
            .remove(KEY_MANUAL_LAT)
            .remove(KEY_MANUAL_LNG)
            .remove(KEY_MANUAL_ZOOM)
            .putBoolean(KEY_USE_MANUAL, false)
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}