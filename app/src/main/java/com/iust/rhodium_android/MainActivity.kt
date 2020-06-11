package com.iust.rhodium_android

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.iust.rhodium_android.data.BaseApplication



class MainActivity : AppCompatActivity() {

    private var permit_s =1
    lateinit var tm : TelephonyManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissiongranter()
        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val Actionbutton : Button = findViewById(R.id.info_button)
        Actionbutton.setOnClickListener{
            Toast.makeText(this, "infos ...", Toast.LENGTH_SHORT).show()
            getinfo()
        }
        var db1 = BaseApplication().appDatabase?.cellPowerDao()?.getAll()

    }
    private fun getinfo(){
        Log.d("MyActivity",tm.getNetworkOperator())
        var netclass = getNetworkClass()
        Log.d("MyActivity",netclass)
        var ci1 = tm.allCellInfo
        Log.d("MyActivity", ci1.toString())
        val out = getCellInfo(ci1.get(0))
        Log.d("MyActivity",out)
        val plmn_num : String = tm.getNetworkOperator()
        val final = "technology : " +netclass + "\n" +
                     out +
                    "plmn : " +plmn_num

        val INFOtext : TextView = findViewById(R.id.info_text)
        INFOtext.text = final
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
    private fun getCellInfo(cellInfo: CellInfo): String {
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
                    + "level of strength " + cellSignalGsm.level + "\n"

                    )
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
                    + "level of strength" + cellSignalLte.level+"\n"
                    )
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
        }
        return additional_info
    }
}