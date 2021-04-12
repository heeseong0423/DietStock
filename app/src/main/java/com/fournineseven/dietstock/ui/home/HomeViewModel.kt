package com.fournineseven.dietstock.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fournineseven.dietstock.User

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = User.step.toString()
    }
    private val _textKcal = MutableLiveData<String>().apply {
        value = User.kcal.toString() + "Kcal"
    }
    val text: LiveData<String> = _text
    val textKcal: LiveData<String> = _textKcal
}