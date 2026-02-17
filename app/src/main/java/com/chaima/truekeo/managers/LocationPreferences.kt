package com.chaima.truekeo.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class LocationPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PERMISSION_ASKED = "permission_asked"
        private const val KEY_USE_MANUAL = "use_manual_location"

        private const val KEY_MANUAL_LAT = "manual_lat"
        private const val KEY_MANUAL_LNG = "manual_lng"
        private const val KEY_MANUAL_ZOOM = "manual_zoom"
    }

    var hasAskedPermission: Boolean
        get() = prefs.getBoolean(KEY_PERMISSION_ASKED, false)
        set(value) = prefs.edit { putBoolean(KEY_PERMISSION_ASKED, value) }

    var useManualLocation: Boolean
        get() = prefs.getBoolean(KEY_USE_MANUAL, false)
        set(value) = prefs.edit { putBoolean(KEY_USE_MANUAL, value) }

    fun saveManualLocation(lat: Double, lng: Double, zoom: Double) {
        prefs.edit {
            putFloat(KEY_MANUAL_LAT, lat.toFloat())
            putFloat(KEY_MANUAL_LNG, lng.toFloat())
            putFloat(KEY_MANUAL_ZOOM, zoom.toFloat())
            putBoolean(KEY_USE_MANUAL, true)
        }
    }

    fun getManualLocation(): Triple<Double, Double, Double>? {
        if (!useManualLocation) return null

        val lat = prefs.getFloat(KEY_MANUAL_LAT, 0f).toDouble()
        val lng = prefs.getFloat(KEY_MANUAL_LNG, 0f).toDouble()
        val zoom = prefs.getFloat(KEY_MANUAL_ZOOM, 0f).toDouble()

        val safeZoom = if (zoom == 0.0) 10.0 else zoom
        return if (lat != 0.0 && lng != 0.0) Triple(lat, lng, safeZoom) else null
    }

    fun clearManualLocation() {
        prefs.edit {
            remove(KEY_MANUAL_LAT)
            remove(KEY_MANUAL_LNG)
            remove(KEY_MANUAL_ZOOM)
            putBoolean(KEY_USE_MANUAL, false)
        }
    }

    fun clearAllBlocking() {
        prefs.edit(commit = true) { clear() }
    }
}