package com.suroid.weatherapp.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(tableName = "selected_city",
        indices = [Index("city_id")],
        foreignKeys = [ForeignKey(
                entity = CityEntity::class,
                parentColumns = ["id"],
                childColumns = ["city_id"],
                onDelete = CASCADE)])
data class SelectedCityEntity(@PrimaryKey
                              @ColumnInfo(name = "city_id")
                              val cityId: Int)