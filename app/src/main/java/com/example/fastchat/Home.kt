package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.fastchat.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var imageSpin: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.back)

        bottomNav = binding.bottomNavigation
        bottomNav.menu.findItem(R.id.page_1)?.setIcon(R.drawable.chat_fill)
        viewPager = binding.viewPager2
        homeAdapter = HomeAdapter(this)
        imageSpin = binding.imageView5

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = homeAdapter

        sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("login1", "true")
        editor.apply()


        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    // Handle chat item click
                    viewPager.currentItem = 0
                    item.setIcon(R.drawable.chat_fill)
                    bottomNav.menu.findItem(R.id.page_3)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_3)?.setIcon(R.drawable.call)
                    bottomNav.menu.findItem(R.id.page_2)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_2)?.setIcon(R.drawable.channel)
                    true
                }

                R.id.page_2 -> {
                    // Handle calls item click
                    viewPager.currentItem = 1
                    bottomNav.menu.findItem(R.id.page_1)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_1)?.setIcon(R.drawable.chat)
                    bottomNav.menu.findItem(R.id.page_3)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_3)?.setIcon(R.drawable.call)
                    item.setIcon(R.drawable.channel_fill)

                    true
                }

                R.id.page_3 -> {
                    viewPager.currentItem = 2
                    item.setIcon(R.drawable.call_fill)
                    bottomNav.menu.findItem(R.id.page_1)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_1)?.setIcon(R.drawable.chat)
                    bottomNav.menu.findItem(R.id.page_2)?.isChecked = false
                    bottomNav.menu.findItem(R.id.page_2)?.setIcon(R.drawable.channel)
                    true
                }

                else -> false
            }
        }
        // Add a page change listener to the ViewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Update the selected item in the BottomNavigationView based on the current fragment
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.page_1
                    1 -> bottomNav.selectedItemId = R.id.page_2
                    2 -> bottomNav.selectedItemId = R.id.page_3
                    // Handle other fragments as needed
                }
            }

        })

        imageSpin.setOnClickListener {
            showPopupMenu(imageSpin)
        }
    }
    private fun showPopupMenu(view: View) {

        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                        R.id.option1 -> {
                    // Handle option 1 selection
                    true
                }
                R.id.option2 -> {
                    // Handle option 2 selection
                    true
                }
                R.id.option3 -> {
                    // Handle option 3 selection
                    true
                }
                R.id.option4 -> {
                    //Handle option4 selection
                    val intent = Intent(applicationContext, Settings::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }
}