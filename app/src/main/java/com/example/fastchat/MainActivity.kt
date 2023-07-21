package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.fastchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myAdapter: Adapter
    lateinit var viewPager: ViewPager2
    lateinit var viewPagerText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.back)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewPager = findViewById(R.id.viewPager)
        myAdapter = Adapter(this)
        viewPagerText = binding.viewPagertext
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = myAdapter
        sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)


        val login: String? = sharedPreferences.getString("login1", "false")
        val create: String? = sharedPreferences.getString("create", "false")
        if (login.equals("true")) {
            val intent = Intent(applicationContext, Home::class.java)
            startActivityWithFadeInAnimation(intent)
            finish()
        } else if (create.equals("true")) {
            val intent = Intent(applicationContext, SetupProfile::class.java)
            startActivityWithFadeInAnimation(intent)
            finish()
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Call a method to update the text based on the current fragment
                updateText(position)
            }
        })


        binding.create.setOnClickListener {
            val intent = Intent(this, SignupPage::class.java)
            startActivity(intent)
        }
    }
    private fun updateText(position: Int) {
        when (position) {
            0 -> viewPagerText.text = "Secure messaging platform among peers."
            1 -> viewPagerText.text = "Encrypted messaging between you and others."
            2 -> viewPagerText.text = "A specialised platform 'Channels' that gives you the freedom to be productive as a team."
            // Add more cases for other fragments if needed
            else -> viewPagerText.text = "UNDEFINED"
        }
    }
    private fun startActivityWithFadeInAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_from_right, android.R.anim.fade_out)
    }
}