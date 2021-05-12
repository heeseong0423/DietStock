package com.fournineseven.dietstock.room

import androidx.room.Database
import androidx.room.RoomDatabase



@Database(entities = arrayOf(UserKcalData::class), version = 2)

abstract class KcalDatabase : RoomDatabase(){
    abstract fun kcalDao(): KcalDao
}