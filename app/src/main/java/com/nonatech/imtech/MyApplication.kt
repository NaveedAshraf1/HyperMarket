package com.nonatech.imtech


import android.app.Application
import android.content.IntentFilter


class MyApplication : Application() {
    companion object{
        var old = ""
    }
    override fun onCreate() {
        super.onCreate()
        // Initialize any necessary components here
        var smsReceiver = SmsReceiver()
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filter)
    }
}
