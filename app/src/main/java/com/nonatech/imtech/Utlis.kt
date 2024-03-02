package com.nonatech.imtech


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Utils {


    fun showProgressDialog(context: Context, message: String): Dialog {
        var progressDialog = Dialog(context)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = message
        progressDialog.setContentView(view)
        progressDialog.show()
        return progressDialog
    }

    fun dismissProgressDialog(progressDialog: Dialog) {
        progressDialog.dismiss()
    }

    fun myToast(context: Activity, message:String, length:Int= Toast.LENGTH_SHORT){
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message,length).show()
        }
    }




}