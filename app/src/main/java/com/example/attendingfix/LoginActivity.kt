package com.example.attendingfix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


class LoginActivity : AppCompatActivity() {

    inner class AuthenticationPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragmentList: ArrayList<Fragment> = ArrayList()

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        val pagerAdapter = AuthenticationPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(FragmentLogin())
        pagerAdapter.addFragment(FragmentRegister())
        viewPager.adapter = pagerAdapter


    }
}