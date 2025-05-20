//package com.sfedu.scheduleapp.ui
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.viewpager2.adapter.FragmentStateAdapter
//
//class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
//
//    override fun getItemCount(): Int = 6
//
//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> Monday()
//            1 -> Tuesday()
//            2 -> Wednesday()
//            3 -> Thursday()
//            4 -> Friday()
//            5 -> Saturday()
//            else -> Monday()
//        }
//    }
//}