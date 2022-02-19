package com.example.attendingfix

import androidx.fragment.app.Fragment

class ScreenFragmentManager {

    companion object {

        fun newInstance(page: Int) : Fragment{
            return when(page){
                1 -> {
                    Statistics()
                }
                2 -> {
                    CheckFragment()
                }
                3 -> {
                    ProfileFragment()
                }
                else -> {
                    ProfileFragment()
                }
            }
        }
    }
}