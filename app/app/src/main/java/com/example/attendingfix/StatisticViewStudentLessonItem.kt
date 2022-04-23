package com.example.attendingfix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 * Use the [StatisticViewStudentLessonItem.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticViewStudentLessonItem : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistic_view_student_lesson_item, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StatisticViewStudentLessonItem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = StatisticViewStudentLessonItem()
    }
}