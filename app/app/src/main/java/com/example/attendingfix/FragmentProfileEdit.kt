package com.example.attendingfix

import android.R.attr.path
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


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

    private fun  changePhotoUser(){
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name: EditText = view.findViewById(R.id.nameValue_edit)
        val surname: EditText = view.findViewById(R.id.surnameValue_edit)
        val parentName: EditText = view.findViewById(R.id.parentNameValue_edit)
        val email: TextView = view.findViewById(R.id.emailValue_edit)
        val phone: EditText = view.findViewById(R.id.phoneValue_edit)
        val personalImage = view.findViewById<ImageView>(R.id.profile_image)
        val saveProfileButton: Button = view.findViewById(R.id.btn_saveProfile)

        personalImage.setOnClickListener {
            changePhotoUser()
        }

        val APP_PREFERENCES: String = "storeddata"
        val APP_PREFERENCES_PROFILE_IMAGE = "ProfileImage"

        val mSettings = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        try {
            val f = File(mSettings.getString(APP_PREFERENCES_PROFILE_IMAGE, null), "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            Log.d("TAG", "Hello!")
            personalImage.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            //means there is no image selected yet
        }

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