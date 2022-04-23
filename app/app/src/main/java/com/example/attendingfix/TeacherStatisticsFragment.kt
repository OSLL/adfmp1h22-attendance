package com.example.attendingfix

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

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

    private fun loadDataLessons() {

        val request = Request.Builder().url("http://10.0.2.2:3001/stats/teacher/lessons/${(requireActivity() as MainActivity).userInfo[0]}").get().build()
        val newData = mutableListOf<IRecyclerViewItemMapHandler>()
        val thread = Thread {
            run() {
                try {
                    val response: Response = (requireActivity() as MainActivity).httpClient.newCall(request).execute()
                    val reqData = JSONObject(response.body!!.string())
                    val lessons = reqData.getJSONArray("lessons")
                    for (i in 0..lessons.length() - 1){
                        val lesson = lessons.getJSONObject(i)
                        newData.add(IRecyclerViewItemMapHandler(
                            lesson.getString("id"),
                            mapOf(
                                "lesson" to lesson.getString("name"),
                                "date" to lesson.getString("date"),
                                "attendance" to lesson.getString("numberOfVisits"),
                                "extras" to lesson.getString("extras")
                            )
                        ))
                    }
                    requireActivity().runOnUiThread{
                        adapter_lessons?.setItems(requireContext(), newData)
                        adapter_lessons?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.d("response", e.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error with request to server Check",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        thread.start()
    }

    private fun loadDataStudents() {

        val request = Request.Builder().url("http://10.0.2.2:3001/stats/teacher/students/${(requireActivity() as MainActivity).userInfo[0]}").get().build()
        val newData = mutableListOf<IRecyclerViewItemMapHandler>()
        val thread = Thread {
            run() {
                try {
                    val response: Response = (requireActivity() as MainActivity).httpClient.newCall(request).execute()
                    val reqData = JSONObject(response.body!!.string())
                    val items = reqData.getJSONArray("items")
                    for (i in 0..items.length() - 1){
                        val item = items.getJSONObject(i)
                        newData.add(IRecyclerViewItemMapHandler(
                            "${i}",
                            mapOf(
                                "lesson" to item.getString("lesson"),
                                "fullname" to item.getString("fullname"),
                                "attendance" to item.getString("numberOfVisits"),
                                "extras" to item.getString("extras")
                            )
                        ))
                    }
                    requireActivity().runOnUiThread{
                        adapter_students?.setItems(requireContext(), newData)
                        adapter_students?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.d("response", e.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error with request to server Check",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        thread.start()
    }

    private fun convertToPDF(){
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val filePath = Environment.getExternalStorageDirectory().toString() + """/${mFileName}.pdf"""
        if(isLessons) {
            try{
                val items = adapter_lessons?.getSelectedItems()
                if(items === null) {
                    Toast.makeText(requireContext(), "Sorry, no items found", Toast.LENGTH_LONG).show()
                    return
                }
                if(items?.size === null || items?.size === 0){
                    Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_LONG).show()
                    return
                }

                PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
                mDoc.open()

                var data = ""

                for (item in items){
                    data += """${item.data["lesson"]} (${item.data["date"]}): ${item.data["extras"]}"""
                    data += "\n"
                }

                mDoc.addAuthor("Attendance Fix App (autogenerated)")
                mDoc.add(Paragraph(data.trim()))
                mDoc.close()

                Toast.makeText(requireContext(), """Exported to file ${filePath}!""", Toast.LENGTH_LONG).show()

            } catch (e: Exception){
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            try {
                val items = adapter_students?.getSelectedItems()
                if(items === null){
                    Toast.makeText(requireContext(), "Sorry, no items found", Toast.LENGTH_LONG).show()
                    return
                }
                if(items?.size === null || items?.size === 0) {
                    Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_LONG).show()
                    return
                }

                PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
                mDoc.open()

                var data = ""

                for (item in items){
                    data += """${item.data["fullname"]} (${item.data["lesson"]}): ${item.data["extras"]}"""
                    data += "\n"
                }

                mDoc.addAuthor("Attendance Fix App (autogenerated)")
                mDoc.add(Paragraph(data.trim()))
                mDoc.close()

                Toast.makeText(requireContext(), """Exported to file ${filePath}!""", Toast.LENGTH_LONG).show()

            } catch (e: Exception){
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentView: View = requireView()

        fun lessonsBindFunction(binding: ViewDataBinding,
                                item: IRecyclerViewItemMapHandler,
                                text1: TextView,
                                text2: TextView,
                                text3: TextView){
            binding.apply {
                text1.text = item.data["lesson"]
                text2.text = item.data["date"]
                text3.text = """${item.data["attendance"]}"""
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
                text3.text = """${item.data["attendance"]}"""
            }
        }

        //функции-помощники
        fun filterRuleLessons(item: IRecyclerViewItemMapHandler, str: String): Boolean {
            return item.data["lesson"]?.lowercase()?.contains(str.lowercase()) ?: false
        }

        fun filterRuleStudents(item: IRecyclerViewItemMapHandler, str: String): Boolean {
            return item.data["fullname"]?.lowercase()?.contains(str.lowercase()) ?: false
        }

        //создание адаптеров
        adapter_lessons = StatisticRecyclerAdapter(this,
            {item, str -> filterRuleLessons(item, str)},
            {binding, item, text1, text2, text3 -> lessonsBindFunction(binding, item, text1, text2, text3)},
            R.layout.fragment_statistic_view_student_lesson_item)

        adapter_students = StatisticRecyclerAdapter(this,
            {item, str -> filterRuleStudents(item, str)},
            {binding, item, text1, text2, text3 -> studentsBindFunction(binding, item, text1, text2, text3)},
            R.layout.fragment_statistic_view_student_lesson_item)

        loadDataLessons()
        loadDataStudents()

        val export_btn = requireActivity().findViewById<Button>(R.id.export_Button)

        export_btn.setOnClickListener {
            val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if(result == PackageManager.PERMISSION_GRANTED){
                convertToPDF()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            }
        }

        val recyclerView: RecyclerView = currentView.findViewById(R.id.stat_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(currentView.context)

        //прикрепление базового адаптера
        recyclerView.adapter = adapter_lessons

        fun getAdapter(): StatisticRecyclerAdapter? {
            if(isLessons){
                return adapter_lessons
            } else {
                return adapter_students
            }
        }

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
        val takeAllButton: Button = currentView.findViewById(R.id.take_all_button)

        studentsButton.setOnClickListener {
            recyclerView.adapter = adapter_students
            isLessons = false
        }

        lessonsButton.setOnClickListener {
            recyclerView.adapter = adapter_lessons
            isLessons = true
        }

        takeAllButton.setOnClickListener {
            if((it as Button).text == "Отмена"){
                it.setText("Взять всё")
            } else {
                it.setText("Отмена")
            }
            if(isLessons){
                adapter_lessons?.selectAll()
            } else {
                adapter_students?.selectAll()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            2 -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    convertToPDF()
                } else {
                    Toast.makeText(requireContext(), "You haven't granted the permission!", Toast.LENGTH_LONG).show()
                }
            }
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