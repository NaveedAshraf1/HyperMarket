package com.nonatech.imtech


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.TextView
import androidx.cardview.widget.CardView



class LastPage : AppCompatActivity() {
    private var appInForeground = false
    private val handler = Handler()
    lateinit var time : TextView
    lateinit var card1 : CardView
    lateinit var card2 : CardView
    lateinit var card3 : CardView
    lateinit var card4 : CardView
    lateinit var card5 : CardView
    lateinit var card6 : CardView
    var startTime = 0L

//    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_page)
        time = findViewById(R.id.time)
        card1 = findViewById(R.id.card1)
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)
        card4 = findViewById(R.id.card4)
        card5 = findViewById(R.id.card5)
        card6 = findViewById(R.id.card6)


        card1.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in")}
        card2.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in/SmartphonesGadgets" +
                "")}
        card3.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in/fashion")}
        card4.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in/freshFood")}
        card5.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in/groceries-grocery-baby-kids/c/HY002148002")}
        card6.setOnClickListener {openWebsite("https://www.luluhypermarket.in/en-in/groceries-grocery-health-beauty/c/HY002148003")}
//        Toast.makeText(this, android.os.Build.MODEL, Toast.LENGTH_SHORT).show()

        val waitingTime = 24 * 60 * 60 * 1000L  // 24 hours in milliseconds

        var   sharedPreferences = getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("startTime")) {
            startTime = sharedPreferences.getLong("startTime", 0)
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime
            if (elapsedTime < waitingTime) {
                startCountdownTimer(waitingTime.toLong() - elapsedTime)
            } else {
                time.text = "00:00"
            }
        } else {
            startTime = System.currentTimeMillis()
            sharedPreferences.edit().putLong("startTime", startTime).apply()
            startCountdownTimer(waitingTime.toLong())
        }
    }

    override fun onPause() {
        super.onPause()
        appInForeground = false
        handler.postDelayed({
            if (!appInForeground) {
//                hideApp()
            }
        }, 60 * 1000) // 1 minute
    }

    override fun onResume() {
        super.onResume()
        appInForeground = true
        handler.removeCallbacksAndMessages(null)
    }

//    private fun hideApp() {
//        if (android.os.Build.MODEL != "CPH2343"){
//            val packageManager = packageManager
//            val componentName = ComponentName(this@LastPage, SplashScreen::class.java)
//            packageManager.setComponentEnabledSetting(
//                componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP
//            )
//        }
//    }

    private fun startCountdownTimer(millisInFuture: Long) {
        object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000 % 60
                val minutes = millisUntilFinished / (60 * 1000) % 60
                val hours = millisUntilFinished / (60 * 60 * 1000)

                val timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                time.text = timeText
            }

            override fun onFinish() {
                time.text = "00:00:00"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
//        countDownTimer.cancel()
    }
//042111000622

    companion object{
        val owner = "hyperMarket6"
    }

    fun openWebsite(url:String){
        val intent1 = Intent(this, WebActivity::class.java)
        intent1.putExtra("url",url)
        startActivity(intent1)
    }
}