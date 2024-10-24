package com.simenko.qmapp.data.cache.prefs

import android.content.SharedPreferences
import com.simenko.qmapp.data.remote.serializer.JsonSingleton
import com.simenko.qmapp.domain.ZeroValue
import kotlinx.serialization.encodeToString

class ScrollStatesPrefs(private val sharedPrefs: SharedPreferences) {
    companion object {
        const val SCROLL_STATES_STORAGE = "scroll_states_storage"

        private const val DEPARTMENTS_LIST = "departments_list"
        private const val LINES_LIST = "lines_list"

        private const val PRODUCTS_LIST = "products_list"
        private const val VERSIONS_LIST = "versions_list"

        private const val CHAR_GROUP_LIST = "char_group_list"
        private const val METRIC_LIST = "metric_list"

        private const val VERSION_CHAR_GROUP_LIST = "version_char_group_list"
        private const val VERSION_TOLERANCE_LIST = "version_tolerance_list"
    }

    private val json = JsonSingleton.networkJson

//    first -> item index, second -> offset

    var departmentsList: Pair<Long, Long>
        get() = sharedPrefs.getString(DEPARTMENTS_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(DEPARTMENTS_LIST, json.encodeToString(value)).apply()
        }

    var linesList: Pair<Long, Long>
        get() = sharedPrefs.getString(LINES_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(LINES_LIST, json.encodeToString(value)).apply()
        }

    var productsList: Pair<Long, Long>
        get() = sharedPrefs.getString(PRODUCTS_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(PRODUCTS_LIST, json.encodeToString(value)).apply()
        }

    var versionsList: Pair<Long, Long>
        get() = sharedPrefs.getString(VERSIONS_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(VERSIONS_LIST, json.encodeToString(value)).apply()
        }

    var charGroupList: Pair<Long, Long>
        get() = sharedPrefs.getString(CHAR_GROUP_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(CHAR_GROUP_LIST, json.encodeToString(value)).apply()
        }

    var metricList: Pair<Long, Long>
        get() = sharedPrefs.getString(METRIC_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(METRIC_LIST, json.encodeToString(value)).apply()
        }

    var versionCharGroupList: Pair<Long, Long>
        get() = sharedPrefs.getString(VERSION_CHAR_GROUP_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(VERSION_CHAR_GROUP_LIST, json.encodeToString(value)).apply()
        }

    var versionToleranceList: Pair<Long, Long>
        get() = sharedPrefs.getString(VERSION_TOLERANCE_LIST, null)?.run { json.decodeFromString(this) } ?: Pair(ZeroValue.num, ZeroValue.num)
        set(value) {
            sharedPrefs.edit().putString(VERSION_TOLERANCE_LIST, json.encodeToString(value)).apply()
        }
}