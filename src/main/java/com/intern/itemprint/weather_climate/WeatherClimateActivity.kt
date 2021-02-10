package com.intern.itemprint.weather_climate

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.intern.itemprint.R
import com.intern.itemprint.Repository.model.WCWeather
import com.intern.itemprint.common.WCAlert
import com.intern.itemprint.common.WCApplication.Companion.instance
import com.intern.itemprint.utilities.WCUtilities
import kotlinx.android.synthetic.main.ip_activity_weather_climate.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherClimateActivity : AppCompatActivity() {
    private var isManualPermissionRequired = false
    var climate=ArrayList<Int>()
    private  val  itemPrintViewModel :WeatherClimateViewModel by lazy{ ViewModelProvider(this).get(WeatherClimateViewModel::class.java) }
    private var itemPrintAdapter:WeatherClimateAdapter?=null
    private val date = Calendar.getInstance().time
    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
        const val GOOGLE_MAP_PERMIT = 300
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ip_activity_weather_climate)

        rv_item.layoutManager=LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        itemPrintAdapter=WeatherClimateAdapter(this, climate)
        rv_item.adapter=itemPrintAdapter

        //itemPrintAdapter?.setItem(climate)
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        tv_date.text=currentDate
        itemPrintViewModel.retrieveClimate()
        observeViewModel()
    }

    private fun observeViewModel() {
        itemPrintViewModel.responseSuccess.observe(this, androidx.lifecycle.Observer {
            val list=it.getList()
            if (list != null) {
                for (i in list){
                    i?.getDeg()?.let {
                        it1 -> climate.add(it1) }
                }
            }
            val hashMap=mutableMapOf<Int,List<WCWeather?>>()
            if (list != null) {
                for (i in list){
                    i?.getWeather()?.let {
                            it1 -> i.getClouds()?.let { it2 ->
                        hashMap.put(it2, it1) } }
                }
            }
            tv_clear.text=hashMap.toList()[0].second[0]?.getDescription().toString()
            rv_item?.adapter?.notifyDataSetChanged()
        })
        itemPrintViewModel.responseFailure.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val getPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(getPermission, REQUEST_CODE_LOCATION_PERMISSION)
            } else {
                    getAddress()
            }
        } else {
                getAddress()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAddress()

                } else if (isManualPermissionRequired) {
                    val fragmentManager =
                            supportFragmentManager
                    val positiveClickListener = DialogInterface.OnClickListener { dialog, _ ->
                        dialog?.dismiss()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog?.dismiss()
                        Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show()
                    }
                    val alertDialog = WCAlert.newInstance(
                            "Alert",
                            "To Perform the Action Permission must required, to give permission manually click \\'ok\\",
                            positiveClickListener
                    )
                    alertDialog.show(fragmentManager, "alertDialog")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getAddress() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        instance?.let {
            LocationServices.getFusedLocationProviderClient(it)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(instance!!)
                            .removeLocationUpdates(this)
                        if (locationResult != null && locationResult.locations.size > 0) {
                            locationResult.locations.size - 1
                            val geoCoder = Geocoder(WCUtilities.mContext, Locale.getDefault())
                            val addresses: List<Address> = geoCoder.getFromLocation(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude,
                                1
                            )
                            val value = addresses[0].getAddressLine(0)
                            itemPrintViewModel.setAddressInFormat(value)
                        }
                    }
                }, Looper.getMainLooper())
        }
    }


}