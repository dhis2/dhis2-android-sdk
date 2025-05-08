package org.hisp.dhis.android.persistence.relationship

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.event.EventDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDB

@Entity(
    tableName = "RelationshipItem",
    foreignKeys = [
        ForeignKey(
            entity = RelationshipDB::class,
            parentColumns = ["uid"],
            childColumns = ["relationship"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["relationship"]),
        Index(value = ["trackedEntityInstance"]),
        Index(value = ["enrollment"]),
        Index(value = ["event"]),
    ],
)
internal data class RelationshipItemDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val relationship: String,
    val relationshipItemType: String,
    val trackedEntityInstance: String?,
    val enrollment: String?,
    val event: String?,
)
