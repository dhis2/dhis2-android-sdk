package org.hisp.dhis.android.persistence.imports

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
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
    primaryKeys = [
        "conflict",
        "value",
        "trackedEntityInstance",
        "enrollment",
        "event",
        "tableReference",
        "trackedEntityAttribute",
        "dataElement"
    ]
)
internal data class TrackerImportConflictDB(
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
) : EntityDB<TrackerImportConflict> {

    override fun toDomain(): TrackerImportConflict {
        return TrackerImportConflict.builder().apply {
            conflict(conflict)
            value(value)
            trackedEntityInstance(trackedEntityInstance)
            enrollment(enrollment)
            event(event)
            tableReference(tableReference)
            errorCode(errorCode)
            status?.let { status(ImportStatus.valueOf(it)) }
            created(created.toJavaDate())
            displayDescription(displayDescription)
            trackedEntityAttribute(trackedEntityAttribute)
            dataElement(dataElement)
        }.build()
    }
}

internal fun TrackerImportConflict.toDB(): TrackerImportConflictDB {
    return TrackerImportConflictDB(
        conflict = conflict(),
        value = value(),
        trackedEntityInstance = trackedEntityInstance(),
        enrollment = enrollment(),
        event = event(),
        tableReference = tableReference(),
        errorCode = errorCode(),
        status = status()?.name,
        created = created().dateFormat(),
        displayDescription = displayDescription(),
        trackedEntityAttribute = trackedEntityAttribute(),
        dataElement = dataElement(),
    )
}
