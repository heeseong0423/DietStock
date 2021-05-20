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
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

import com.fournineseven.dietstock.databinding.ActivityMainBinding
import com.fournineseven.dietstock.ui.feedback.FeedBackFragment

import com.fournineseven.dietstock.config.TaskServer

import com.fournineseven.dietstock.ui.food.FoodFragmentCamera
import com.fournineseven.dietstock.ui.home.HomeFragment
import com.fournineseven.dietstock.ui.notifications.NotificationsFragment
import com.fournineseven.dietstock.ui.ranking.RankingFragment
import com.fournineseven.dietstock.ui.rolemodel.RoleModelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val TAG = "MyTag"
private const val REQ_CAMERA = 101
private const val REQ_STORAGE_BEFORE = 102
private const val REQ_STORAGE_AFTER = 103
private const val PERMISSION_REQUEST_CODE = 2

class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView

    private lateinit var beforeImage: ImageView
    private lateinit var afterImage: ImageView
    private lateinit var myName: TextView
    private lateinit var myEmail: TextView

    /*private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }*/

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

        App.retrofit = Retrofit.Builder()
            .baseUrl(TaskServer.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val contentMainViewPager = findViewById<ViewPager2>(R.id.view_pager)
        val contentMainNavView = findViewById<NavigationView>(R.id.nav_view)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.nav_bottom_view)
        //val navController = findNavController(R.id.nav_host_fragment)

        beforeImage = contentMainNavView.getHeaderView(0).findViewById(R.id.before_image)
        afterImage = contentMainNavView.getHeaderView(0).findViewById(R.id.after_image)


        //로그인 상태 확인
        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var email: String? = sharedpreferences.getString(LoginState.EMAIL_KEY, null)
        var password: String? = sharedpreferences.getString(LoginState.PASSWORD_KEY, null)
        var beforeImageUri: String?= sharedpreferences.getString(LoginState.BEFORE_IMAGE_KEY,null)
        var afterImageUri:String ?= sharedpreferences.getString(LoginState.AFTER_IMAGE_KEY, null)
        if (email == null || password == null) {
            var intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }


        if (beforeImageUri != null) {
            beforeImage.setImageURI(beforeImageUri!!.toUri())
        }

        if(afterImageUri != null){
            afterImage.setImageURI(afterImageUri!!.toUri())
        }

        if(email == null){
            myName = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_name)
            myEmail = contentMainNavView.getHeaderView(0).findViewById(R.id.nav_email)
            myName.text = "안녕하세요"
            myEmail.text = "www.naver.com"
        }

        //viewPager = binding.viewPager
        //viewPager = contentMainBinding.viewPager
        viewPager = contentMainViewPager
        viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.registerOnPageChangeCallback(PageChangeCallback())
        navView = bottomNavView
        navView.setOnNavigationItemSelectedListener { navigationSelected(it) }
        /*appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)*/
        //navView.setupWithNavController(navController)


        requirePermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACTIVITY_RECOGNITION
            ), PERMISSION_REQUEST_CODE
        )
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
            else if (FoodFragmentCamera().binding.foodName.text == "음식을 촬영해 주세요") {
                super.onBackPressed()
            } else {
                FoodFragmentCamera().reOpenCamera()
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

                0 -> {
                    HomeFragment()
                }
                1 -> RankingFragment()
                2 -> FeedBackFragment()
                3 -> {
                    requirePermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
                    FoodFragmentCamera()
                }
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
}