package com.example.attendingfix

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.LocationServices
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var current_fragment: Int = 2

    val httpClient = OkHttpClient.Builder().retryOnConnectionFailure(true).build()

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
                        val reqData = JSONObject(response.body!!.string())
                        if(reqData.get("status").toString() == "true") {
                            val obj = JSONObject(reqData.get("data").toString())
                            Log.d("response", "DONE")
                            val newData = arrayListOf(
                                obj.get("id").toString(),
                                obj.get("surname").toString(),
                                obj.get("firstname").toString(),
                                obj.get("secondname").toString(),
                                obj.get("email").toString(),
                                obj.get("telnum").toString(),
                                obj.get("status").toString(),
                                obj.get("group").toString()
                            )
                            userInfo = newData
                            navBarButtonClickHandler(3, this.supportFragmentManager)
                        } else {
                            Log.d("response", reqData.get("status").toString())
                            this.runOnUiThread {
                                Toast.makeText(
                                    this,
                                    reqData.get("status").toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("response", e.toString())
                        this.runOnUiThread {
                            Toast.makeText(
                                this,
                                "Error with request to server\n" +
                                        " Error: $e",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            thread.start()
        }
    }

    fun onCheckItemsClickHandler(l: List<IRecyclerViewItemMapHandler>, callback: () -> Unit){
        val lessonId = l[0].id

        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
                    val json = JSONObject()
                        .put("positionX", "$latitude")
                        .put("positionY", "$longitude")

                    val reqBody = json.toString().toRequestBody(JSON)
                    val request = Request.Builder().url("http://10.0.2.2:3001/lessons/check/${lessonId}/${userInfo[0]}").post(reqBody).build()
                    val thread = Thread {
                        run() {
                            try {
                                val response: Response = httpClient.newCall(request).execute()
                                val reqData = JSONObject(response.body!!.string())
                                if(reqData.get("status").toString() == "true") {
                                    this.runOnUiThread{
                                        callback()
                                    }
                                } else {
                                    Log.d("response", reqData.get("status").toString())
                                    this.runOnUiThread {
                                        Toast.makeText(
                                            this,
                                            reqData.get("status").toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } catch (e: NetworkOnMainThreadException) {
                                Log.d("response", e.toString())
                                this.runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Error with request to server\n" +
                                                " Error: $e",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    thread.start()
                } else {
                    Toast.makeText(applicationContext, "Cannot get location", Toast.LENGTH_LONG)
                        .show()
                }
            }
                .addOnFailureListener { e ->
                    Log.d("LocationFetch", "Error trying to get last GPS location")
                    e.printStackTrace()
                }
        } catch (e: SecurityException) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
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

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            val photoURI = CropImage.getActivityResult(data).uri
            var bitmap: Bitmap? = null

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoURI)
            } catch (e: IOException) {
                Log.d("TAG ", "DIDN'T FOUND BY URI")
            }
            val cw = ContextWrapper(applicationContext)
            val directory: File = cw.getDir("imageDir", MODE_PRIVATE)
            val mypath = File(directory, "profile.jpg")

            var fos: FileOutputStream? = null
            try {
                Log.d("TAG", mypath.toString())
                fos = FileOutputStream(mypath)
                Log.d("TAG", fos.toString())
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                val APP_PREFERENCES: String = "storeddata"
                val APP_PREFERENCES_PROFILE_IMAGE = "ProfileImage"

                val mSettings = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = mSettings.edit()
                editor.putString(APP_PREFERENCES_PROFILE_IMAGE, directory.absolutePath)
                editor.apply()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                fos?.close()
            }
        }
    }
}