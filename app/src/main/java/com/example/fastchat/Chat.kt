package com.example.fastchat

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fastchat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import java.util.Date

class Chat : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var adapter: MessageAdapter? = null
    private var messages: ArrayList<Message>? = null
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var dialog: ProgressDialog? = null
    private var senderUid: String? = null
    private var receiverUid: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private val outputFile: String by lazy {
        "${externalCacheDir?.absolutePath}/recorded_audio.mp3"
    }
    private var fadeInOut: AlphaAnimation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.back)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        messages = ArrayList()
        fadeInOut = AlphaAnimation(0.0f, 1.0f)

        val name = intent.getStringExtra("name")
        val image = intent.getStringExtra("profile")
        binding.userNameTxt.text = name
        Glide.with(this).load(image)
            .placeholder(R.drawable.profile)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.profileImage)
        binding.profileImage.setBackgroundColor(Color.TRANSPARENT)


        binding.backImg.setOnClickListener { finish() }

        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence").child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "Offline") {
                            binding.status.text = status
                        } else {
                            binding.status.text = status
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        adapter = MessageAdapter(this, messages!!, senderRoom!!, receiverRoom!!)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        GlobalScope.launch(Dispatchers.IO) {
            database!!.reference.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messages!!.clear()
                        for (snapshot1 in snapshot.children) {
                            val message: Message? = snapshot1.getValue(Message::class.java)
                            message!!.messageId = snapshot1.key
                            messages!!.add(message)
                        }
                        adapter!!.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        binding.send.setOnClickListener {
            if (!isRecording) {
                if (binding.messageEdt.text.isNotEmpty()) {
                    val messageTxt: String = binding.messageEdt.text.toString()
                    val date = Date()
                    val message = Message(messageTxt, senderUid, date.time)


                    // Clear the EditText
                    binding.messageEdt.setText("")

                    // Delay scrolling to ensure animation is complete
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Scroll to the last item
                        binding.recyclerView.scrollToPosition(adapter!!.itemCount - 1)
                    }, 300)
                    val randomKey = database!!.reference.push().key
                    val lastMsgObj = HashMap<String, Any>()
                    lastMsgObj["lastMsg"] = message.message!!
                    lastMsgObj["lastMsgTime"] = date.time


                    database!!.reference.child("chats").child(senderRoom!!)
                        .updateChildren(lastMsgObj)
                    database!!.reference.child("chats").child(receiverRoom!!)
                        .updateChildren(lastMsgObj)
                    database!!.reference.child("chats").child(senderRoom!!)
                        .child("messages")
                        .child(randomKey!!)
                        .setValue(message).addOnSuccessListener {
                            database!!.reference.child("chats")
                                .child(receiverRoom!!)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message)
                                .addOnSuccessListener { }

                        }

                } else if (binding.messageEdt.text.isEmpty() || binding.messageEdt.text.toString() == "") {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already granted, you can start recording here
                        startRecording()
                    } else {
                        // Permission is not granted, request the permission
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
                    }
                    binding.camera.setImageResource(R.drawable.send)
                }
            } else {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                }
                isRecording = false
                binding.camera.setImageResource(R.drawable.camera)
                binding.send.setImageResource(R.drawable.send)
                stopBlinkAnimation(binding.send)
                binding.messageEdt.setText("")
                Toast.makeText(applicationContext, "Voice recording cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.camera.setOnClickListener {
            stopRecording()
        }

        binding.clip.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }

        val handler = Handler()
        binding.messageEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                database!!.reference.child("presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 500)

                if (binding.messageEdt.text.isEmpty()) {
                    binding.send.setImageResource(R.drawable.microphone)
                } else {
                    binding.send.setImageResource(R.drawable.send)
                }
            }

            var userStoppedTyping = Runnable {
                database!!.reference.child("presence")
                    .child(senderUid!!)
                    .setValue("Online")
            }

        })

    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!)
            .setValue("Offline")
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = storage!!.reference.child("chats")
                        .child(calendar.timeInMillis.toString() + "")

                    val handler = Handler()
                    val uploadTimeout = 15000L // 15 seconds

                    dialog!!.show()
                    reference.putFile(selectedImage!!)
                        .addOnCompleteListener { task ->
                            dialog!!.dismiss()
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->
                                    val filePath = uri.toString()
                                    val messageTxt: String = binding.messageEdt.text.toString()
                                    val date = Date()
                                    val message = Message(messageTxt, senderUid, date.time)
                                    message.message = "photo"
                                    message.imageUrl = filePath
                                    binding.messageEdt.setText("")
                                    val randomKey = database!!.reference.push().key
                                    val lastMsgObj = HashMap<String, Any>()
                                    lastMsgObj["lastMsg"] = message.message!!
                                    lastMsgObj["lastMsgTime"] = date.time

                                    database!!.reference.child("chats")
                                        .child(senderRoom!!)
                                        .updateChildren(lastMsgObj)
                                    database!!.reference.child("chats")
                                        .child(receiverRoom!!)
                                        .updateChildren(lastMsgObj)
                                    database!!.reference.child("chats")
                                        .child(senderRoom!!)
                                        .child("messages")
                                        .child(randomKey!!)
                                        .setValue(message)
                                        .addOnSuccessListener { }
                                    database!!.reference.child("chats")
                                        .child(receiverRoom!!)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message)
                                        .addOnSuccessListener { }
                                }
                            } else {
                                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    // Set a timeout for the upload process
                    handler.postDelayed({
                        if (reference.activeUploadTasks.isNotEmpty()) {
                            reference.activeUploadTasks.first().cancel()
                            dialog!!.dismiss()
                            Toast.makeText(this, "Image upload timed out", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }, uploadTimeout)
                }
            }
        }
    }
    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // Use the appropriate output format
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // Use the appropriate audio encoder
                setOutputFile(outputFile)
                prepare()
                start()
            }
            isRecording = true
            // Update UI to show recording started
            binding.messageEdt.setText("Recording...")
            blinkAnimation(binding.send)
        } catch (e: Exception) {
            // Handle any errors
            binding.messageEdt.setText("")
            binding.camera.setImageResource(R.drawable.camera)
            Toast.makeText(applicationContext, "Error starting the mic", Toast.LENGTH_SHORT).show()
            Log.d("mic_error", e.toString())
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null
            isRecording = false
            binding.send.setImageResource(R.drawable.microphone)
            binding.messageEdt.setText("")
            stopBlinkAnimation(binding.send)
            binding.camera.setImageResource(R.drawable.camera)
            uploadAudioToFirebaseStorage()
            // Update UI to show recording stopped
        } catch (e: Exception) {
            // Handle any errors
            Toast.makeText(applicationContext, "Error stopping the mic", Toast.LENGTH_SHORT).show()
            Log.d("mic_error", e.toString())
        }
    }

    private fun blinkAnimation(view: ImageView) {
        // Set up the AlphaAnimation
        // Fade in from 0 to 1
        fadeInOut!!.duration = 1000 // Half second for fade in
        fadeInOut!!.repeatMode = Animation.REVERSE
        binding.send.setImageResource(R.drawable.recording)// Reverse the animation (fade out)

        // Set AnimationListener to restart the animation after it ends
        fadeInOut!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Start the animation again after it ends
                view.startAnimation(fadeInOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Start the animation
        view.startAnimation(fadeInOut)
    }
    private fun uploadAudioToFirebaseStorage() {
        val calendar = Calendar.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference.child("chats").child(calendar.timeInMillis.toString() + ".mp3")

        val fileUri = Uri.fromFile(File(outputFile))
        val handler = Handler()
        val uploadTimeout = 15000L // 15 seconds
        dialog!!.setMessage("Sending voice note...")
        dialog!!.show()
        storageReference.putFile(fileUri!!)
            .addOnCompleteListener { task ->
                dialog!!.dismiss()
                if (task.isSuccessful) {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val filePath = uri.toString()
                        val messageTxt: String = binding.messageEdt.text.toString()
                        val date = Date()
                        val message = Message(messageTxt, senderUid, date.time)
                        message.message = "voice"
                        message.imageUrl = filePath
                        binding.messageEdt.setText("")
                        val randomKey = database!!.reference.push().key
                        val lastMsgObj = HashMap<String, Any>()
                        lastMsgObj["lastMsg"] = message.message!!
                        lastMsgObj["lastMsgTime"] = date.time

                        database!!.reference.child("chats")
                            .child(senderRoom!!)
                            .updateChildren(lastMsgObj)
                        database!!.reference.child("chats")
                            .child(receiverRoom!!)
                            .updateChildren(lastMsgObj)
                        database!!.reference.child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(randomKey!!)
                            .setValue(message)
                            .addOnSuccessListener { }
                        database!!.reference.child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message)
                            .addOnSuccessListener { }
                    }
                } else {
                    Toast.makeText(this, "Voice note uploaded", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    private fun stopBlinkAnimation(view: ImageView) {
        // Clear the animation and remove the AnimationListener
        view.clearAnimation()
        fadeInOut?.setAnimationListener(null)
        fadeInOut = null
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can start recording here
                startRecording()
            } else {
                // Permission is denied, show a message or take appropriate action
                Toast.makeText(this, "Microphone permission is required to record audio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}