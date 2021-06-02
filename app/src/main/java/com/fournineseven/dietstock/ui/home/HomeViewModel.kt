package com.fournineseven.dietstock.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fournineseven.dietstock.User

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {

        value = "칼로리 소모량 : ${User.kcal + User.PKcal} Kcal"

    }
    val text: LiveData<String> = _text
}