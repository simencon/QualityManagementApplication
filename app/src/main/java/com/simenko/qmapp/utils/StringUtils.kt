package com.simenko.qmapp.utils

import android.util.Log
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "StringUtils"

object StringUtils {

    @JvmStatic
    fun getWithSpaces(original: String?): String {
        return original?.replace("_", " ") ?: "-"
    }

    @JvmStatic
    fun getWithSpacesTitle(original: String?): String {
        val str = original?.replace("_", " ") ?: "-"
        return str.substring(0, 1).uppercase() + str.substring(1).lowercase()
    }

    @JvmStatic
    fun getMail(original: String?): String {
        return if (!original.isNullOrEmpty()) {
            original.split("#mailto:").toTypedArray()[0]
        } else {
            "has no mail"
        }
    }

    @JvmStatic
    fun getDateTime(original: String?): String {
        var result = if (!original.isNullOrEmpty()) {
            original.split("T").toTypedArray()[0] + " " + original.split("T").toTypedArray()[1]
        } else {
            "-"
        }
        if (result != "-") {
            result = result.split(".").toTypedArray()[0]
        }

        return result
    }

    @JvmStatic
    fun concatTwoStrings(str1: String?, str2: String?): String {
        return "${str1 ?: "-"} / ${str2 ?: "-"}"
    }

    @JvmStatic
    fun concatTwoStrings1(str1: String?, str2: String?): String {
        return "${str1 ?: "_"} (${str2 ?: "_"})"
    }

    @JvmStatic
    fun concatTwoStrings2(str1: String, str2: String): String {
        return "($str1) $str2"
    }

    @JvmStatic
    fun concatTwoStrings3(str1: String?, str2: String?): String {
        return "${str1 ?: "_"}-${str2 ?: "_"}"
    }

    @JvmStatic
    fun concatThreeStrings(str1: String?, str2: String?, str3: String?): String {
        return "${str1 ?: "-"} / ${str2 ?: "-"} / ${str3 ?: "-"}"
    }

    @JvmStatic
    fun concatThreeStrings1(str1: String?, str2: String?, str3: String?) = buildString {
        if (!str1.isNullOrEmpty()) append(str1)
        if (!str2.isNullOrEmpty()) append(", $str2")
        if (!str3.isNullOrEmpty()) append(", $str3")
    }

    @JvmStatic
    fun concatFourStrings(str1: String?, str2: String?, str3: String?, str4: String?): String {
        return "${str1 ?: "-"} / ${str2 ?: "-"} / ${str3 ?: "-"} / ${str4 ?: "-"}"
    }

    @JvmStatic
    val mySimpleFormatters: List<SimpleDateFormat> = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSXXX"),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX"),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
        SimpleDateFormat("dd-MMMM-yyyy EEE HH:mm")
    )

    @JvmStatic
    fun getMillisecondsDate(myDateTimeStr: String?): Long? {
        if (myDateTimeStr == null) return null
        mySimpleFormatters.forEach {
            try {
                return it.parse(myDateTimeStr)?.time ?: NoRecord.num.toLong()
            } catch (e: ParseException) {
                Log.d(TAG, "getMillisecondsDate: e.message")
            }
        }
        return NoRecord.num.toLong()
    }

    val FormatForRestService = SelectedNumber(0)

    @JvmStatic
    fun getStringDate(myDateTimeLong: Long?, formatType: Int = 5): String? {
        if (myDateTimeLong == null) return null
        return mySimpleFormatters[formatType].format(Date(myDateTimeLong))
    }

    @JvmStatic
    fun getBoolean(original: String): Boolean {
        val arr = original.split("/").toTypedArray()
        val last = arr[arr.lastIndex]

        return if(last == "true" || last == "false")
            last.toBoolean()
        else
            false
    }
}