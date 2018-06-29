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

/**
 * Generic functions and utilities
 */
class Utilities {
    /**
     * Shows an alert dialog with single OK button, unless the default string ("OK") is overwritten
     * in function call.
     * @param context - Activity context
     * @param title - Dialog title
     * @param desc - Dialog message
     * @param okayButtonText - String used to populate content of OK button
     * @param functionalCode - lambda function that is executed when ok button selected. Defaults to
     * no action.
     * @param timed - Boolean that determines if the dialog times out. Defaults to false.
     */
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

    /**
     * Shows an alert dialog with and negative and positive button. The content of each button can
     * be defined within the function's call or will default to "Yes" and "No". The function also
     * takes a lambda function which contains the code to be performed if the positive button is
     * selected by the user. If no lambda code is provided then the function performs no action.
     * @param context - Activity context
     * @param title - Dialog title
     * @param desc - Dialog message
     * @param posButtonText - Text displayed within positive button.
     * @param positiveCode - Lambda function provided by caller to be performed on positive button
     * press. Defaults to no action.
     * @param negButtonText - Text displayed within negative button.
     * @param negativeCode - Lambda function provided by caller to be performed on positive button
     * press. Defaults to no action.
     * @param timed - Boolean that determines if the dialog times out. Defaults to false.
     */
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

    /**
     * Encode
     */
    fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
    }

    /**
     * Decode
     */
    fun decode(input: String): String {
        return String(Base64.decode(input, Base64.DEFAULT))
    }

    /**
     * String to Boolean
     */
    fun stringToBoolean(boolean: String): Boolean{
        return when (boolean.trim()){
            "true" -> {
                true
            } else -> {
                false
            }
        }
    }
}