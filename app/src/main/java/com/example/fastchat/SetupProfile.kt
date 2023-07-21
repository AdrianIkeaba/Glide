package com.example.fastchat


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fastchat.databinding.ActivitySetupProfileBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SetupProfile : AppCompatActivity() {
    private lateinit var findoutmore: TextView
    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var continueFab: FloatingActionButton
    private lateinit var rootView: View
    private lateinit var userImage: ImageView
    private var PICK_IMAGE_REQUEST_CODE = 101
    private lateinit var userName: TextInputEditText
    private lateinit var aboutMe: TextInputEditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {}
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var imageUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.back)


        findoutmore = binding.findMore
        continueFab = binding.continueFabProfile
        userImage = binding.userImage
        userName = binding.userNameTxt
        aboutMe = binding.aboutMe
        progressBar = binding.progressBar

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("create", "true")
        editor.apply()


        val resendText = "Your profile is visible to everyone including channels and groups on Glide. Find out more."
        val spannableString = SpannableString(resendText)
        val start = resendText.indexOf("Find out more")
        val end = start + "Find out more".length
        val colorSpan = ForegroundColorSpan(getColor(R.color.lightBlue))
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        findoutmore.text = spannableString

        FirebaseApp.initializeApp(this)



        continueFab.setOnClickListener {
            if (userName.text?.isEmpty() == true) {
                userName.error = "Please input a name"
            } else {
                if (selectedImageUri != null) {
                    showProgress()
                    val uid = auth.uid
                    val name: String = userName.text.toString()
                    val number = sharedPreferences.getString("number", "UNDEFINED")
                    val user = UserModel(uid, name, number, imageUrl)
                    database.reference
                        .child("users")
                        .child(uid!!)
                        .setValue(user)
                        .addOnCompleteListener {
                            hideProgress()
                            val intent = Intent(applicationContext, Home::class.java)
                            editor.putString("name", userName.text.toString())
                            editor.apply()
                            val bitmap = (binding.userImage.drawable as? BitmapDrawable)?.bitmap // Get the bitmap from the ImageView
                            val uri = saveImageToInternalStorage(bitmap) // Save the image and get its URI as a string

                            val sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("selected_image_uri", uri)
                            editor.apply()
                            startActivity(intent)
                            finish()
                        }
                } else {
                    val uid = auth.uid
                    val phone = auth.currentUser!!.phoneNumber
                    val name: String = userName.text.toString()
                    val user = UserModel(uid, name, phone, "No image")
                    database.reference
                        .child("users")
                        .child(uid!!)
                        .setValue(user)
                        .addOnCompleteListener {
                            hideProgress()
                            val intent = Intent(applicationContext, Home::class.java)
                            editor.putString("name", userName.text.toString())
                            editor.apply()
                            startActivity(intent)
                            finish()
                        }
                }
            }
        }


        //Change color of edittext depending on the UI
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userName.setTextColor(ContextCompat.getColor(this, R.color.white))
                aboutMe.setTextColor(ContextCompat.getColor(this, R.color.white))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                userName.setTextColor(ContextCompat.getColor(this, R.color.black))
                aboutMe.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }


        userImage.setOnClickListener {
            // Create an intent to open the gallery
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        rootView = window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)


        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height

            val keyboardHeight = screenHeight - rect.bottom

            val layoutParams = continueFab.layoutParams as ViewGroup.MarginLayoutParams
            if (keyboardHeight > 0) {
                // Keyboard is visible, move the FAB up
                layoutParams.bottomMargin = keyboardHeight + 30
            } else {
                // Keyboard is not visible, reset the FAB position
                layoutParams.bottomMargin =
                    resources.getDimensionPixelSize(R.dimen.fab_margin_bottom)
            }
            continueFab.layoutParams = layoutParams
        }
    }

    // Detach the keyboard visibility listener when the activity stops
    override fun onStop() {
        super.onStop()
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        showProgress()

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            selectedImageUri?.let { uri ->
                val reference = storage.reference.child("Profile").child(auth.uid!!)
                val uploadTask = reference.putFile(uri)

                uploadTask.addOnSuccessListener { _ ->

                    // Image upload success
                    reference.downloadUrl.addOnCompleteListener { uriTask ->
                        imageUrl = uriTask.result.toString()

                        // Retrieve other user data
                        val uid = auth.uid
                        val name = userName.text.toString()
                        val number = sharedPreferences.getString("number", "UNDEFINED")
                        val user = UserModel(uid, name, number, imageUrl)

                        // Save user data to Firebase Realtime Database
                        database.reference
                            .child("users")
                            .child(uid!!)
                            .setValue(user)
                            .addOnCompleteListener {
                                Toast.makeText(applicationContext, "Display picture updated!", Toast.LENGTH_SHORT).show()
                                hideProgress()
                                val editor = sharedPreferences.edit()

                                }
                            }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                        Log.d("error", exception.toString())
                        // Image upload failure
                        // Handle the failure scenario
                        hideProgress()
                    }
                }
                // Load the selected image with circular cropping
                loadSelectedImage(selectedImageUri)
            }
        }

    private fun loadSelectedImage(uri: Uri?) {


        Glide.with(this)
            .load(uri)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.userImage)
        binding.userImage.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    private fun showProgress() {
        continueFab.isEnabled = false
        continueFab.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
        continueFab.visibility = View.VISIBLE
        continueFab.isEnabled = true
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap?): String? {
        if (bitmap == null) return null

        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
        val fileName = "selected_image.png"
        val file = File(directory, fileName)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Uri.parse(file.absolutePath).toString()
    }


}

