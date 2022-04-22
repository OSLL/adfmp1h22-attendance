package com.example.attendingfix

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

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
        val personalImage = view.findViewById<ImageView>(R.id.profile_image)
        val editProfileButton: Button = view.findViewById(R.id.btn_editProfile)

        name.text = userInfo[2]
        surname.text = userInfo[1]
        parentName.text = userInfo[3]
        email.text = userInfo[4]
        phone.text = userInfo[5]

        val APP_PREFERENCES: String = "storeddata"
        val APP_PREFERENCES_PROFILE_IMAGE = "ProfileImage"

        val mSettings = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        try {
            val f = File(mSettings.getString(APP_PREFERENCES_PROFILE_IMAGE, null), "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            personalImage.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            //means there is no image selected yet
        }

        editProfileButton.setOnClickListener {
            val myActivity = activity as MainActivity
            myActivity.onEditProfileButtonClicked()
        }
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