package com.simenko.qmapp.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileLogger {
    private const val TAG = "FileLogger"
    private const val FILE_NAME = "app_log.txt"

    fun logEvent(context: Context, message: String) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "$timeStamp - $message\n"

        try {
            val file = File(context.filesDir, FILE_NAME)
            FileOutputStream(file, true).use { it.write(logMessage.toByteArray()) }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing log to file", e)
        }
    }
}