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
 * Use the [TeacherSttisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TeacherStatisticsFragment : Fragment() {

    private var isLessons = true
    private var adapter_lessons: StatisticRecyclerAdapter? = null
    private var adapter_students: StatisticRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emulated_data_Lessons: Map<String, List<IRecyclerViewItemMapHandler>> =
            mapOf("items" to listOf(
                IRecyclerViewItemMapHandler(0, mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "34", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(1 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "35", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(2 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "32", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(3 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "15", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(4 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "36", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(5 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "35", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(6 ,mapOf("lesson" to "Android", "date" to "02.02.2022", "attendance" to "39", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(7 ,mapOf("lesson" to "Math", "date" to "02.02.2022", "attendance" to "37", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(8 ,mapOf("lesson" to "Haskell", "date" to "02.02.2022", "attendance" to "34", "attendanceOutOf" to "40"))
            ))

        val emulated_data_Students: Map<String, List<IRecyclerViewItemMapHandler>> =
            mapOf("items" to listOf(
                IRecyclerViewItemMapHandler(0, mapOf("lesson" to "Android", "fullname" to "Shirokov", "attendance" to "34", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(1 ,mapOf("lesson" to "Math", "fullname" to "Shirikov", "attendance" to "35", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(2 ,mapOf("lesson" to "Haskell", "fullname" to "Shirokov Kirill", "attendance" to "32", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(3 ,mapOf("lesson" to "Android", "fullname" to "Kirill", "attendance" to "15", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(4 ,mapOf("lesson" to "Math", "fullname" to "Petya", "attendance" to "36", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(5 ,mapOf("lesson" to "Haskell", "fullname" to "Vova", "attendance" to "35", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(6 ,mapOf("lesson" to "Android", "fullname" to "Sasha", "attendance" to "39", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(7 ,mapOf("lesson" to "Math", "fullname" to "Shirokov2.0", "attendance" to "37", "attendanceOutOf" to "40")),
                IRecyclerViewItemMapHandler(8 ,mapOf("lesson" to "Haskell", "fullname" to "Shirokov Vova", "attendance" to "34", "attendanceOutOf" to "40"))
            ))

        val currentView: View = requireView()

        //функции-помощники
        fun filterRuleLessons(item: IRecyclerViewItemMapHandler, str: String): Boolean {
            return item.data["lesson"]?.lowercase()?.contains(str.lowercase()) ?: false
        }

        fun filterRuleStudents(item: IRecyclerViewItemMapHandler, str: String): Boolean {
            return item.data["fullname"]?.lowercase()?.contains(str.lowercase()) ?: false
        }

        fun lessonsBindFunction(binding: ViewDataBinding,
                                item: IRecyclerViewItemMapHandler,
                                text1: TextView,
                                text2: TextView,
                                text3: TextView){
            binding.apply {
                text1.text = item.data["lesson"]
                text2.text = item.data["date"]
                text3.text = """${item.data["attendance"]} out of ${item.data["attendanceOutOf"]}"""
            }
        }

        fun studentsBindFunction(binding: ViewDataBinding,
                                item: IRecyclerViewItemMapHandler,
                                text1: TextView,
                                text2: TextView,
                                text3: TextView){
            binding.apply {
                text1.text = item.data["fullname"]
                text2.text = item.data["lesson"]
                text3.text = """${item.data["attendance"]} out of ${item.data["attendanceOutOf"]}"""
            }
        }

        fun getAdapter(): StatisticRecyclerAdapter? {
            if(isLessons){
                return adapter_lessons
            } else {
                return adapter_students
            }
        }

        val recyclerView: RecyclerView = currentView.findViewById(R.id.stat_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(currentView.context)

        //создание адаптеров
        adapter_lessons = StatisticRecyclerAdapter(this,
            {item, str -> filterRuleLessons(item, str)},
            {binding, item, text1, text2, text3 -> lessonsBindFunction(binding, item, text1, text2, text3)},
            R.layout.fragment_statistic_view_student_lesson_item)
        adapter_lessons?.setItems(requireContext(), emulated_data_Lessons["items"] ?: listOf())

        adapter_students = StatisticRecyclerAdapter(this,
            {item, str -> filterRuleStudents(item, str)},
            {binding, item, text1, text2, text3 -> studentsBindFunction(binding, item, text1, text2, text3)},
            R.layout.fragment_statistic_view_student_lesson_item)
        adapter_students?.setItems(requireContext(), emulated_data_Students["items"] ?: listOf())

        //прикрепление базового адаптера
        recyclerView.adapter = adapter_lessons

        //про фильтрацию
        fun handleOnTextChange(text: CharSequence?, start: Int, before: Int, count: Int){
            var fil = getAdapter()!!.getFilter()
            return fil!!.filter(text)
        }

        val searchEditText: EditText = currentView.findViewById(R.id.statsTeacherFilter_editText)
        searchEditText.doOnTextChanged { text, start, before, count -> handleOnTextChange(text, start, before, count) }

        //кнопки
        val studentsButton: Button = currentView.findViewById(R.id.studentsStatsButton)
        val lessonsButton: Button = currentView.findViewById(R.id.lessonsStatsButton)

        studentsButton.setOnClickListener {
            recyclerView.adapter = adapter_students
            isLessons = false
        }

        lessonsButton.setOnClickListener {
            recyclerView.adapter = adapter_lessons
            isLessons = true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TeacherSttisticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = TeacherStatisticsFragment()
    }
}