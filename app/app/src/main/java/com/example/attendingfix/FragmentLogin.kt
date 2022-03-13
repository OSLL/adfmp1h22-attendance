package com.example.attendingfix

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentLogin.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentLogin : Fragment() {

    val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        val email: EditText = view.findViewById(R.id.et_email)
        val password: EditText = view.findViewById(R.id.et_password)

        val loginButton: Button = view.findViewById(R.id.btn_login)
        loginButton.setOnClickListener {

            if(email.text.toString().isNotEmpty() &&
                password.text.toString().isNotEmpty()){
                val json = JSONObject()
                json.put("email", email.text.toString())
                    .put("password", password.text.toString())
                email.setText("")
                password.setText("")
                val reqBody = json.toString().toRequestBody(JSON)
                val request = Request.Builder().url("http://10.0.2.2:3001/users/login").post(reqBody).build()
                val thread = Thread {
                    run() {
                        try {
                            val response: Response = httpClient.newCall(request).execute()
                            val code = response.code
                            if(code == 200) {
                                val reqData = JSONObject(response.body!!.string())
                                Log.d("response", "DONE")
                                val intent = Intent(context, MainActivity::class.java)
                                val data = arrayListOf(
                                    reqData.get("id"),
                                    reqData.get("lastname"),
                                    reqData.get("firstname"),
                                    reqData.get("secondname"),
                                    reqData.get("email"),
                                    reqData.get("telnum"),
                                    reqData.get("status")
                                )
                                intent.putExtra("userInfo", data)
                                startActivity(intent)
                            } else {
                                Log.d("response", "response code: " + code)
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        "Please enter correct data!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("response", e.toString())
                            requireActivity().runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Please enter correct data!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                thread.start()
            } else {
                val toast = Toast.makeText(context, "Please enter correct data!", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FragmentLogin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = FragmentLogin()
    }
}