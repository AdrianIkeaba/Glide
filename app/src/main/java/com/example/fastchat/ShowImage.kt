package com.example.fastchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.fastchat.databinding.ActivityShowImageBinding

class ShowImage : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val image = binding.showImage

        val imageUrl = intent.getStringExtra("imageUrl")

        Glide.with(this)
            .load(imageUrl)
            .into(image)
    }
}