package com.example.attendingfix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val emulated_data: Map<String, List<IRecyclerViewItemMapHandler>> =
            mapOf("items" to listOf(
                IRecyclerViewItemMapHandler(0, mapOf("lesson" to "Android", "date" to "02.02.2022", "time" to "14:20")),
                IRecyclerViewItemMapHandler(1 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "time" to "16:00")),
                IRecyclerViewItemMapHandler(2 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "time" to "17:55")),
                IRecyclerViewItemMapHandler(3 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "time" to "14:20")),
                IRecyclerViewItemMapHandler(4 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "time" to "16:00")),
                IRecyclerViewItemMapHandler(5 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "time" to "17:55")),
                IRecyclerViewItemMapHandler(6 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "time" to "14:20")),
                IRecyclerViewItemMapHandler(7 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "time" to "16:00")),
                IRecyclerViewItemMapHandler(8 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "time" to "17:55"))
            ))
        val fragmentManager: FragmentManager = this.childFragmentManager
        val currentView: View = requireView()
        val scrollView: ScrollView = currentView.findViewById(R.id.checkScrollView)
        val checkButton: Button = currentView.findViewById(R.id.mainCheckButton)
        checkButton.isClickable = false
        fun itemClickHandler(item: IRecyclerViewItemMapHandler){

        }

        val recyclerView: RecyclerView = currentView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(currentView.context)
        val adapter = CheckRecyclerAdapter(this) { item -> itemClickHandler(item) }
        adapter.setItems(emulated_data["items"] ?: listOf())
        recyclerView.adapter = adapter
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