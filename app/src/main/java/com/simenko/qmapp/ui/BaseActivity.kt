package com.simenko.qmapp.ui

import androidx.activity.ComponentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseActivity : ComponentActivity() {

    fun showDialog(title: String, msg: String, positiveBtnTitle: String = "OK") {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveBtnTitle) { _, _ -> }
            .create()
            .show()
    }

    fun openNewActionView() {

    }
}