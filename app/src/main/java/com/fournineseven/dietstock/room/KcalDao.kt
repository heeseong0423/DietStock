package com.fournineseven.dietstock.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface KcalDao {
    @Query("SELECT * FROM userkcaldata")
    fun getAll(): List<UserKcalData>

    @Insert(onConflict = REPLACE)
    fun insert(user: UserKcalData)

    @Delete
    fun delete(user: UserKcalData)
}