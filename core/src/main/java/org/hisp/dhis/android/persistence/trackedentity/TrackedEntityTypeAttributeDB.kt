package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityTypeAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["trackedEntityType"]),
        Index(value = ["trackedEntityAttribute"]),
    ],
)
internal data class TrackedEntityTypeAttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val trackedEntityType: String?,
    val trackedEntityAttribute: String?,
    val displayInList: Int?,
    val mandatory: Int?,
    val searchable: Int?,
    val sortOrder: Int?,
)
