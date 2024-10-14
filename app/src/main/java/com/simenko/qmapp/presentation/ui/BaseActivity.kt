package com.simenko.qmapp.presentation.ui

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseActivity : ComponentActivity() {
    private var dialog: AlertDialog? = null

    fun showDialog(title: String, msg: String, positiveBtnTitle: String = "OK") {
        dialog?.hide()

        dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveBtnTitle) { _, _ -> }
            .create()

        dialog?.show()
    }

    fun openNewActionView() {

    }
}