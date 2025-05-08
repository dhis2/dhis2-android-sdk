package org.hisp.dhis.android.persistence.imports

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.event.EventDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDB

@Entity(
    tableName = "TrackerImportConflict",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class TrackerImportConflictDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val conflict: String?,
    val value: String?,
    val trackedEntityInstance: String?,
    val enrollment: String?,
    val event: String?,
    val tableReference: String?,
    val errorCode: String?,
    val status: String?,
    val created: String?,
    val displayDescription: String?,
    val trackedEntityAttribute: String?,
    val dataElement: String?,
)
