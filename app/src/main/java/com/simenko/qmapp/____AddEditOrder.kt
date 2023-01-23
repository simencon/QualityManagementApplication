package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button

class ____AddEditOrder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_order)
        val button = findViewById<Button>(R.id.button_save)
        /**
         * One way to create anonymous inner class instance
         */
        button.setOnClickListener(object :OnClickListener {
            override fun onClick(view: View) {
                TODO("Not yet implemented")
            }
        })
        /**
         * because it is a single function interface:
         * we can use lambda expression
         */
        button.setOnClickListener { v: View -> TODO("Not yet implemented") }
        /**
         * or even simplified lambda expression by
         * removing single lambda parameter
         */
        button.setOnClickListener { TODO("Not yet implemented") }
    }
}