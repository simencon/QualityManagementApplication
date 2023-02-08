package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.simenko.qmapp.databinding.ActivityRecordEditorBinding

class Activity____RecordEditor : AppCompatActivity() {
    private lateinit var binding: ActivityRecordEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = binding.buttonSave
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