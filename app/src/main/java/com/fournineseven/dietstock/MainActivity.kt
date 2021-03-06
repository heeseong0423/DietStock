package com.fournineseven.dietstock

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.fournineseven.dietstock.config.TaskServer
import com.fournineseven.dietstock.retrofitness.*
import com.fournineseven.dietstock.ui.feedback.FeedBackFragment
import com.fournineseven.dietstock.ui.food.FoodFragmentCamera
import com.fournineseven.dietstock.ui.home.HomeFragment
import com.fournineseven.dietstock.ui.ranking.RankingFragment
import com.fournineseven.dietstock.ui.rolemodel.RoleModelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*


private const val TAG = "MyTag"
private const val TAG1 = "MyImageTag"
private const val REQ_CAMERA = 101
private const val REQ_STORAGE_BEFORE = 102
private const val REQ_STORAGE_AFTER = 103
private const val PERMISSION_REQUEST_CODE = 2

class MainActivity : BaseActivity(), View.OnClickListener, UserSettingDialogInterface, NavigationView.OnNavigationItemSelectedListener{
    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView
    private lateinit var beforeImage: ImageView
    private lateinit var afterImage: ImageView
    private lateinit var myName: TextView
    private lateinit var myEmail: TextView
    private lateinit var myAgeTextView: TextView
    private lateinit var myHeightTextView: TextView
    private lateinit var myWeightTextView: TextView
    private lateinit var myGoalTextView: TextView
    private lateinit var myGenderTextView: TextView
    private lateinit var myActivityTypeTextView: TextView
    private lateinit var navigationView: NavigationView

    private val mySettingDialog by lazy{
        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        UserSettingDialog(this,this,sharedpreferences)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_change_info->{
                mySettingDialog.show()
                return true
            }
            R.id.nav_log_out->{
                var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                var editor = sharedpreferences.edit()
                editor.clear()
                editor.apply()
                alarmmCancel()
                var intent = Intent(this, SignActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.before_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                startActivityForResult(intent, REQ_STORAGE_BEFORE)
            }
            R.id.after_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                startActivityForResult(intent, REQ_STORAGE_AFTER)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_DietStock)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar!!.title = "DietStock"

        navigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)

        alarmRegister()
/*
        //?????? ?????????
        RetrofitBuilder.api.getUserInfo("22").enqueue(object : Callback<getUserInfoResponse>{
            override fun onResponse(
                call: Call<getUserInfoResponse>,
                response: Response<getUserInfoResponse>
            ) {
                Log.d(TAG,"${response.body()?.height} is height")
                Log.d(TAG,"${response.body()?.name}")
            }

            override fun onFailure(call: Call<getUserInfoResponse>, t: Throwable) {
                Log.d(TAG,"YOu ??????")
            }

        })
*/

        App.retrofit = Retrofit.Builder()
            .baseUrl(TaskServer.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val contentMainViewPager = findViewById<ViewPager2>(R.id.view_pager)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.nav_bottom_view)

        setUser()


        viewPager = contentMainViewPager
        viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.registerOnPageChangeCallback(PageChangeCallback())
        navView = bottomNavView
        navView.setOnNavigationItemSelectedListener { navigationSelected(it) }


        requirePermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.CAMERA
            ), PERMISSION_REQUEST_CODE
        )
        var loading_layout: ConstraintLayout = findViewById(R.id.loading_layout)
        contentMainViewPager.visibility = View.VISIBLE
        loading_layout.visibility = View.GONE
        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)




        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var userNumber = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()

        User.UserIntakeKcal = sharedpreferences.getFloat(LoginState.INTAKE_KEY,0.0f)


        var userNumberCheck = sharedpreferences.getString(LoginState.USER_NUMBER,null)
        if(userNumberCheck!=null) {
            //?????? ?????????
            RetrofitBuilder.api.getUserInfo(GetUserInfoRequest(userNumber))
                .enqueue(object : Callback<GetUserInfoResponse> {
                    override fun onResponse(
                        call: Call<GetUserInfoResponse>,
                        response: Response<GetUserInfoResponse>
                    ) {
                        var loading_layout: ConstraintLayout = findViewById(R.id.loading_layout)
                        contentMainViewPager.visibility = View.VISIBLE
                        loading_layout.visibility = View.GONE
                        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)

                        beforeImage =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.before_image)
                        afterImage =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.after_image)
                        myGoalTextView =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_goal)
                        myWeightTextView =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_weight)
                        myHeightTextView =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_height)
                        myAgeTextView =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_age)
                        myGenderTextView =
                            contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_gender)
                       /* myActivityTypeTextView = contentMainNavView.getHeaderView(0)
                            .findViewById(R.id.nav_header_activity_type)*/

                        var sharedpreferences =
                            getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                        var editor = sharedpreferences.edit()
                        editor.putString(
                            LoginState.NAME_KEY,
                            response.body()?.result?.get(0)?.user_name
                        )
                        editor.putString(
                            LoginState.BEFORE_IMAGE_KEY,
                            //"http://497.iptime.org/" + response.body()?.result?.get(0)?.beforeImage
                            response.body()?.result?.get(0)?.beforeImage
                        )
                        editor.putString(
                            LoginState.AFTER_IMAGE_KEY,
                            //"http://497.iptime.org/" + response.body()?.result?.get(0)?.beforeImage
                            response.body()?.result?.get(0)?.afterImage
                        )

                        editor.putFloat(
                            LoginState.GOAL_KEY,
                            response.body()?.result?.get(0)?.goal!!.toFloat()
                        )
                        editor.putFloat(
                            LoginState.WEIGHT_KEY,
                            response.body()?.result?.get(0)?.weight!!.toFloat()
                        )
                        editor.putFloat(
                            LoginState.HEIGHT_KEY,
                            response.body()?.result?.get(0)?.height!!.toFloat()
                        )
                        editor.putInt(
                            LoginState.AGE_KEY,
                            response.body()?.result?.get(0)?.age!!.toInt()
                        )
                        editor.putInt(
                            LoginState.GENDER_KEY,
                            response.body()?.result?.get(0)?.sex!!.toInt()
                        )

                        editor.putInt(
                            LoginState.ACTIVITY_KEY,
                            response.body()?.result?.get(0)?.activity!!.toInt()
                        )
                        editor.apply()


                        var beforeImageUri: String? =
                            sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY, null)
                        var afterImageUri: String? =
                            sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY, null)
                        var myGoal: Float = sharedpreferences.getFloat(LoginState.GOAL_KEY, 0.0f)
                        var myWeight: Float =
                            sharedpreferences.getFloat(LoginState.WEIGHT_KEY, 0.0f)
                        var myHeight: Float =
                            sharedpreferences.getFloat(LoginState.HEIGHT_KEY, 0.0f)
                        var myAge: Int = sharedpreferences.getInt(LoginState.AGE_KEY, 0)
                        var myGender: Int = sharedpreferences.getInt(LoginState.GENDER_KEY, 0)
                        var myActivityType: Int =
                            sharedpreferences.getInt(LoginState.ACTIVITY_KEY, 0)
                        var userName: String? =
                            sharedpreferences.getString(LoginState.NAME_KEY, null)
                        var userEmail: String? =
                            sharedpreferences.getString(LoginState.EMAIL_KEY, null)

                        if (beforeImageUri != response.body()?.result?.get(0)?.beforeImage) {
                            editor.putString(
                                LoginState.BEFORE_IMAGE_KEY,
                                response.body()?.result?.get(0)?.beforeImage
                            )
                            beforeImageUri =
                                sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY, null)
                        }

                        if (afterImageUri != response.body()?.result?.get(0)?.afterImage) {
                            editor.putString(
                                LoginState.AFTER_IMAGE_KEY,
                                response.body()?.result?.get(0)?.afterImage
                            )
                            afterImageUri =
                                sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY, null)
                        }

                        if (myGoal != response.body()?.result?.get(0)?.goal) {
                            editor.putFloat(
                                LoginState.GOAL_KEY,
                                response.body()?.result?.get(0)?.goal!!.toFloat()
                            )
                            myGoal = sharedpreferences.getFloat(LoginState.GOAL_KEY, 0.0f)
                        }
                        if (myWeight != response.body()?.result?.get(0)?.weight) {
                            editor.putFloat(
                                LoginState.WEIGHT_KEY,
                                response.body()?.result?.get(0)?.weight!!.toFloat()
                            )
                            myWeight = sharedpreferences.getFloat(LoginState.WEIGHT_KEY, 0.0f)
                        }
                        if (myHeight != response.body()?.result?.get(0)?.height) {
                            editor.putFloat(
                                LoginState.HEIGHT_KEY,
                                response.body()?.result?.get(0)?.height!!.toFloat()
                            )
                            myHeight = sharedpreferences.getFloat(LoginState.HEIGHT_KEY, 0.0f)
                        }
                        if (myAge != response.body()?.result?.get(0)?.age) {
                            editor.putInt(
                                LoginState.AGE_KEY,
                                response.body()?.result?.get(0)?.age!!.toInt()
                            )
                            myAge = sharedpreferences.getInt(LoginState.AGE_KEY, 0)
                        }
                        if (myGender != response.body()?.result?.get(0)?.sex) {
                            editor.putInt(
                                LoginState.GENDER_KEY,
                                response.body()?.result?.get(0)?.sex!!.toInt()
                            )
                            myGender = sharedpreferences.getInt(LoginState.GENDER_KEY, 0)
                        }
                        if (myActivityType != response.body()?.result?.get(0)?.activity) {
                            editor.putInt(
                                LoginState.ACTIVITY_KEY,
                                response.body()?.result?.get(0)?.activity!!.toInt()
                            )
                            myActivityType = sharedpreferences.getInt(LoginState.ACTIVITY_KEY, 0)
                        }


                        //?????????
                        if (beforeImageUri != null) {
                            //beforeImage.setImageURI(beforeImageUri!!.toUri())
                            Glide.with(beforeImage).load(
                                TaskServer.base_url + response.body()?.result?.get(0)?.beforeImage
                            ).error(R.drawable.food_icon)
                                .placeholder(R.drawable.food_icon).into(beforeImage)
                        }

                        if (afterImageUri != null) {
                            //afterImage.setImageURI(afterImageUri!!.toUri())
                            Glide.with(afterImage).load(
                                //TaskServer.base_url + response.body()?.result?.get(0)?.afterImage
                                sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY,"0")
                            ).error(R.drawable.food_icon)
                                .placeholder(R.drawable.food_icon).into(afterImage)
                        }
                        myAgeTextView.text = "?????? : " + myAge.toString() + "???"
                        myGoalTextView.text = "?????? : " + myGoal.toString() + "KG"
                        myWeightTextView.text = "????????? : " + myWeight.toString() + "KG"
                        myHeightTextView.text = "??? : " + myHeight.toString() + "CM"


                        myName = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_name)
                        myEmail = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_email)

                        if (myName != null) {
                            myName.text = userName
                        }

                        if (myEmail != null) {
                            myEmail.text = userEmail
                        }

                        if (myGoal < 0.2f) {
                            myGoalTextView.text = "?????? ????????? ??????"
                        }
                        if (myAge < 1) {
                            myAgeTextView.text = "?????? ????????? ??????"
                        }
                        if (myHeight < 0.2f) {
                            myHeightTextView.text = "??? ????????? ??????"
                        }
                        if (myWeight < 0.2f) {
                            myWeightTextView.text = "????????? ????????? ??????"
                        }


                        if (myGender <=1) {
                            myGenderTextView.text = "??????"
                        } else {
                            myGenderTextView.text = "??????"
                        }


                        /*if (myActivityType < 1) {
                            myActivityTypeTextView.text = "????????? ??????"
                        } else if (myActivityType < 2) {
                            myActivityTypeTextView.text = "????????? ??????"
                        } else {
                            myActivityTypeTextView.text = "????????? ??????"
                        }*/
                    }

                    override fun onFailure(call: Call<GetUserInfoResponse>, t: Throwable) {
                        Toast.makeText(baseContext, "???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                })
        }

        var today =sharedpreferences.getString(LoginState.DATE_KEY,null)
        if(today != null){
            RetrofitBuilder.api.getDailyKcal(GetDailyKcalRequest(user_no = userNumber, date = today))
                .enqueue(object : Callback<GetDailyKcalResponse>{
                    override fun onResponse(
                        call: Call<GetDailyKcalResponse>,
                        response: Response<GetDailyKcalResponse>
                    ) {
                        User.UserIntakeKcal = response.body()?.result?.get(0)!!.kcalSum
                    }

                    override fun onFailure(call: Call<GetDailyKcalResponse>, t: Throwable) {

                    }

                })
        }
    }

    private fun setUser(){
        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)

        beforeImage = contentMainNavView.getHeaderView(0).findViewById(R.id.before_image)
        afterImage = contentMainNavView.getHeaderView(0).findViewById(R.id.after_image)
        myGoalTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_goal)
        myWeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_weight)
        myHeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_height)
        myAgeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_age)
        myGenderTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_gender)
        /*myActivityTypeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_activity_type)*/

        //????????? ?????? ??????
        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var email: String? = sharedpreferences.getString(LoginState.EMAIL_KEY, null)
        var password: String? = sharedpreferences.getString(LoginState.PASSWORD_KEY, null)
        var beforeImageUri: String?= sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY,null)
        var afterImageUri:String ?= sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY, null)
        var myGoal:Float= sharedpreferences.getFloat(LoginState.GOAL_KEY,0.0f)
        var myWeight:Float = sharedpreferences.getFloat(LoginState.WEIGHT_KEY,0.0f)
        var myHeight:Float = sharedpreferences.getFloat(LoginState.HEIGHT_KEY,0.0f)
        var myAge:Int = sharedpreferences.getInt(LoginState.AGE_KEY,0)
        var myGender:Int = sharedpreferences.getInt(LoginState.GENDER_KEY,0)
        var myActivityType:Int = sharedpreferences.getInt(LoginState.ACTIVITY_KEY,0)
        var lowKcal:Float = sharedpreferences.getFloat(LoginState.LOW_KEY,0.0f) //???????????????
        var highKcal:Float = sharedpreferences.getFloat(LoginState.HIGH_KEY,0.0f) //?????? ?????????
        var startKcal:Float = sharedpreferences.getFloat(LoginState.START_KEY,0.0f) // ???????????????
        var endKcal: Float = sharedpreferences.getFloat(LoginState.END_KEY,0.0f) //???????????????
        var date:String? = sharedpreferences.getString(LoginState.DATE_KEY,null)

        //????????? ?????????????????? SignActivity ??????
        if ((email == "" || email == null) || (password == "" || password == null)) {
            var intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }

        //???????????????
        if(date == null){
            val dt = Date()
            val full_sdf = SimpleDateFormat("yyyy-MM-dd")
            var todayDate:String = full_sdf.format(dt).toString()
            var todayDateMidNight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()

            var editor = sharedpreferences.edit()
            editor.putFloat(LoginState.INTAKE_KEY,0.0f)
            editor.putFloat(LoginState.START_KEY,0.0f)
            editor.putString(LoginState.DATE_KEY,todayDate)
            editor.putLong(LoginState.START_TIME_KEY,todayDateMidNight)
            editor.putFloat(LoginState.LOW_KEY,0.0f)
            editor.apply()
        }

        //var userNo: Int? = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()
        var userNumber = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()


        //?????????
        if (beforeImageUri != null) {
            //beforeImage.setImageURI(beforeImageUri!!.toUri())
            Glide.with(beforeImage).load(
                TaskServer.base_url + beforeImageUri!!.toUri()
            ).error(R.drawable.food_icon)
                .placeholder(R.drawable.food_icon).into(beforeImage)
        }

        if(afterImageUri != null){
            //afterImage.setImageURI(afterImageUri!!.toUri())
            Glide.with(afterImage).load(
                TaskServer.base_url + afterImageUri!!.toUri()
            ).error(R.drawable.food_icon)
                .placeholder(R.drawable.food_icon).into(afterImage)
        }

        myAgeTextView.text = "?????? : " + myAge.toString() + "???"
        myGoalTextView.text = "?????? : " + myGoal.toString() + "KG"
        myWeightTextView.text = "????????? : " + myWeight.toString() + "KG"
        myHeightTextView.text = "??? : " + myHeight.toString() +"CM"


        //???????????? ????????? ??????.
        if(email == null){
            myName = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_name)
            myEmail = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_email)
            myName.text = "?????? ?????? ????????? ??????"
            myEmail.text = "?????? ????????? ????????? ??????"
        }
        if(myGoal < 0.2f){
            myGoalTextView.text = "?????? ????????? ??????"
        }
        if(myAge <1){
            myAgeTextView.text = "?????? ????????? ??????"
        }
        if(myHeight < 0.2f){
            myHeightTextView.text = "??? ????????? ??????"
        }
        if(myWeight < 0.2f){
            myWeightTextView.text = "????????? ????????? ??????"
        }
        if(myGender <= 1){
            myGenderTextView.text = "??????"
        }else{
            myGenderTextView.text = "??????"
        }
        /*if(myActivityType < 1){
            myActivityTypeTextView.text= "????????? ??????"
        }*/

    }

    override fun permissionGranted(requestCode: Int) {

    }

    override fun permissionDenied(requestCode: Int) {
        Toast.makeText(baseContext, "?????? ?????????", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT)
            return
        }else{
            super.onBackPressed()
        }
    }

    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
        override fun getItemCount(): Int {
            return 5
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {

                0 -> HomeFragment()
                1 -> RankingFragment()
                2 -> FeedBackFragment()
                3 -> FoodFragmentCamera()
                4 -> RoleModelFragment()
                else -> error("no such Position $position")
            }
        }
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.navigation_home -> {
                viewPager.currentItem = 0
                return false
            }
            R.id.navigation_ranking -> {
                viewPager.currentItem = 1
                return false
            }

            R.id.navigation_feedback -> {
                viewPager.currentItem = 2
                return false
            }
            R.id.navigation_food -> {
                viewPager.currentItem = 3
                return false
            }
            R.id.navigation_rolemodel -> {
                viewPager.currentItem = 4
                return false
            }
        }
        return false
    }

    private inner class PageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            navView.selectedItemId = when (position) {
                0 -> R.id.navigation_home
                1 -> R.id.navigation_ranking
                2 -> R.id.navigation_feedback
                3 -> R.id.navigation_food
                4 -> R.id.navigation_rolemodel
                else -> error("no such position: $position")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    fun getFileName(uri: Uri): String?{
        val contentResolver = this.getContentResolver()
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        try{
            if(cursor == null)
                return null
            cursor.moveToFirst()
            val fileName: String = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            cursor.close()
            return fileName
        }catch (e: Exception){
            cursor?.close()
            return null
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQ_STORAGE_BEFORE -> {
                    var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                    //val img = findViewById<ImageView>(R.id.before_image)
                    data?.data?.let { uri ->
                        var cursor: Cursor? = null

                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        assert(uri != null)
                        cursor = contentResolver.query(uri, proj, null, null, null)
                        assert(cursor != null)
                        val column_index =
                            cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor.moveToFirst()
                        var file = File(cursor.getString(column_index))
                        val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        var body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        var userNo = sharedpreferences.getString(LoginState.USER_NUMBER,"0")
                        cursor?.close()
                        val userNumber: RequestBody = RequestBody.create(MultipartBody.FORM, userNo)
                        RetrofitBuilder.api.updateBeforeImage(userNumber,body).enqueue(object: Callback<UpdateBeforeImageResponse>{
                            override fun onResponse(
                                call: Call<UpdateBeforeImageResponse>,
                                response: Response<UpdateBeforeImageResponse>
                            ) {
                                //  img.setImageURI(uri)
                                beforeImage.setImageURI(uri)
                                var editor = sharedpreferences.edit()
                                //editor.putString("http://497.iptime.org/" + LoginState.BEFORE_IMAGE_KEY,uri.toString())
                                editor.putString("LoginState.BEFORE_IMAGE_KEY",uri.toString())
                                editor.apply()
                            }

                            override fun onFailure(
                                call: Call<UpdateBeforeImageResponse>,
                                t: Throwable
                            ) {

                                Toast.makeText(this@MainActivity,"??? ????????? ??????",Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                REQ_STORAGE_AFTER -> {
                    var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                    data?.data?.let { uri ->
                        var cursor: Cursor? = null

                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        assert(uri != null)
                        cursor = contentResolver.query(uri, proj, null, null, null)
                        assert(cursor != null)
                        val column_index =
                            cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor!!.moveToFirst()
                        var file = File(cursor!!.getString(column_index))
                        val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        var body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        var userNo = sharedpreferences.getString(LoginState.USER_NUMBER,"0")
                        cursor?.close()
                        val userNumber: RequestBody = RequestBody.create(MultipartBody.FORM, userNo)

                        RetrofitBuilder.api.updateAfterImage(userNumber,body).enqueue(object: Callback<UpdateAfterImageResponse>{
                            override fun onResponse(
                                call: Call<UpdateAfterImageResponse>,
                                response: Response<UpdateAfterImageResponse>
                            ) {
                                //  img.setImageURI(uri)
                                afterImage.setImageURI(uri)
                                var editor = sharedpreferences.edit()
                                //editor.putString("http://497.iptime.org/" + LoginState.AFTER_IMAGE_KEY,uri.toString())
                                editor.putString(LoginState.AFTER_IMAGE_KEY,uri.toString())
                                editor.apply()
                            }

                            override fun onFailure(
                                call: Call<UpdateAfterImageResponse>,
                                t: Throwable
                            ) {

                                Toast.makeText(this@MainActivity,"??? ????????? ??????",Toast.LENGTH_SHORT).show()
                            }
                        })
                    }


                }
            }
        }
    }

    override fun onCancelButtonClicked() {

    }

    override fun onOkButtonClicked() {

        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)
        myGoalTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_goal)
        myWeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_weight)
        myHeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_height)

        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var myGoal:Float= sharedpreferences.getFloat(LoginState.GOAL_KEY,0.0f)
        var myWeight:Float = sharedpreferences.getFloat(LoginState.WEIGHT_KEY,0.0f)
        var myHeight:Float = sharedpreferences.getFloat(LoginState.HEIGHT_KEY,0.0f)

        myGoalTextView.text = "?????? : " + myGoal.toString() + "KG"
        myWeightTextView.text = "????????? : " + myWeight.toString() + "KG"
        myHeightTextView.text = "??? : " + myHeight.toString() +"CM"
    }

    fun alarmRegister(){
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, AlarmReceiver.REQUEST_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val repeatInterval: Long =  86400 // ????????????
        //val repeatInterval: Long =  60 // ????????????
        /*val triggerTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
            .toEpochSecond() + 1619741870*/
        val triggerTime = LocalDateTime.of(LocalDate.now(),LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toEpochSecond()

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime, repeatInterval,
            pendingIntent)
    }

    fun alarmmCancel(){
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, AlarmReceiver.REQUEST_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }
}