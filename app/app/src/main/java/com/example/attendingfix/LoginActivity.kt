package com.example.attendingfix

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject


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

    val httpClient = OkHttpClient.Builder().retryOnConnectionFailure(true).build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        val pagerAdapter = AuthenticationPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(FragmentLogin())
        pagerAdapter.addFragment(FragmentRegister())
        viewPager.adapter = pagerAdapter

        val APP_PREFERENCES: String = "storeddata"
        val APP_PREFERENCES_LOGIN = "Login"
        val APP_PREFERENCES_PASSWORD = "Password"

        val mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if(mSettings.contains(APP_PREFERENCES_LOGIN) && mSettings.contains(APP_PREFERENCES_PASSWORD)) {
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val json = JSONObject()
            json.put("email", mSettings.getString(APP_PREFERENCES_LOGIN, ""))
                .put("password", mSettings.getString(APP_PREFERENCES_PASSWORD, ""))
            val reqBody = json.toString().toRequestBody(JSON)
            val request = Request.Builder().url("http://10.0.2.2:3001/users/login").post(reqBody).build()
            val thread = Thread {
                run() {
                    try {
                        val response: Response = httpClient.newCall(request).execute()
                        val reqData = JSONObject(response.body!!.string())
                        if(reqData.get("status").toString() == "true") {
                            val obj = JSONObject(reqData.get("data").toString())
                            Log.d("response", "DONE")
                            val intent = Intent(this, MainActivity::class.java)
                            val data = arrayListOf(
                                obj.get("id"),
                                obj.get("lastname"),
                                obj.get("firstname"),
                                obj.get("secondname"),
                                obj.get("email"),
                                obj.get("telnum"),
                                obj.get("status"),
                                obj.get("group")
                            )
                            intent.putExtra("userInfo", data)
                            startActivity(intent)
                        } else {
                            Log.d("response", reqData.get("status").toString())
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    reqData.get("status").toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("response", e.toString())
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Error with request to server\n Error: $e",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            thread.start()
        }

    }
}