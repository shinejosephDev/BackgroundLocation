package com.sample.backgroundlocation.db

import androidx.room.*


@Entity(indices = [Index(value = ["name"], unique = true)])
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "interval") val interval: Int,
) {
}


@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("deviceId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocationData(
    @ColumnInfo(index = true) val deviceId: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "created_at")val created_at: Long?
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}

data class UserAndLocation(
    @Embedded
    val artist: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "deviceId"
    )
    val albums: List<LocationData>,
)
