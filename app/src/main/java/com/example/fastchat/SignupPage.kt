package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fastchat.databinding.ActivitySignupPageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale
import java.util.concurrent.TimeUnit

class SignupPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupPageBinding
    private lateinit var countryTxt: AutoCompleteTextView
    private lateinit var continueFab: FloatingActionButton
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var rootView: View
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySignupPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = ContextCompat.getColor(this, R.color.back)


        countryTxt = binding.countryCode
        continueFab = binding.continueFab
        phoneNumber = binding.phoneNumber
        progressBar = binding.progressBar
        getTelephoneCode()
        val countryCodes = listOf(
            "+93", // Afghanistan
            "+355", // Albania
            "+213", // Algeria
            "+376", // Andorra
            "+244", // Angola
            "+1", // Antigua and Barbuda
            "+54", // Argentina
            "+374", // Armenia
            "+61", // Australia
            "+43", // Austria
            "+994", // Azerbaijan
            "+1", // Bahamas
            "+973", // Bahrain
            "+880", // Bangladesh
            "+1", // Barbados
            "+375", // Belarus
            "+32", // Belgium
            "+501", // Belize
            "+229", // Benin
            "+975", // Bhutan
            "+591", // Bolivia
            "+387", // Bosnia and Herzegovina
            "+267", // Botswana
            "+55", // Brazil
            "+673", // Brunei
            "+359", // Bulgaria
            "+226", // Burkina Faso
            "+257", // Burundi
            "+855", // Cambodia
            "+237", // Cameroon
            "+1", // Canada
            "+238", // Cape Verde
            "+236", // Central African Republic
            "+235", // Chad
            "+56", // Chile
            "+86", // China
            "+57", // Colombia
            "+269", // Comoros
            "+242", // Congo
            "+506", // Costa Rica
            "+385", // Croatia
            "+53", // Cuba
            "+357", // Cyprus
            "+420", // Czech Republic
            "+45", // Denmark
            "+253", // Djibouti
            "+1", // Dominica
            "+1", // Dominican Republic
            "+670", // East Timor
            "+593", // Ecuador
            "+20", // Egypt
            "+503", // El Salvador
            "+240", // Equatorial Guinea
            "+291", // Eritrea
            "+372", // Estonia
            "+251", // Ethiopia
            "+679", // Fiji
            "+358", // Finland
            "+33", // France
            "+241", // Gabon
            "+220", // Gambia
            "+995", // Georgia
            "+49", // Germany
            "+233", // Ghana
            "+30", // Greece
            "+1", // Grenada
            "+502", // Guatemala
            "+224", // Guinea
            "+245", // Guinea-Bissau
            "+592", // Guyana
            "+509", // Haiti
            "+504", // Honduras
            "+36", // Hungary
            "+354", // Iceland
            "+91", // India
            "+62", // Indonesia
            "+98", // Iran
            "+964", // Iraq
            "+353", // Ireland
            "+972", // Israel
            "+39", // Italy
            "+1", // Jamaica
            "+81", // Japan
            "+962", // Jordan
            "+7", // Kazakhstan
            "+254", // Kenya
            "+686", // Kiribati
            "+850", // Korea, North
            "+82", // Korea, South
            "+965", // Kuwait
            "+996", // Kyrgyzstan
            "+856", // Laos
            "+371", // Latvia
            "+961", // Lebanon
            "+266", // Lesotho
            "+231", // Liberia
            "+218", // Libya
            "+423", // Liechtenstein
            "+370", // Lithuania
            "+352", // Luxembourg
            "+389", // Macedonia
            "+261", // Madagascar
            "+265", // Malawi
            "+60", // Malaysia
            "+960", // Maldives
            "+223", // Mali
            "+356", // Malta
            "+692", // Marshall Islands
            "+222", // Mauritania
            "+230", // Mauritius
            "+52", // Mexico
            "+691", // Micronesia
            "+373", // Moldova
            "+377", // Monaco
            "+976", // Mongolia
            "+382", // Montenegro
            "+212", // Morocco
            "+258", // Mozambique
            "+95", // Myanmar
            "+264", // Namibia
            "+674", // Nauru
            "+977", // Nepal
            "+31", // Netherlands
            "+64", // New Zealand
            "+505", // Nicaragua
            "+227", // Niger
            "+234", // Nigeria
            "+47", // Norway
            "+968", // Oman
            "+92", // Pakistan
            "+680", // Palau
            "+507", // Panama
            "+675", // Papua New Guinea
            "+595", // Paraguay
            "+51", // Peru
            "+63", // Philippines
            "+48", // Poland
            "+351", // Portugal
            "+974", // Qatar
            "+40", // Romania
            "+7", // Russia
            "+250", // Rwanda
            "+1", // Saint Kitts and Nevis
            "+1", // Saint Lucia
            "+1", // Saint Vincent and the Grenadines
            "+685", // Samoa
            "+378", // San Marino
            "+239", // Sao Tome and Principe
            "+966", // Saudi Arabia
            "+221", // Senegal
            "+381", // Serbia
            "+248", // Seychelles
            "+232", // Sierra Leone
            "+65", // Singapore
            "+421", // Slovakia
            "+386", // Slovenia
            "+677", // Solomon Islands
            "+252", // Somalia
            "+27", // South Africa
            "+211", // South Sudan
            "+34", // Spain
            "+94", // Sri Lanka
            "+249", // Sudan
            "+597", // Suriname
            "+268", // Swaziland
            "+46", // Sweden
            "+41", // Switzerland
            "+963", // Syria
            "+886", // Taiwan
            "+992", // Tajikistan
            "+255", // Tanzania
            "+66", // Thailand
            "+228", // Togo
            "+676", // Tonga
            "+1", // Trinidad and Tobago
            "+216", // Tunisia
            "+90", // Turkey
            "+993", // Turkmenistan
            "+688", // Tuvalu
            "+256", // Uganda
            "+380", // Ukraine
            "+971", // United Arab Emirates
            "+44", // United Kingdom
            "+1", // United States
            "+598", // Uruguay
            "+998", // Uzbekistan
            "+678", // Vanuatu
            "+379", // Vatican City
            "+58", // Venezuela
            "+84", // Vietnam
            "+967", // Yemen
            "+260", // Zambia
            "+263" // Zimbabwe
        )


        val adapter = ArrayAdapter(this, R.layout.list_item, countryCodes)
        (countryTxt).setAdapter(adapter)



        //Change color of edittext depending on the UI
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                phoneNumber.setTextColor(ContextCompat.getColor(this, R.color.white))
                countryTxt.setTextColor(ContextCompat.getColor(this, R.color.white))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                phoneNumber.setTextColor(ContextCompat.getColor(this, R.color.black))
                countryTxt.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }



        continueFab.setOnClickListener {
            val numberTxt = phoneNumber.text.toString()
            val updatedNumberTxt = if (numberTxt.startsWith("0") && numberTxt.length > 1) {
                numberTxt.substring(1)
            } else {
                numberTxt
            }

            if (countryTxt.text.isEmpty() || updatedNumberTxt.isEmpty()) {
                Snackbar.make(view, "Please input a valid phone number to continue", 2500)
                    .show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm phone number")
                    .setMessage("${countryTxt.text} $updatedNumberTxt \nA verification code will be sent to this number.")
                    .setNegativeButton("Edit number") { dialog, _ ->
                        // Respond to negative button press
                        dialog.dismiss()
                    }
                    .setPositiveButton("Continue") { dialog, _ ->
                        // Respond to positive button press
                        dialog.dismiss()
                        showProgress()
                        val phoneNumber = "${countryTxt.text}${updatedNumberTxt}".trim()
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            15,
                            TimeUnit.SECONDS,
                            this,
                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    signInWithPhoneAuthCredential(credential)
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    Snackbar.make(view, "${e}", Snackbar.LENGTH_SHORT)
                                        .show()
                                    Log.d("error", e.toString())
                                    hideProgress()
                                }

                                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                    this@SignupPage.verificationId = verificationId
                                    val intent = Intent(applicationContext, Verification::class.java)
                                    intent.putExtra("verification_id", verificationId)
                                    val sharedPreferences = getSharedPreferences("glide", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("number", "${countryTxt.text} $updatedNumberTxt")
                                    editor.apply()
                                    hideProgress()
                                    startActivityWithFadeInAnimation(intent)
                                }
                            })
                    }.show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(applicationContext, Verification::class.java)
                    startActivityWithFadeInAnimation(intent)
                    hideProgress()
                    finish()
                } else {
                    Snackbar.make(rootView, "Sign in Failed", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun getTelephoneCode() {
        val items = mutableListOf<String>()
        val localeList = Locale.getAvailableLocales()
        for (locale in localeList) {
            val countryCode = locale.country
            if (countryCode.isNotEmpty()) {
                items.add(countryCode)
            }
            val adapter = ArrayAdapter(this, R.layout.list_item, items)
            (countryTxt as? AutoCompleteTextView)?.setAdapter(adapter)
        }

        val edittext = countryTxt

        val tm = applicationContext
            .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val country = tm.simCountryIso
        Log.d("country :", country.uppercase())
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val countryCode = phoneNumberUtil.getCountryCodeForRegion(country.uppercase())
        edittext.setText("+$countryCode")
    }
    private fun startActivityWithFadeInAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_from_right, android.R.anim.fade_out)
        finish()
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