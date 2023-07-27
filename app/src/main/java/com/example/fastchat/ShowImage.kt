package com.example.fastchat

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.fastchat.databinding.ActivityShowImageBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ShowImage : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private var loadedBitmap: Bitmap? = null
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

        binding.back2.setOnClickListener {
            finish()
        }

        binding.download.setOnClickListener {
            downloadImageToGallery(imageUrl.toString())
        }

        binding.share.setOnClickListener {
                shareImageFromGallery()
        }
    }
    private fun downloadImageToGallery(imageUrl: String) {

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    loadedBitmap = resource
                    saveImageToGallery(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Toast.makeText(applicationContext, "Failed to download image.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"

        val imageOutStream: OutputStream? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(imageUri!!)
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(imagesDir, filename)
            FileOutputStream(imageFile)
        }

        if (imageOutStream != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
            imageOutStream.close()
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun shareImageFromGallery() {
        val drawable = binding.showImage.drawable
        if (drawable != null && drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"

            // Save the image to a temporary file to be shared
            val tempFile = File.createTempFile("shared_image", ".jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            // Attach the image URI to the intent
            val imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", tempFile)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

            // Start the share activity
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } else {
            Toast.makeText(this, "Image is not loaded yet.", Toast.LENGTH_SHORT).show()
        }
    }

}