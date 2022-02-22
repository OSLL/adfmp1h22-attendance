package com.example.attendingfix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 * Use the [CheckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var userInfo: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = this.arguments?.getStringArrayList("userInfo") as ArrayList<String>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name: TextView = view.findViewById(R.id.nameValue)
        val surname: TextView = view.findViewById(R.id.surnameValue)
        val parentName: TextView = view.findViewById(R.id.parentNameValue)
        val email: TextView = view.findViewById(R.id.emailValue)
        val phone: TextView = view.findViewById(R.id.phoneValue)

        name.text = userInfo[2]
        surname.text = userInfo[1]
        parentName.text = userInfo[3]
        email.text = userInfo[4]
        phone.text = userInfo[5]
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}