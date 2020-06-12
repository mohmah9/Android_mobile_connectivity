package com.iust.rhodium_android.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CellPower(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val latitude : Int,
    val longitude : Int,
    val cell_identity : String?,
    val MCC : String?,
    val MNC : String?,
    val plmn : String?,
    val Level_of_strength : String?,
    val net_type : String?,
    val RSSI : String?=null,
    val RxLev : String?=null,
    val LAC : String?=null,
    val RSCP : String?=null,
    val TAC : String?=null,
    val RSRP : String?=null,
    val RSRQ : String?=null,
    val CINR : String?=null


)