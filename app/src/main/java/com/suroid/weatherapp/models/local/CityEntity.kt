package com.suroid.weatherapp.models.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "city",
        indices = [Index("id", "name")])
@Parcelize
data class CityEntity(
        @field:SerializedName("name")
        val name: String,
        @field:SerializedName("country")
        val country: String,
        @field:SerializedName("id")
        @PrimaryKey
        val id: Int) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other is CityEntity && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}