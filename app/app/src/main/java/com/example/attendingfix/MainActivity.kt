package com.example.attendingfix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var current_fragment: Int = 2

    val httpClient = OkHttpClient()

    var userInfo = arrayListOf<String>()

    fun onEditProfileButtonClicked(){
        navBarButtonClickHandler(4, this.supportFragmentManager)
    }

    fun onSaveProfileDataButtonClicked(data: ArrayList<String>){

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        if(data[4].isNotEmpty() &&
                data[1].isNotEmpty() &&
                data[2].isNotEmpty() &&
                data[3].isNotEmpty() &&
                data[5].isNotEmpty()){
            val json = JSONObject()
            json.put("email", data[4])
                .put("firstname", data[2])
                .put("id", data[0])
                .put("lastname", data[1])
                .put("secondname", data[3])
                .put("telnum", data[5])
            val reqBody = json.toString().toRequestBody(JSON)
            val request = Request.Builder().url("http://10.0.2.2:3001/users/modify").post(reqBody).build()
            val thread = Thread {
                run() {
                    try {
                        val response: Response = httpClient.newCall(request).execute()
                        val code = response.code
                        if(code == 202) {
                            val reqData = JSONObject(response.body!!.string())
                            Log.d("response", "DONE")
                            val newData = arrayListOf(
                                reqData.get("id").toString(),
                                reqData.get("lastname").toString(),
                                reqData.get("firstname").toString(),
                                reqData.get("secondname").toString(),
                                reqData.get("email").toString(),
                                reqData.get("telnum").toString(),
                                reqData.get("status").toString()
                            )
                            userInfo = newData
                            navBarButtonClickHandler(3, this.supportFragmentManager)
                        } else {
                            Log.d("response", "response code: " + code)
                            this.runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Please modify with correct data!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("response", e.toString())
                        this.runOnUiThread {
                            Toast.makeText(
                                this,
                                "Please modify with correct data!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            thread.start()
        }
    }

    fun onCheckItemsClickHandler(l: List<IRecyclerViewItemMapHandler>){
        val lessonId = l[0].id
        val lessonData = l[0].data

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