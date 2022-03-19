package com.example.attendingfix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentRegister.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentRegister : Fragment() {

    val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        val fio: EditText = view.findViewById(R.id.et_name)
        val email: EditText = view.findViewById(R.id.et_email)
        val telnum: EditText = view.findViewById(R.id.et_phone)
        val password: EditText = view.findViewById(R.id.et_password)
        val rePassword: EditText = view.findViewById(R.id.et_repassword)

        val registerButton: Button = view.findViewById(R.id.btn_register)
        registerButton.setOnClickListener {

            if(fio.text.toString().split(' ').size == 3 &&
                    email.text.toString().isNotEmpty() &&
                    telnum.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    password.text.toString() == rePassword.text.toString()){
                        val json = JSONObject()
                        json.put("firstname", fio.text.toString().split(' ')[1])
                            .put("lastname", fio.text.toString().split(' ')[0])
                            .put("secondname", fio.text.toString().split(' ')[2])
                            .put("email", email.text.toString())
                            .put("telnum", telnum.text.toString())
                            .put("password", password.text.toString())
                        fio.setText("")
                        email.setText("")
                        telnum.setText("")
                        password.setText("")
                        rePassword.setText("")
                        val reqBody = json.toString().toRequestBody(JSON)
                        val request = Request.Builder().url("http://10.0.2.2:3001/users/register").post(reqBody).build()
                        val thread = Thread {
                            run() {
                                try {
                                    val response: Response = httpClient.newCall(request).execute()
                                    val reqData = JSONObject(response.body!!.string())
                                    if(reqData.get("status").toString() == "true"){
                                        val obj = JSONObject(reqData.get("data").toString())
                                        Log.d("response", "DONE")
                                        val intent = Intent(context, MainActivity::class.java)
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
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(
                                                activity,
                                                reqData.get("status").toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.d("response", e.toString())
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "Error with request to server",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                        thread.start()
            } else {
                val toast = Toast.makeText(context, "Please enter correct data! You need 3 words in ФИО, word in email, number in Phone and two equal passwords.", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FragmentRegister.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) = FragmentRegister()
    }
}