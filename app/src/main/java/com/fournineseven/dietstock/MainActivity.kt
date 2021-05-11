package com.fournineseven.dietstock

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.fournineseven.dietstock.databinding.ActivityMainBinding
import com.fournineseven.dietstock.ui.home.HomeFragment
import com.fournineseven.dietstock.ui.notifications.NotificationsFragment
import com.fournineseven.dietstock.ui.ranking.RankingFragment
import com.fournineseven.dietstock.ui.rolemodel.RoleModelFragment
import com.fournineseven.dietstock.ui.food.FoodFragmentCamera

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var navView: BottomNavigationView
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewPager = binding.viewPager
        viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.registerOnPageChangeCallback(PageChangeCallback())

        navView = binding.navView

        navView.setOnNavigationItemSelectedListener { navigationSelected(it) }
    }

    override fun onBackPressed() {
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
                0-> HomeFragment()
                1-> RankingFragment()
                2-> NotificationsFragment()
                3-> FoodFragmentCamera()
                4-> RoleModelFragment()
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
}