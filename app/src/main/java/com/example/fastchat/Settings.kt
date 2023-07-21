package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastchat.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var back: ImageView
    private lateinit var phoneNum: TextView
    private lateinit var userName: TextView
    private lateinit var userImage: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        back = binding.back
        phoneNum = binding.phoneNum
        userImage = binding.userImg
        userName = binding.nameUser

        auth = FirebaseAuth.getInstance()


        sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
        userName.text = sharedPreferences.getString("name", "UNDEFINED")
        phoneNum.text = sharedPreferences.getString("number", "UNDEFINED")
// Assuming you have an ImageView with the ID 'imageView' to display the selected image

        val sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString("selected_image_uri", null)

        if (uriString != null) {
            val uri = Uri.parse(uriString)
            binding.userImg.setImageURI(uri)
        }



        val adapter = SettingAdapter(this, settingsData())
        binding.listview.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.listview.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, id ->
            // Handle the item click event
            val selectedItem = parent.getItemAtPosition(position)
            if (position == 4) {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                clearImageAndPreference()
                auth.signOut()
                val intent = Intent(applicationContext, SignupPage::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
            }
        }

        back.setOnClickListener {
            val intent = Intent(applicationContext, Home::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun settingsData(): ArrayList<SettingModel> {
        val settings = ArrayList<SettingModel>()

        val setting1 = SettingModel(
            R.drawable.chats,
            "Chats"
        )

        val setting2 = SettingModel(
            R.drawable.notification,
            "Notifications"
        )

        val setting3 = SettingModel(
            R.drawable.help,
        "Help"
        )

        val setting4 = SettingModel(
            R.drawable.invite,
        "Invite a friend"
        )
        val setting5 = SettingModel(
            R.drawable.logout,
        "Log out"
        )

        settings.add(setting1)
        settings.add(setting2)
        settings.add(setting3)
        settings.add(setting4)
        settings.add(setting5)

        return settings
    }

    private fun clearImageAndPreference() {
        val sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("userImg")
        editor.apply()

        val imagePath = sharedPreferences.getString("userImg", "")
        if (imagePath != null) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                if (!deleted) {
                    // Failed to delete the image file
                    // Log or display an error message here if needed
                }
            }
        }
    }
}