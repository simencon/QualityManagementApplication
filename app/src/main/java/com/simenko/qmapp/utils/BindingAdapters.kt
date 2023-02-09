package com.simenko.qmapp.utils

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Sort of advance staff
 * ToDo - learn it later!
 */

@BindingAdapter("isNetworkError", "departments")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, departments: Any?) {
    view.visibility = if (departments != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}

object StringUtils {

    @JvmStatic
    fun getMail(original: String?): String {
        return if(original != null) {
            original.split("#mailto:").toTypedArray()[0]
        } else {
            "has no mail"
        }
    }

    @JvmStatic
    fun concatTwoStrings(str1: String, str2: String): String {
        return "$str1 / $str2"
    }

    @JvmStatic
    fun concatTwoStrings1(str1: String, str2: String): String {
        return "$str1 ($str2)"
    }

    @JvmStatic
    fun concatThreeStrings(str1: String, str2: String, str3: String): String {
        return "$str1 / $str2 / $str3"
    }
}