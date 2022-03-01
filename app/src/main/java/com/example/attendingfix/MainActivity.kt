package com.example.attendingfix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager


class MainActivity : AppCompatActivity() {

    var current_fragment: Int = 2

    private var userInfo = arrayListOf<String>()

    fun onEditProfileButtonClicked(){
        navBarButtonClickHandler(4, this.supportFragmentManager)
    }

    fun onSaveProfileDataButtonClicked(data: ArrayList<String>){
        userInfo = data
        navBarButtonClickHandler(3, this.supportFragmentManager)
    }

    fun navBarButtonClickHandler(target_id: Int, supportFragmentManager: FragmentManager){

        if (current_fragment != target_id) {
            current_fragment = target_id
            val screen = ScreenFragmentManager.newInstance(current_fragment)
            val bundle = Bundle()
            bundle.putStringArrayList("userInfo", userInfo)
            screen.arguments = bundle
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

        this.userInfo = intent?.getStringArrayListExtra("userInfo") as ArrayList<String>

        val btn_stats: Button = findViewById(R.id.stats)
        val btn_check: Button = findViewById(R.id.check)
        val btn_profile: Button = findViewById(R.id.profile)
        val btn_export: Button = findViewById(R.id.export_Button)
        val title_text: TextView = findViewById(R.id.header_title)
        val supportFragmentManager = this.supportFragmentManager

        supportFragmentManager.beginTransaction()
            .add(R.id.screen_fragment, ScreenFragmentManager.newInstance(current_fragment), "MainFragment")
            .commit()

        btn_export.isClickable = false
        title_text.text = getString(R.string.sign)

        btn_stats.setOnClickListener{
            btn_export.isClickable = true
            title_text.text = getString(R.string.stats)
            var target_id = 1
            if(userInfo[6] == "Teacher"){
                target_id = 5
            }
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }

        btn_check.setOnClickListener {
            val target_id = 2
            btn_export.isClickable = false
            title_text.text = getString(R.string.sign)
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }

        btn_profile.setOnClickListener {
            val target_id = 3
            btn_export.isClickable = false
            title_text.text = getString(R.string.profile)
            navBarButtonClickHandler(target_id, supportFragmentManager)
        }
    }
}