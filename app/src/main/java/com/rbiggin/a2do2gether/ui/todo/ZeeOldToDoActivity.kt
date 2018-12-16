package com.rbiggin.a2do2gether.ui.homepage

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.rbiggin.a2do2gether.R

class ZeeOldToDoActivity {

    /**private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_to_do)
        super.onCreate(savedInstanceState)
        supportActionBar?.customView?.findViewById<TextView>(R.id.action_bar_title_view)?.text= getString(R.string.to_do_lists)

        val list = arrayListOf("rhgrt", "eriotgjoe jg", "oiertg or")

        linearLayoutManager = LinearLayoutManager(this)
        to_do_recycler_view.layoutManager = linearLayoutManager

        adapter = RecyclerAdapter(list, application)
        to_do_recycler_view.adapter = adapter



        to_do_item_btn.setOnClickListener {
            showAddBtn(true)
        }

        to_do_add_btn.setOnClickListener {
            showAddBtn(false)
        }
    }

    private fun showAddBtn(show: Boolean){
        if (show){
            to_do_add_btn.visibility = View.VISIBLE
            to_do_item_et.visibility = View.GONE
            to_do_item_btn.visibility = View.GONE
            hideKeyboard()
        } else {
            to_do_add_btn.visibility = View.GONE
            to_do_item_et.visibility = View.VISIBLE
            to_do_item_btn.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(to_do_item_et.windowToken, 0)
    }*/
}
