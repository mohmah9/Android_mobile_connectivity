package com.iust.rhodium_android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.iust.rhodium_android.data.AppDatabase
import com.iust.rhodium_android.data.model.CellPower
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener



class MainActivity : AppCompatActivity() {

    private var permit_s =1
    var repeat : Int = 0
    lateinit var tm : TelephonyManager
    private var db: AppDatabase? = null
    var handler: Handler = Handler()
    val delayer = 10000
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude : Double = 35.0
    var longitude : Double = 40.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissiongranter()
        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        db = AppDatabase.getAppDataBase(context = this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val Actionbutton : Button = findViewById(R.id.info_button)
        val text2 : TextView = findViewById(R.id.text2)
        getLastLocation()
        Actionbutton.setOnClickListener{
            Toast.makeText(this, "infos ...", Toast.LENGTH_SHORT).show()
            if (repeat==0){
                repeat=1
            }else{
                repeat=0
            }
            text2.text = "repeat is : " + repeat.toString()
        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (repeat == 1){
                    getinfo()
                }
                handler.postDelayed(this, delayer.toLong())
            }
        }, delayer.toLong())

    }

    override fun onResume() {
        super.onResume()
        repeat =1
    }

    override fun onStop() {
        super.onStop()
        repeat=0
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
                requestNewLocationData()
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 9000
        mLocationRequest.fastestInterval = 5000
//        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            latitude = mLastLocation.latitude
            longitude= mLastLocation.longitude
        }
    }

    private fun getinfo(){
        var ci1 = tm.allCellInfo
        Log.d("MyActivity", ci1.toString())
        val out = getCellInfo(ci1.get(0))
        Log.d("MyActivity",out.toString())
        var my_info : CellPower
        if (out["type"]=="2"){
            my_info = CellPower(latitude = latitude,longitude = longitude,Level_of_strength = out["Level_of_strength"],MCC = out["MCC"],MNC = out["MNC"],plmn = out["plmn"],cell_identity = out["cell_identity"],net_type = out["net_type"],LAC = out["LAC"],RSSI = out["RSSI"] ,RxLev = out["RxLev"])
        }else if (out["type"]=="3"){
            my_info = CellPower(latitude = latitude,longitude = longitude,Level_of_strength = out["Level_of_strength"],MCC = out["MCC"],MNC = out["MNC"],plmn = out["plmn"],cell_identity = out["cell_identity"],net_type = out["net_type"],LAC = out["LAC"],RSCP = out["RSCP"])
        }else{
            my_info = CellPower(latitude = latitude,longitude = longitude,Level_of_strength = out["Level_of_strength"],MCC = out["MCC"],MNC = out["MNC"],plmn = out["plmn"],cell_identity = out["cell_identity"],net_type = out["net_type"],TAC = out["TAC"],RSRP = out["RSRP"],RSRQ = out["RSRQ"],CINR = out["CINR"])
        }
        val INFOtext : TextView = findViewById(R.id.info_text)
        db?.cellPowerDao()?.insert(my_info)
        var my_info2 = db?.cellPowerDao()?.getAll()
        INFOtext.text = my_info2.toString() + "\n"
    }

    private fun permissiongranter() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    Toast.makeText(this@MainActivity, "permission granted", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity, arrayOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), permit_s
                    )


                }

            }).check()
    }
    private fun getNetworkClass(): String {
        val networkType = tm.getDataNetworkType()
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS -> return "GPRS(2G)"
            TelephonyManager.NETWORK_TYPE_GSM -> return "GSM(2G)"
            TelephonyManager.NETWORK_TYPE_EDGE -> return "EDGE(2G)"
            TelephonyManager.NETWORK_TYPE_CDMA -> return "CDMA(2G)"
            TelephonyManager.NETWORK_TYPE_1xRTT -> return "1XRTT(2G)"
            TelephonyManager.NETWORK_TYPE_IDEN -> return "IDEN(2G)"
            TelephonyManager.NETWORK_TYPE_UMTS -> return "UMTS(3G)"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return "TD_SCDMA(3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_0-> return "EVDO_0(3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_A-> return "EVDO_A(3G)"
            TelephonyManager.NETWORK_TYPE_HSDPA-> return "HSDPA(3G)"
            TelephonyManager.NETWORK_TYPE_HSUPA-> return "HSUPA(3G)"
            TelephonyManager.NETWORK_TYPE_HSPA-> return "HSPA(3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_B-> return "EVDO_B(3G)"
            TelephonyManager.NETWORK_TYPE_EHRPD-> return "EHRPD(3G)"
            TelephonyManager.NETWORK_TYPE_HSPAP -> return "HSPAP(3G)"
            TelephonyManager.NETWORK_TYPE_LTE -> return "LTE(4G)"
//            TelephonyManager.NETWORK_TYPE_NR -> return "5G"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> return "Unknown"
            else -> return "No network"
        }
    }
    private fun getCellInfo(cellInfo: CellInfo): HashMap<Any? ,String?> {
        var netclass = getNetworkClass()
        Log.d("MyActivity",netclass)
        var map = hashMapOf<Any?, String?>()
        var additional_info: String =""
        if (cellInfo is CellInfoGsm) {
            val cellIdentityGsm = cellInfo.cellIdentity
            val cellSignalGsm = cellInfo.cellSignalStrength
            additional_info = ("cell identity " + cellIdentityGsm.cid + "\n"
                    + "MCC " + cellIdentityGsm.mcc + "\n"
                    + "MNC " + cellIdentityGsm.mnc + "\n"
                    + "LAC " + cellIdentityGsm.lac + "\n"
                    + "RSSI " + cellSignalGsm.dbm + "\n"
                    + "RXlex  " + cellSignalGsm.asuLevel + "\n"
                    + "level of strength " + cellSignalGsm.level + "\n")

            map["cell_identity"]=cellIdentityGsm.cid.toString()
            map["MCC"]=cellIdentityGsm.mcc.toString()
            map["MNC"]=cellIdentityGsm.mnc.toString()
            map["LAC"]=cellIdentityGsm.lac.toString()
            map["RSSI"]=cellSignalGsm.dbm.toString()
            map["RxLev"]=cellSignalGsm.asuLevel.toString()
            map["Level_of_strength"]=cellSignalGsm.level.toString()
            map["type"]="2"
        } else if (cellInfo is CellInfoLte) {
            val cellIdentityLte = cellInfo.cellIdentity
            val cellSignalLte = cellInfo.cellSignalStrength
            additional_info = ("cell identity " + cellIdentityLte.ci + "\n"
                    + "MCC " + cellIdentityLte.mcc + "\n"
                    + "MNC " + cellIdentityLte.mnc + "\n"
                    + "physical cell " + cellIdentityLte.pci + "\n"
                    + "TAC " + cellIdentityLte.tac + "\n"
                    + "RSRP " + cellSignalLte.rsrp + "\n"
                    + "RSRQ " + cellSignalLte.rsrq + "\n"
                    + "CINR " + cellSignalLte.rssnr + "\n"
                    + "level of strength" + cellSignalLte.level+"\n")
            map["cell_identity"]=cellIdentityLte.ci.toString()
            map["MCC"]=cellIdentityLte.mcc.toString()
            map["MNC"]=cellIdentityLte.mnc.toString()
            map["TAC"]=cellIdentityLte.tac.toString()
            map["RSRP"]=cellSignalLte.rsrp.toString()
            map["RSRQ"]=cellSignalLte.rsrq.toString()
            map["CINR"]=cellSignalLte.rssnr.toString()
            map["Level_of_strength"]=cellSignalLte.level.toString()
            map["type"]="4"
        } else if (cellInfo is CellInfoWcdma) {
            val cellIdentityWcdma = cellInfo.cellIdentity
            val cellSignalWcdma = cellInfo.cellSignalStrength
            additional_info = ("cell identity " + cellIdentityWcdma.cid + "\n"
                    + "MCC " + cellIdentityWcdma.mcc + "\n"
                    + "MNC " + cellIdentityWcdma.mnc + "\n"
                    + "LAC" + cellIdentityWcdma.lac + "\n"
                    + "RSCP" + cellSignalWcdma.dbm + "\n"
//                    + "EC/No" + cellInfo. + "\n"
                    + "level of strength" + cellSignalWcdma.level + "\n"
//                    + "local area umts" + cellIdentityWcdma.lac + "\n"
                    )
            map["cell_identity"]=cellIdentityWcdma.cid.toString()
            map["MCC"]=cellIdentityWcdma.mcc.toString()
            map["MNC"]=cellIdentityWcdma.mnc.toString()
            map["LAC"]=cellIdentityWcdma.lac.toString()
            map["RSCP"]=cellSignalWcdma.dbm.toString()
//            map["RxLev"]=cellSignalGsm.asuLevel
            map["Level_of_strength"]=cellSignalWcdma.level.toString()
            map["type"]="3"
        }
        map["net_type"]=netclass
        map["plmn"] = tm.getNetworkOperator().toString()
        return map
    }
}

