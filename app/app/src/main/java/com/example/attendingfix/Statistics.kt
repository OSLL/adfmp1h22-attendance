package com.example.attendingfix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [Statistics.newInstance] factory method to
 * create an instance of this fragment.
 */
class Statistics : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emulated_data: Map<String, List<IRecyclerViewItemMapHandler>> =
            mapOf("items" to listOf(
                IRecyclerViewItemMapHandler(0, mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "Was on lesson")),
                IRecyclerViewItemMapHandler(1 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "Not present")),
                IRecyclerViewItemMapHandler(2 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "Was on lesson")),
                IRecyclerViewItemMapHandler(3 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "Not present")),
                IRecyclerViewItemMapHandler(4 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "Was on lesson")),
                IRecyclerViewItemMapHandler(5 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "Not present")),
                IRecyclerViewItemMapHandler(6 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "Was on lesson")),
                IRecyclerViewItemMapHandler(7 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "Was on lesson")),
                IRecyclerViewItemMapHandler(8 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "Was on lesson"))
            ))

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
        adapter.setItems(requireContext(), emulated_data["items"] ?: listOf())
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