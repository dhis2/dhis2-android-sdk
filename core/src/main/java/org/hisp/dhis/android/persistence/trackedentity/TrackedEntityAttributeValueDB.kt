package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityAttributeValue",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["trackedEntityInstance"]),
        Index(value = ["trackedEntityInstance", "trackedEntityAttribute"], unique = true),
    ],
)
internal data class TrackedEntityAttributeValueDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val created: String?,
    val lastUpdated: String?,
    val value: String?,
    val trackedEntityAttribute: String,
    val trackedEntityInstance: String,
    val syncState: String?,
)
