package com.fournineseven.dietstock

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageView
import java.io.File

@SuppressLint("StaticFieldLeak")
object LoginState {
    const val USER_NUMBER = "user_number"

    // creating constant keys for shared preferences.
    const val SHARED_PREFS = "shared_prefs"

    // key for storing email.
    const val EMAIL_KEY = "email_key"

    // key for storing password.
    const val PASSWORD_KEY = "password_key"

    const val BEFORE_IMAGE_KEY = "before_image"
    const val AFTER_IMAGE_KEY = "after_image"

    // variable for shared preferences.
    //var sharedpreferences: SharedPreferences? = null
    var email: String?=null
    var password:String?=null
    var beforeImage:String?=null
    var afterImage:String?=null
}