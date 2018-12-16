package com.rbiggin.a2do2gether.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserDetails

class Utilities {

    fun showTextEntryDialog(context: Context, title: String, hint: String,
                            posButtonText: String = "Yes", positiveCode: (String) -> Unit = {},
                            negButtonText: String = "No", negativeCode: () -> Unit = {},
                            timed: Boolean = false) {
        (context as Activity).runOnUiThread {
            val alertDialog = Dialog(context)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.dialog_text_entry)
            alertDialog.findViewById<TextView?>(R.id.textDialogTitle)?.text = title
            alertDialog.findViewById<EditText?>(R.id.textDialogEt)?.hint = hint

            val negButton = alertDialog.findViewById<TextView>(R.id.textDialogNegativeButton)
            negButton.text = negButtonText
            negButton.setOnClickListener {
                if (!context.isFinishing) {
                    alertDialog.dismiss()
                }
                negativeCode()
            }

            val posButton = alertDialog.findViewById<TextView>(R.id.textDialogPositiveButton)
            posButton.text = posButtonText
            posButton.setOnClickListener {
                if (!context.isFinishing) {
                    alertDialog.dismiss()
                }
                val text = alertDialog.findViewById<EditText?>(R.id.textDialogEt)?.text.toString().trim()
                positiveCode(text)
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

    fun <T> hashMapToArray(map: HashMap<*, T>): ArrayList<T>{
        val array : ArrayList<T> = ArrayList()
        for ((_, item) in map) {
            array.add(item)
        }
        return array
    }

    fun <T, J> hashMapToDoubleArray(map: HashMap<T, J>): HashMap<String, ArrayList<String>>{
        val keyArray : ArrayList<String> = ArrayList()
        val valueArray : ArrayList<String> = ArrayList()
        for ((key, value) in map) {
            keyArray.add(key.toString())
            valueArray.add(value.toString())
        }
        return hashMapOf("keyArray" to keyArray, "valueArray" to valueArray)
    }
}