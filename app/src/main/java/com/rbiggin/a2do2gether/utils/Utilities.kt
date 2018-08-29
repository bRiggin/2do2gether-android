package com.rbiggin.a2do2gether.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import com.rbiggin.a2do2gether.R

class Utilities {

    fun showOKDialog(context: Context, title: String, desc: String, okayButtonText: String = "OK",
                     functionalCode: () -> Unit = {}, timed: Boolean = false) {
        (context as Activity).runOnUiThread {
            val alertDialog = Dialog(context)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.dialog_alert)
            alertDialog.findViewById<TextView?>(R.id.alertDialogTitle)?.text = title
            alertDialog.findViewById<TextView?>(R.id.alertDialogMessage)?.text = desc

            alertDialog.findViewById<TextView>(R.id.alertDialogNegativeButton).visibility = View.GONE
            val positiveButton = alertDialog.findViewById<TextView>(R.id.alertDialogPositiveButton)
            positiveButton.text = okayButtonText
            val alertLayout = alertDialog.findViewById<RelativeLayout>(R.id.alertDialogLayout)
            alertLayout.setOnClickListener{
                alertDialog.dismiss()
                functionalCode()
            }
            alertDialog.show()

            if (timed) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (alertDialog.isShowing) {
                        alertDialog.dismiss()
                    }
                }, Constants.DIALOG_DISMISS_TIME.toLong())
            }
        }
    }

    fun showFunctionDialog(context: Context, title: String, desc: String,
                           posButtonText: String = "Yes", positiveCode: () -> Unit = {},
                           negButtonText: String = "No", negativeCode: () -> Unit = {},
                           timed: Boolean = false) {
        (context as Activity).runOnUiThread {
            val alertDialog = Dialog(context)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.dialog_alert)
            alertDialog.findViewById<TextView?>(R.id.alertDialogTitle)?.text = title
            alertDialog.findViewById<TextView?>(R.id.alertDialogMessage)?.text = desc

            val negButton = alertDialog.findViewById<TextView>(R.id.alertDialogNegativeButton)
            negButton.text = negButtonText
            negButton.setOnClickListener {
                if (!context.isFinishing) {
                    alertDialog.dismiss()
                }
                negativeCode()
            }

            val posButton = alertDialog.findViewById<TextView>(R.id.alertDialogPositiveButton)
            posButton.text = posButtonText
            posButton.setOnClickListener {
                if (!context.isFinishing) {
                    alertDialog.dismiss()
                }
                positiveCode()
            }

            alertDialog.show()

            if (timed) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (!context.isFinishing) {
                        alertDialog.dismiss()
                    }
                }, Constants.DIALOG_DISMISS_TIME.toLong())
            }
        }
    }

    fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
    }

    fun decode(input: String): String {
        return String(Base64.decode(input, Base64.DEFAULT))
    }

    fun String.toBoolean(): Boolean{
        return when (toString().trim()){
            "true" -> {
                true
            } else -> {
                false
            }
        }
    }


    fun hashMapToArray(map: HashMap<*, *>): ArrayList<Any>{
        val array : ArrayList<Any> = ArrayList()
        for ((_, item) in map) {
            array.add(item)
        }
        return array
    }
}