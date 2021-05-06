package com.Zaich.latihangooglemapscurrentlocation

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getCurrentLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("RestrictedApi")
    fun getCurrentLocation() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest().setInterval(3000)
                .setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback(){
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        for (location in p0.locations){
                            mapFragment.getMapAsync( OnMapReadyCallback {
                                mMap = it
                                if (ActivityCompat.checkSelfPermission(this@MapsActivity,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                this@MapsActivity,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(this@MapsActivity,
//                                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                                }
                                mMap.isMyLocationEnabled = true
                                mMap.uiSettings.isZoomControlsEnabled = true
                                val locationResult = LocationServices
                                        .getFusedLocationProviderClient(this@MapsActivity).lastLocation

                                locationResult.addOnCompleteListener(this@MapsActivity) {
                                    if (it.isSuccessful && it.result != null){
                                        var currentLocation = it.result
                                        var currentLatitude = currentLocation.latitude
                                        var currentLongitude = currentLocation.longitude
                                        mMap.clear()
                                        val geocoder = Geocoder(this@MapsActivity)
                                        val geoCoderResult  = geocoder.getFromLocation(currentLocation.latitude,
                                                currentLocation.longitude, 1)


                                        var myLocation = LatLng(currentLatitude, currentLongitude)
                                        mMap.addMarker(MarkerOptions().position(myLocation)
                                                .title(geoCoderResult[0].getAddressLine(0))).showInfoWindow()
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                                    }
                                }
                            })
                        }
                    }
                },
                Looper.myLooper()
        )

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }
}