package com.example.fitnesstracker.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromExerciseEntries(entries: List<ExerciseEntry>?): String? =
        entries?.let { gson.toJson(it) }

    @TypeConverter
    fun toExerciseEntries(json: String?): List<ExerciseEntry>? =
        json?.let {
            val type = object : TypeToken<List<ExerciseEntry>>() {}.type
            gson.fromJson(json, type)
        }

    @TypeConverter
    fun fromCompoundList(compounds: List<CompoundDose>?): String? =
        compounds?.let { gson.toJson(it) }

    @TypeConverter
    fun toCompoundList(json: String?): List<CompoundDose>? =
        json?.let {
            val type = object : TypeToken<List<CompoundDose>>() {}.type
            gson.fromJson(json, type)
        }

    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.let { gson.toJson(it) }

    @TypeConverter
    fun toIntList(json: String?): List<Int>? = json?.let {
        val type = object : TypeToken<List<Int>>() {}.type
        gson.fromJson(json, type)
    }
}
