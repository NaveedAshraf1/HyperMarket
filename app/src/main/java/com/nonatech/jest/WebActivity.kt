package com.example.jest


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var loadingDialog: ProgressDialog // Use ProgressDialog or Dialog

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        webView = findViewById(R.id.webView)
        val url = intent.getStringExtra("url")

        // Initialize loading dialog with custom attributes
        loadingDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            setCanceledOnTouchOutside(false) // Prevent accidental dismissal
        }
        loadingDialog.show()

        webView.settings.javaScriptEnabled = true // Enable JavaScript if needed
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingDialog.dismiss() // Show dialog when page starts loading
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Toast.makeText(this@WebActivity, error.toString(), Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingDialog.dismiss() // Dismiss dialog when page finishes loading
            }
        }

        webView.loadUrl(url.toString())
    }
}

