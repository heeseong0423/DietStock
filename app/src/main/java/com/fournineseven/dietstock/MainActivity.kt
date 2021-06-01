package com.fournineseven.dietstock

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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

import com.fournineseven.dietstock.ui.feedback.FeedBackFragment

import com.fournineseven.dietstock.config.TaskServer
import com.fournineseven.dietstock.retrofitness.GetUserInfoResponse
import com.fournineseven.dietstock.retrofitness.RetrofitBuilder

import com.fournineseven.dietstock.ui.food.FoodFragmentCamera
import com.fournineseven.dietstock.ui.home.HomeFragment
import com.fournineseven.dietstock.ui.ranking.RankingFragment
import com.fournineseven.dietstock.ui.rolemodel.RoleModelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val TAG = "MyTag"
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
                editor.putString(LoginState.EMAIL_KEY,null)
                editor.putString(LoginState.PASSWORD_KEY,null)
                editor.apply()

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
                Manifest.permission.CAMERAs
            ), PERMISSION_REQUEST_CODE
        )

        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var userNumber = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()
        //요청 보내기
        RetrofitBuilder.api.getUserInfo(GetUserInfoRequest(userNumber)).enqueue(object : Callback<GetUserInfoResponse>{
            override fun onResponse(
                call: Call<GetUserInfoResponse>,
                response: Response<GetUserInfoResponse>
            ) {


                var loading_layout: ConstraintLayout = findViewById(R.id.loading_layout)
                contentMainViewPager.visibility = View.VISIBLE
                loading_layout.visibility = View.GONE
                val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)

                Log.d(TAG,"이름 : ${response.body()?.result?.get(0)?.user_name}")
                Log.d(TAG,"활동량 : ${response.body()?.result?.get(0)?.activity}")
                Log.d(TAG,"나이 : ${response.body()?.result?.get(0)?.age}")
                Log.d(TAG,"전 이미지 : ${response.body()?.result?.get(0)?.beforeImage}")
                Log.d(TAG,"다음 이미지: ${response.body()?.result?.get(0)?.before_weight}")
                Log.d(TAG," BIM : ${response.body()?.result?.get(0)?.bmi}")
                Log.d(TAG,"목표 : ${response.body()?.result?.get(0)?.goal}")



                beforeImage = contentMainNavView.getHeaderView(0).findViewById(R.id.before_image)
                afterImage = contentMainNavView.getHeaderView(0).findViewById(R.id.after_image)
                myGoalTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_goal)
                myWeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_weight)
                myHeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_height)
                myAgeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_age)
                myGenderTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_gender)
                myActivityTypeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_activity_type)

                var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                var editor = sharedpreferences.edit()
                editor.putString(LoginState.NAME_KEY,response.body()?.result?.get(0)?.user_name)
                editor.putString(LoginState.BEFORE_IMAGE_KEY,response.body()?.result?.get(0)?.beforeImage)
                editor.putFloat(LoginState.GOAL_KEY,response.body()?.result?.get(0)?.goal!!.toFloat())
                editor.putFloat(LoginState.WEIGHT_KEY,response.body()?.result?.get(0)?.weight!!.toFloat())
                editor.putFloat(LoginState.HEIGHT_KEY,response.body()?.result?.get(0)?.height!!.toFloat())
                editor.putInt(LoginState.AGE_KEY,response.body()?.result?.get(0)?.age!!.toInt())
                editor.putInt(LoginState.GENDER_KEY,response.body()?.result?.get(0)?.sex!!.toInt())
                editor.putInt(LoginState.ACTIVITY_KEY,response.body()?.result?.get(0)?.activity!!.toInt())
                editor.apply()


                var beforeImageUri: String?= sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY,null)
                var afterImageUri:String ?= sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY, null)
                var myGoal:Float= sharedpreferences.getFloat(LoginState.GOAL_KEY,0.0f)
                var myWeight:Float = sharedpreferences.getFloat(LoginState.WEIGHT_KEY,0.0f)
                var myHeight:Float = sharedpreferences.getFloat(LoginState.HEIGHT_KEY,0.0f)
                var myAge:Int = sharedpreferences.getInt(LoginState.AGE_KEY,0)
                var myGender:Int = sharedpreferences.getInt(LoginState.GENDER_KEY,0)
                var myActivityType:Int = sharedpreferences.getInt(LoginState.ACTIVITY_KEY,0)
                var userName: String? = sharedpreferences.getString(LoginState.NAME_KEY,null)
                var userEmail:String ?= sharedpreferences.getString(LoginState.EMAIL_KEY,null)

                Log.d(TAG,"값들 : $myWeight $myHeight")

                /*if(beforeImageUri !=response.body()?.result?.get(0)?.beforeImage){
                    editor.putString(LoginState.BEFORE_IMAGE_KEY,response.body()?.result?.get(0)?.beforeImage)
                    beforeImageUri = sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY,null)
                    Log.d(TAG,"전11 이미지 : $beforeImageUri")
                }
                if(myGoal != response.body()?.result?.get(0)?.goal){
                    editor.putFloat(LoginState.GOAL_KEY,response.body()?.result?.get(0)?.goal!!.toFloat())
                    myGoal = sharedpreferences.getFloat(LoginState.GOAL_KEY,0.0f)
                    Log.d(TAG,"목표 : ${myGoal}")
                }
                if(myWeight != response.body()?.result?.get(0)?.weight){
                    editor.putFloat(LoginState.WEIGHT_KEY,response.body()?.result?.get(0)?.weight!!.toFloat())
                    myWeight = sharedpreferences.getFloat(LoginState.WEIGHT_KEY,0.0f)
                    Log.d(TAG,"몸무게 : $myWeight")
                }
                if(myHeight != response.body()?.result?.get(0)?.height){
                    editor.putFloat(LoginState.HEIGHT_KEY,response.body()?.result?.get(0)?.height!!.toFloat())
                    myHeight = sharedpreferences.getFloat(LoginState.HEIGHT_KEY,0.0f)
                    Log.d(TAG,"키 : $myHeight")
                }
                if(myAge != response.body()?.result?.get(0)?.age){
                    editor.putInt(LoginState.AGE_KEY,response.body()?.result?.get(0)?.age!!.toInt())
                    myAge = sharedpreferences.getInt(LoginState.AGE_KEY,0)
                    Log.d(TAG,"나이 : $myAge")
                }
                if(myGender != response.body()?.result?.get(0)?.sex){
                    editor.putInt(LoginState.GENDER_KEY,response.body()?.result?.get(0)?.sex!!.toInt())
                    myGender = sharedpreferences.getInt(LoginState.GENDER_KEY,0)
                }
                if(myActivityType != response.body()?.result?.get(0)?.activity){
                    editor.putInt(LoginState.ACTIVITY_KEY,response.body()?.result?.get(0)?.activity!!.toInt())
                    myActivityType = sharedpreferences.getInt(LoginState.ACTIVITY_KEY,0)
                }*/




                //초기화
                if (beforeImageUri != null) {
                    beforeImage.setImageURI(beforeImageUri!!.toUri())
                }

                if(afterImageUri != null){
                    afterImage.setImageURI(afterImageUri!!.toUri())
                }
                myAgeTextView.text = "나이 : " + myAge.toString() + "살"
                myGoalTextView.text = "목표 : " + myGoal.toString() + "KG"
                myWeightTextView.text = "몸무게 : " + myWeight.toString() + "KG"
                myHeightTextView.text = "키 : " + myHeight.toString() +"CM"


                myName = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_name)
                myEmail = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_email)

                if(myName != null){
                    myName.text = userName
                }

                if(myEmail != null){
                    myEmail.text = userEmail
                }

                if(myGoal < 0.2f){
                    myGoalTextView.text = "목표 입력값 없음"
                }
                if(myAge <1){
                    myAgeTextView.text = "나이 입력값 없음"
                }
                if(myHeight < 0.2f){
                    myHeightTextView.text = "키 입력값 없음"
                }
                if(myWeight < 0.2f){
                    myWeightTextView.text = "몸무게 입력값 없음"
                }


                if(myGender < 1){
                    myGenderTextView.text = "남자"
                }else{
                    myGenderTextView.text = "여자"
                }


                if(myActivityType < 1){
                    myActivityTypeTextView.text= "활동량 적음"
                }else if(myActivityType < 2 ){
                    myActivityTypeTextView.text = "활동량 보통"
                }else {
                    myActivityTypeTextView.text = "활동량 많음"
                }
            }

            override fun onFailure(call: Call<GetUserInfoResponse>, t: Throwable) {
                Log.d(TAG,"You 실패")
                Toast.makeText(baseContext,"네트워크 연결을 확인해주세요.",Toast.LENGTH_LONG).show()
                finish()
            }
        })
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
        myActivityTypeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_activity_type)

        //로그인 상태 확인
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

        //로그인 안되어있으면 SignActivity 실행
        if ((email == "" || email == null) || (password == "" || password == null)) {
            var intent = Intent(this, SignActivity::class.java)
            Log.d("시발", "시발")
            startActivity(intent)
            //finish()
        }

        //var userNo: Int? = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()
        var userNumber = sharedpreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()

        Log.d(TAG,"이메일: ${email} , 비밀번호 : ${password} 유저번호 : ${userNumber}  bbbbbbbbbbbbbbbbbbbb")

        //초기화
        if (beforeImageUri != null) {
            beforeImage.setImageURI(beforeImageUri!!.toUri())
        }

        if(afterImageUri != null){
            afterImage.setImageURI(afterImageUri!!.toUri())
        }

        myAgeTextView.text = "나이 : " + myAge.toString() + "살"
        myGoalTextView.text = "목표 : " + myGoal.toString() + "KG"
        myWeightTextView.text = "몸무게 : " + myWeight.toString() + "KG"
        myHeightTextView.text = "키 : " + myHeight.toString() +"CM"


        //입력하지 않았을 경우.
        if(email == null){
            myName = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_name)
            myEmail = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_email)
            myName.text = "나의 이름 입력값 없음"
            myEmail.text = "나의 이메일 입력값 없음"
        }
        if(myGoal < 0.2f){
            myGoalTextView.text = "목표 입력값 없음"
        }
        if(myAge <1){
            myAgeTextView.text = "나이 입력값 없음"
        }
        if(myHeight < 0.2f){
            myHeightTextView.text = "키 입력값 없음"
        }
        if(myWeight < 0.2f){
            myWeightTextView.text = "몸무게 입력값 없음"
        }
        if(myGender < 1){
            myGenderTextView.text = "남자"
        }
        if(myActivityType < 1){
            myActivityTypeTextView.text= "활동량 적음"
        }

        Log.d(TAG,"값들 : $myWeight $myHeight $myActivityType , $myAge")
    }

    override fun permissionGranted(requestCode: Int) {
        Log.d(TAG, "PERMISSION GRANTED")
    }

    override fun permissionDenied(requestCode: Int) {
        Toast.makeText(baseContext, "권한 거부됨", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)


        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT)
            return
        }

        if (navView.selectedItemId == 3) {
            if(!FoodFragmentCamera().binding.foodSearch.isIconified){
                FoodFragmentCamera().binding.foodSearch.isIconified = true
            }
            else if (FoodFragmentCamera().binding.foodName.text == "음식을 촬영해 주세요" ||
                (FoodFragmentCamera().binding.LeftConstraint.visibility == View.INVISIBLE && FoodFragmentCamera().binding.RightConstraint.visibility == View.INVISIBLE)) {
                super.onBackPressed()
            } else {
                FoodFragmentCamera().flipVisibility(true)
                FoodFragmentCamera().openCamera()
            }
        } else {
            super.onBackPressed()
        }
    }


    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        FragmentStateAdapter(fm, lc) {
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
                return true
            }
            R.id.navigation_ranking -> {
                viewPager.currentItem = 1
                return true
            }

            R.id.navigation_feedback -> {
                viewPager.currentItem = 2
                return true
            }
            R.id.navigation_food -> {
                viewPager.currentItem = 3
                return true
            }
            R.id.navigation_rolemodel -> {
                viewPager.currentItem = 4
                return true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQ_STORAGE_BEFORE -> {
                    var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                    //val img = findViewById<ImageView>(R.id.before_image)
                    data?.data?.let { uri ->
                        //  img.setImageURI(uri)
                        beforeImage.setImageURI(uri)
                        Log.d(TAG,uri.toString())
                        var editor = sharedpreferences.edit()
                        editor.putString(LoginState.BEFORE_IMAGE_KEY,uri.toString())
                        editor.apply()
                    }
                }
                REQ_STORAGE_AFTER -> {
                    var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
                    data?.data?.let { uri ->
                        //  img.setImageURI(uri)
                        afterImage.setImageURI(uri)
                        Log.d(TAG,uri.toString())
                        var editor = sharedpreferences.edit()
                        editor.putString(LoginState.AFTER_IMAGE_KEY,uri.toString())
                        editor.apply()
                    }
                }
            }
        }
    }

    override fun onCancelButtonClicked() {
        Log.d(TAG,"취소버튼 클릭")
    }

    override fun onOkButtonClicked() {
        Log.d(TAG,"확인버튼 클릭")

        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)
        myGoalTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_goal)
        myWeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_weight)
        myHeightTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_height)
        myAgeTextView = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_header_age)

        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var myGoal:Float= sharedpreferences.getFloat(LoginState.GOAL_KEY,0.0f)
        var myWeight:Float = sharedpreferences.getFloat(LoginState.WEIGHT_KEY,0.0f)
        var myHeight:Float = sharedpreferences.getFloat(LoginState.HEIGHT_KEY,0.0f)
        var myAge:Int = sharedpreferences.getInt(LoginState.AGE_KEY,0)

        myAgeTextView.text = "나이 : " + myAge.toString() + "살"
        myGoalTextView.text = "목표 : " + myGoal.toString() + "KG"
        myWeightTextView.text = "몸무게 : " + myWeight.toString() + "KG"
        myHeightTextView.text = "키 : " + myHeight.toString() +"CM"
    }
}