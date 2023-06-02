package com.simenko.qmapp.room.implementation

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
}