package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.FilterPeriodDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "TrackedEntityInstanceEventFilter",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityInstanceFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstanceFilter"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["trackedEntityInstanceFilter", "programStage"],
)
internal data class TrackedEntityInstanceEventFilterDB(
    val trackedEntityInstanceFilter: String,
    val programStage: String?,
    val eventStatus: String?,
    val periodFrom: Int?,
    val periodTo: Int?,
    val assignedUserMode: String?,
) : EntityDB<TrackedEntityInstanceEventFilter> {
    override fun toDomain(): TrackedEntityInstanceEventFilter {
        return TrackedEntityInstanceEventFilter.builder()
            .trackedEntityInstanceFilter(trackedEntityInstanceFilter)
            .programStage(programStage)
            .eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
            .eventCreatedPeriod(FilterPeriodDB(periodFrom, periodTo).toDomain())
            .assignedUserMode(assignedUserMode?.let { AssignedUserMode.valueOf(it) })
            .build()
    }
}

internal fun TrackedEntityInstanceEventFilter.toDB(): TrackedEntityInstanceEventFilterDB {
    val eventCreatedPeriodDB = eventCreatedPeriod().toDB()

    return TrackedEntityInstanceEventFilterDB(
        trackedEntityInstanceFilter = trackedEntityInstanceFilter()!!,
        programStage = programStage(),
        eventStatus = eventStatus()?.name,
        periodFrom = eventCreatedPeriodDB.periodFrom,
        periodTo = eventCreatedPeriodDB.periodTo,
        assignedUserMode = assignedUserMode()?.name,
    )
}
