package com.fournineseven.dietstock.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserKcalData(

    @PrimaryKey(autoGenerate = true)

    val no: Int,

    @ColumnInfo(name = "base_kcal")
    val baseKcal: Float,

    @ColumnInfo(name = "physical_kcal")
    val physicalKcal: Float,


    //시가
    @ColumnInfo(name = "start_time")
    val startTime: Float,

    //종가
    @ColumnInfo(name = "end_time")
    val endTime: Float,

    @ColumnInfo(name = "high_Kcal")
    val highKcal: Float,

    @ColumnInfo(name = "low_Kcal")
    val lowKcal: Float

)
