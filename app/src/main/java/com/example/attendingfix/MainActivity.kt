package com.example.attendingfix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentManager


class MainActivity : AppCompatActivity() {

    var current_fragment: Int = 2

    fun navBarButtonClickHandler(target_id: Int, supportFragmentManager: FragmentManager){

        if (current_fragment != target_id) {
            current_fragment = target_id
            val screen = ScreenFragmentManager.newInstance(current_fragment)
            val fragment_to_remove = supportFragmentManager.findFragmentByTag("MainFragment")
            if(fragment_to_remove != null){
                supportFragmentManager.beginTransaction()
                    .remove(fragment_to_remove)
                    .add(R.id.screen_fragment, screen, "MainFragment")
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(screen, "MainFragment")
                    .commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_stats: Button = findViewById(R.id.stats)
        val btn_check: Button = findViewById(R.id.check)
        val btn_profile: Button = findViewById(R.id.profile)
        val supportFragmentManager = this.supportFragmentManager

        supportFragmentManager.beginTransaction()
            .add(R.id.screen_fragment, ScreenFragmentManager.newInstance(current_fragment), "MainFragment")
            .commit()

        btn_stats.setOnClickListener{
            val target_id = 1
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }

        btn_check.setOnClickListener {
            val target_id = 2
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }

        btn_profile.setOnClickListener {
            val target_id = 3
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }
    }
}