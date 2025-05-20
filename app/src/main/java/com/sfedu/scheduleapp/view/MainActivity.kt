package com.sfedu.scheduleapp.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayoutMediator
//import com.sfedu.scheduleapp.R
//import com.sfedu.scheduleapp.mvp.DBHelper
//
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var viewPager: ViewPager2
//    private lateinit var tabLayout: TabLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val dbHelper = DBHelper(this)
//
//        dbHelper.checkForUpdates()
//
//        val groupNames = dbHelper.getColumns()
//        val spinner = findViewById<Spinner>(R.id.spinner)
//
//        val adapterArr = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            groupNames
//        )
//
//        adapterArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        // Применяем адаптер к Spinner
//        spinner.adapter = adapterArr
//
//        viewPager = findViewById(R.id.viewPager)
//        tabLayout = findViewById(R.id.tab_layout)
//
//        // Настройка адаптера
//        val adapter = ViewPagerAdapter(this)
//        viewPager.adapter = adapter
//
//        // Связывание TabLayout с ViewPager2
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> "Пн"
//                1 -> "Вт"
//                2 -> "Ср"
//                3 -> "Чт"
//                4 -> "Пт"
//                5 -> "Сб"
//                else -> "Пн"
//            }
//        }.attach()
//    }
//}