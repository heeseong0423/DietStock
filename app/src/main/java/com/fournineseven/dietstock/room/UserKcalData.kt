package com.fournineseven.dietstock.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserKcalData(
    @PrimaryKey
    val no: Int,

    @ColumnInfo(name = "base_kcal")
    val baseKcal: Float,

    @ColumnInfo(name = "physical_kcal")
    val physicalKcal: Float,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    val endTime: Long
)
