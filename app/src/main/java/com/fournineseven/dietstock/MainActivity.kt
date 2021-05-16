package com.fournineseven.dietstock

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.fournineseven.dietstock.databinding.ActivityMainBinding
import com.fournineseven.dietstock.databinding.ContentMainBinding

import com.fournineseven.dietstock.ui.food.FoodFragmentCamera
import com.fournineseven.dietstock.ui.home.HomeFragment
import com.fournineseven.dietstock.ui.notifications.NotificationsFragment
import com.fournineseven.dietstock.ui.ranking.RankingFragment
import com.fournineseven.dietstock.ui.rolemodel.RoleModelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


private const val TAG = "MyTag"
private const val PERMISSION_REQUEST_CODE = 2

class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_DietStock)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar!!.title = ""


        val contentMainViewPager = findViewById<ViewPager2>(R.id.view_pager)
        val contentMainNavView = findViewById<BottomNavigationView>(R.id.nav_view)
        //val navController = findNavController(R.id.nav_host_fragment)

        //로그인 상태 확인
        var sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        var email: String ?= sharedpreferences.getString(LoginState.EMAIL_KEY, null)
        var password:String?= sharedpreferences.getString(LoginState.PASSWORD_KEY,null)
        if(email == null || password == null){
            var intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }

        //viewPager = binding.viewPager
        //viewPager = contentMainBinding.viewPager
        viewPager = contentMainViewPager
        viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.registerOnPageChangeCallback(PageChangeCallback())
        navView = contentMainNavView
        navView.setOnNavigationItemSelectedListener { navigationSelected(it) }
        /*appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)*/
        //navView.setupWithNavController(navController)


        requirePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACTIVITY_RECOGNITION), PERMISSION_REQUEST_CODE)
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


        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT)
            return
        }

        if(navView.selectedItemId == 3){
            if(FoodFragmentCamera().binding.foodName.text == "음식을 촬영해 주세요"){
                super.onBackPressed()
            }
            else{
                FoodFragmentCamera().reOpenCamera()
            }
        }else{
            super.onBackPressed()
        }
    }

    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm, lc){
        override fun getItemCount(): Int {
            return 5
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){

                0 -> {
                    HomeFragment()
                }
                1 -> RankingFragment()
                2 -> NotificationsFragment()
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

            R.id.navigation_notifications -> {
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

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            navView.selectedItemId = when (position) {
                0 -> R.id.navigation_home
                1 -> R.id.navigation_ranking

                2 -> R.id.navigation_notifications

                3 -> R.id.navigation_food
                4 -> R.id.navigation_rolemodel
                else -> error("no such position: $position")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)


        if(item.itemId == android.R.id.home){
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.closeDrawer(Gravity.LEFT)
            }else{
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}