package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fastchat.databinding.ActivityVerificationBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class Verification : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding
    private lateinit var no1: EditText
    private lateinit var no2: EditText
    private lateinit var no3: EditText
    private lateinit var no4: EditText
    private lateinit var no5: EditText
    private lateinit var no6: EditText
    private lateinit var inputOTP: String
    private lateinit var continueFab: FloatingActionButton
    private lateinit var resend: TextView
    private lateinit var rootView: View
    private lateinit var numberTxt: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.back)
        auth = FirebaseAuth.getInstance()

        no1 = binding.no1
        no2 = binding.no2
        no3 = binding.no3
        no4 = binding.no4
        no5 = binding.no5
        no6 = binding.no6
        continueFab = binding.continueFab
        resend = binding.resend
        numberTxt = binding.number
        progressBar = binding.progressBar

        sharedPreferences = this.getSharedPreferences("glide", Context.MODE_PRIVATE)
        val number = sharedPreferences.getString("number", "UNDEFINED")

        numberTxt.text = number


        // Set a TextWatcher on each EditText
        no1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    no2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        no2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    no3.requestFocus()
                } else if (s?.length == 0) {
                    no1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        no3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    no4.requestFocus()
                } else if (s?.length == 0) {
                    no2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        no4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    no5.requestFocus()
                } else if (s?.length == 0) {
                    no3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        no5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    no6.requestFocus()
                } else if (s?.length == 0) {
                    no4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        no6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    inputOTP = no1.text.toString() + no2.text.toString() + no3.text.toString() + no4.text.toString() + no5.text.toString() + no6.text.toString()
                    inputOTP = inputOTP.replace("//s".toRegex(), "").trim()
                } else if (s?.length == 0) {
                    no5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        resend.paintFlags = resend.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        continueFab.setOnClickListener {
            val verificationId = intent.getStringExtra("verification_id") ?: ""
            val verificationCode = inputOTP
            val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
            showProgress()
            signInWithPhoneAuthCredential(credential)
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgress()
                    val intent = Intent(applicationContext, SetupProfile::class.java)
                    startActivityWithFadeInAnimation(intent)
                    finish()
                } else {
                    hideProgress()
                    Snackbar.make(rootView, "Check the verification code again.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_out_to_right, R.anim.fade_out_to_right)
    }
    override fun onStart() {
        super.onStart()
        rootView = window.decorView.rootView


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
                layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.fab_margin_bottom)
            }
            continueFab.layoutParams = layoutParams
        }
    }

    // Detach the keyboard visibility listener when the activity stops
    override fun onStop() {
        super.onStop()
        rootView.viewTreeObserver.removeOnGlobalLayoutListener{}
    }
    private fun startActivityWithFadeInAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_from_right, android.R.anim.fade_out)
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

}