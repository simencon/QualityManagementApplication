package com.simenko.qmapp.room.implementation

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromListOfLongToPair(list: List<Long?>): Pair<Long?, Long?> {
        return Pair(list[0], list[1])
    }

    @TypeConverter
    fun fromPairToListOfLong(pair: Pair<Long?, Long?>): List<Long?> {
        return listOf(pair.first, pair.second)
    }
}