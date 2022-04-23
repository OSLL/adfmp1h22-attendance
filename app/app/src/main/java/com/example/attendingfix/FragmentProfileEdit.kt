package com.example.attendingfix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentProfileEdit.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentProfileEdit : Fragment() {

    var userInfo: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = this.arguments?.getStringArrayList("userInfo") as ArrayList<String>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name: EditText = view.findViewById(R.id.nameValue_edit)
        val surname: EditText = view.findViewById(R.id.surnameValue_edit)
        val parentName: EditText = view.findViewById(R.id.parentNameValue_edit)
        val email: EditText = view.findViewById(R.id.emailValue_edit)
        val phone: EditText = view.findViewById(R.id.phoneValue_edit)
        val saveProfileButton: Button = view.findViewById(R.id.btn_saveProfile)

        name.setText(userInfo[2])
        surname.setText(userInfo[1])
        parentName.setText(userInfo[3])
        email.setText(userInfo[4])
        phone.setText(userInfo[5])

        saveProfileButton.setOnClickListener {
            val myActivity = activity as MainActivity
            myActivity.onSaveProfileDataButtonClicked(arrayListOf(
                userInfo[0],
                surname.text.toString(),
                name.text.toString(),
                parentName.text.toString(),
                email.text.toString(),
                phone.text.toString()
            ))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FragmentProfileEdit.
         */
        fun newInstance() = FragmentProfileEdit()
    }
}