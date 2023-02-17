package com.simenko.qmapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.simenko.qmapp.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = binding.buttonSave
        /**
         ** One way to create anonymous inner class instance
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