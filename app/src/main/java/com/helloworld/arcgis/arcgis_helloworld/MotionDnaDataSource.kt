package com.helloworld.arcgis.arcgis_helloworld

import android.content.Context
import android.content.pm.PackageManager
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.location.LocationDataSource
import com.navisens.motiondnaapi.MotionDna
import com.navisens.motiondnaapi.MotionDnaApplication
import com.navisens.motiondnaapi.MotionDnaInterface

class MotionDnaDataSource : MotionDnaInterface, LocationDataSource {

    var app : MotionDnaApplication
    var ctx : Context
    var pkg : PackageManager


    // Constructor with context and packagemanger for our SDK internal usage.
    constructor(ctx: Context, pkg: PackageManager, devKey: String):super(){
        this.ctx = ctx
        this.pkg = pkg

        // Instantiating core
        app = MotionDnaApplication(this)

        // Enabling GPS receivers within SDK.
        app.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY)

        // Instantiating inertial engine
        app.runMotionDna(devKey)
    }

    override fun getAppContext(): Context {
        return this.ctx
    }

    override fun receiveNetworkData(p0: MotionDna?) {
    }

    override fun receiveNetworkData(p0: MotionDna.NetworkCode?, p1: MutableMap<String, out Any>?) {
    }

    override fun receiveMotionDna(motionDna: MotionDna?) {
        // Conversion phase to convert MotionDna data structure to ARCGIS accepted data structures.
        var hdg : Double
        hdg=motionDna!!.location.heading
        var coordinate = Point(motionDna!!.location.globalLocation.longitude, motionDna!!.location.globalLocation.latitude, SpatialReferences.getWgs84())
        var position = LocationDataSource.Location(coordinate, motionDna!!.location.uncertainty.x, motionDna!!.motion.stepFrequency, hdg, false)

        updateLocation(position)
        updateHeading(hdg)
    }

    override fun reportError(errorCode: MotionDna.ErrorCode?, s: String?) {
        when (errorCode) {
            MotionDna.ErrorCode.ERROR_AUTHENTICATION_FAILED -> println("Error: authentication failed $s")
            MotionDna.ErrorCode.ERROR_SDK_EXPIRED -> println("Error: SDK expired $s")
            MotionDna.ErrorCode.ERROR_WRONG_FLOOR_INPUT -> println("Error: wrong floor input $s")
            MotionDna.ErrorCode.ERROR_PERMISSIONS -> println("Error: permissions not granted $s")
            MotionDna.ErrorCode.ERROR_SENSOR_MISSING -> println("Error: sensor missing $s")
            MotionDna.ErrorCode.ERROR_SENSOR_TIMING -> println("Error: sensor timing $s")
        }
    }

    override fun getPkgManager(): PackageManager {
        return this.pkg
    }

    override fun onStop() {
        app.stop()
    }

    override fun onStart() {

        // Trigger inertial engine to run with global positional corrections.
        app.setLocationNavisens()

        // Trigger inertial engine to run in pure inertial from given lat lon and heading.
        //app.setLocationLatitudeLongitudeAndHeadingInDegrees(37.787742, -122.396859, 315.0)

        // Tell ArcGIS data source you have completed setup.
        onStartCompleted(null);
    }
}
