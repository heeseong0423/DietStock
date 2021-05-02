package com.fournineseven.dietstock.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.databinding.FragmentHomeBinding
import com.fournineseven.dietstock.room.KcalDatabase

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val button: Button = root.findViewById(R.id.button2)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val db = Room.databaseBuilder(
                root.context,
                KcalDatabase::class.java, "database-name"
        ).allowMainThreadQueries()
                .build()
        val userDao = db.kcalDao()


        button.setOnClickListener {
            Log.d("MyTag","hello")
            userDao.deleteAllUsers()
        }
        return root
    }
}