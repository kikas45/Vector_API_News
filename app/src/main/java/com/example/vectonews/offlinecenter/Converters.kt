package com.example.vectonews.offlinecenter

import androidx.room.TypeConverter
import com.example.vectonews.api.Source

class Converters {
    @TypeConverter
    fun fromSource(articleName: Source): String {
        return articleName.name
    }

    @TypeConverter
    fun toSource(regular_Name: String): Source {
        return Source(regular_Name, regular_Name, )
    }

}