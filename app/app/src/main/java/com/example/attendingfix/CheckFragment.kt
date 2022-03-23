package com.example.attendingfix

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.wait
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [CheckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val data = получение данных с сервера

        val currentView: View = requireView()
        val checkButton: Button = currentView.findViewById(R.id.mainCheckButton)

        fun toggleButton(){
            checkButton.isClickable = !checkButton.isClickable
        }

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        val myActivity = activity as MainActivity

        val adapter = CheckRecyclerAdapter(this){ toggleButton() }

        Log.d("Data", myActivity.userInfo[7])
        Log.d("Data", myActivity.userInfo[0])

        val request = Request.Builder().url("http://10.0.2.2:3001/lessons/${myActivity.userInfo[7]}/${myActivity.userInfo[0]}").build()
        val newData = mutableListOf<IRecyclerViewItemMapHandler>()
        val thread = Thread {
            run() {
                try {
                    val response: Response = myActivity.httpClient.newCall(request).execute()
                    val reqData = JSONObject(response.body!!.string())
                    if(reqData.get("status").toString() == "true") {
                        val obj = JSONArray(reqData.get("data").toString())
                        Log.d("response", "DONE")
                        for( i in 0 until obj.length()){
                            val lessonObj = obj.getJSONObject(i)
                            newData.add(IRecyclerViewItemMapHandler(lessonObj.get("id").toString(),
                                mapOf("lesson" to lessonObj.get("name").toString(),
                                      "date" to lessonObj.get("date").toString(),
                                      "time" to lessonObj.get("time").toString())))
                        }
                        myActivity.runOnUiThread {
                            val recyclerView: RecyclerView = currentView.findViewById(R.id.recyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(currentView.context)
                            adapter.setItems(newData)
                            recyclerView.adapter = adapter
                        }
                    } else {
                        Log.d("response", reqData.get("status").toString())
                        myActivity.runOnUiThread {
                            Toast.makeText(
                                myActivity,
                                reqData.get("status").toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("response", e.toString())
                    myActivity.runOnUiThread {
                        Toast.makeText(
                            myActivity,
                            "Error with request to server Check",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        thread.start()

        checkButton.setOnClickListener {
            val item = adapter.getSelectedItems()[0]
            myActivity.onCheckItemsClickHandler(adapter.getSelectedItems()) { adapter.remove(item) }
        }

        checkButton.isClickable = false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CheckFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CheckFragment()
    }
}