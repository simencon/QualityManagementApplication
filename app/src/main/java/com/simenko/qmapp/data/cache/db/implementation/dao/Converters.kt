package com.simenko.qmapp.data.cache.db.implementation.dao

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPairOfLongs(pair: Pair<Long, Long>): String {
        return "${pair.first}:${pair.second}"
    }

    @TypeConverter
    fun toPairOfLongs(value: String): Pair<Long, Long> {
        val parts = value.split(":")
        return Pair(parts[0].toLong(), parts[1].toLong())
    }

    @TypeConverter
    fun fromSetOfStringToString(set: Set<String>?): String? {
        return set?.joinToString(separator = ";") { it }
    }

    @TypeConverter
    fun fromStringToSetOfString(string: String?): Set<String>? {
        return string?.split(";")?.map { it }?.toSet()
    }
}