package com.example.attendingfix

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [Statistics.newInstance] factory method to
 * create an instance of this fragment.
 */
class Statistics : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val httpClient = OkHttpClient.Builder().retryOnConnectionFailure(true).build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val request = Request.Builder().url("http://10.0.2.2:3001/stats/${(requireActivity() as MainActivity).userInfo[0]}").get().build()
        val thread = Thread {
            run() {
                try {
                    val response: Response = httpClient.newCall(request).execute()
                    val reqData = JSONObject(response.body!!.string())
                    Log.d("TAG", reqData.get("items").toString())
                    val objects = JSONArray(reqData.get("items").toString())
                    Log.d("response", "DONE")
                    val items: MutableList<IRecyclerViewItemMapHandler> = mutableListOf()
                    for(i in 0..(objects.length() - 1)){
                        val obj = objects.getJSONObject(i)
                        items.add(IRecyclerViewItemMapHandler(obj.getString("id"),
                            mapOf(
                            "lesson" to obj.getString("lesson"),
                            "date" to obj.getString("date"),
                            "attendance" to obj.getString("attendance")
                            )
                        ))
                    }
                    requireActivity().runOnUiThread {
                        val currentView: View = requireView()
                        val recyclerView: RecyclerView = currentView.findViewById(R.id.stat_recyclerView)
                        val adapter = (recyclerView.adapter as StatisticRecyclerAdapter)
                        adapter.setItems(requireContext(), items)
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.d("response", e.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error with request to server\n Error: $e",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        thread.start()

        val currentView: View = requireView()

        fun lessonsBindFunction(binding: ViewDataBinding,
                                item: IRecyclerViewItemMapHandler,
                                text1: TextView,
                                text2: TextView,
                                text3: TextView
        ){
            binding.apply {
                text1.text = item.data["lesson"]
                text2.text = item.data["date"]
                text3.text = item.data["attendance"]
            }
        }

        fun filterRule(item: IRecyclerViewItemMapHandler, str: String): Boolean {
            return item.data["lesson"]?.lowercase()?.contains(str.lowercase()) ?: false
        }

        val recyclerView: RecyclerView = currentView.findViewById(R.id.stat_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(currentView.context)
        val adapter = StatisticRecyclerAdapter(this,
            {item, str -> filterRule(item, str)},
            {binding, item, text1, text2, text3 -> lessonsBindFunction(binding, item, text1, text2, text3)},
            R.layout.fragment_statistic_view_student_lesson_item)
        recyclerView.adapter = adapter

        fun handleOnTextChange(text: CharSequence?, start: Int, before: Int, count: Int){
            var fil = adapter.getFilter()
            return fil!!.filter(text)
        }

        val searchEditText: EditText = currentView.findViewById(R.id.filter_editText)
        searchEditText.doOnTextChanged { text, start, before, count -> handleOnTextChange(text, start, before, count) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Statistics.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = Statistics()
    }
}