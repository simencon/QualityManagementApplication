package com.simenko.qmapp.utils

object StringUtils {

    @JvmStatic
    fun getWithSpaces(original: String?): String {
        return original?.replace("_", " ") ?: "-"
    }

    @JvmStatic
    fun getMail(original: String?): String {
        return if (original != null) {
            original.split("#mailto:").toTypedArray()[0]
        } else {
            "has no mail"
        }
    }

    @JvmStatic
    fun getDateTime(original: String?): String {
        return if (original != null) {
            original.split("T").toTypedArray()[0] + " " + original.split("T").toTypedArray()[1]
        } else {
            "-"
        }
    }

    @JvmStatic
    fun concatTwoStrings(str1: String?, str2: String?): String {
        return "${str1 ?: "-"} / ${str2 ?: "-"}"
    }

    @JvmStatic
    fun concatTwoStrings1(str1: String?, str2: String?): String {
        return "${str1?:"_"} (${str2?:"_"})"
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
    fun concatTwoStrings4(str1: String?, str2: String?): String {
        return "${str1 ?: "_"}|${str2 ?: "_"}"
    }

    @JvmStatic
    fun concatThreeStrings(str1: String, str2: String, str3: String): String {
        return "$str1 / $str2 / $str3"
    }

    @JvmStatic
    fun concatFourStrings(str1: String?, str2: String?, str3: String?, str4: String?): String {
        return "${str1 ?: "-"} / ${str2 ?: "-"} / ${str3 ?: "-"} / ${str4 ?: "-"}"
    }
}