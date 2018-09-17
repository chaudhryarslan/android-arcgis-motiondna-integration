package com.helloworld.arcgis.arcgis_helloworld

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.navisens.motiondnaapi.MotionDnaApplication
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.LocationDisplay


class MainActivity : AppCompatActivity(){

    private lateinit var mMapView : MapView
    lateinit var motionDnaRuntimeSource: MotionDnaDataSource


    private fun setupMap() {
        if (mMapView != null) {
            val basemapType = Basemap.Type.STREETS_VECTOR
            val latitude = 34.05293
            val longitude = -118.24368
            val levelOfDetail = 11
            val map = ArcGISMap(basemapType, latitude, longitude, levelOfDetail)
            mMapView.map = map
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.resume()
    }

    override fun onDestroy() {
        mMapView.dispose()
        super.onDestroy()
    }

    companion object {
        private val REQUEST_MDNA_PERMISSIONS = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Method called when permissions have been confirmed

        // Instantiating our custom MotionDnaDataSource that will be providing our positioning to the ARCGIS UI.
        motionDnaRuntimeSource = MotionDnaDataSource(applicationContext, packageManager, "YOURDEVELOPERKEY")

        // Now we override the internal positioning data source of the ARCGIS Map.
        mMapView.locationDisplay.locationDataSource = motionDnaRuntimeSource

        // Set camera mode to rotate with heading.
        mMapView.locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION)

        // Starts our MotionDnaDataSource.
        mMapView.locationDisplay.startAsync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMapView = findViewById(R.id.mapView);
        setupMap();

        // Runs Navisens' permission request.
        ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS)
    }
}
