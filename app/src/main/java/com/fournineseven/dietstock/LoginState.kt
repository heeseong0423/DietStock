package com.fournineseven.dietstock

import android.content.SharedPreferences

object LoginState {
    // creating constant keys for shared preferences.
    const val SHARED_PREFS = "shared_prefs"

    // key for storing email.
    const val EMAIL_KEY = "email_key"

    // key for storing password.
    const val PASSWORD_KEY = "password_key"

    // variable for shared preferences.
    //var sharedpreferences: SharedPreferences? = null
    var email: String?=null
    var password:String?=null
}