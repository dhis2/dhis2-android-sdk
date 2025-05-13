package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["trackedEntityInstanceFilter"]),
    ],
)
internal data class TrackedEntityInstanceEventFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val trackedEntityInstanceFilter: String,
    val programStage: String?,
    val eventStatus: String?,
    val periodFrom: Int?,
    val periodTo: Int?,
    val assignedUserMode: String?,
) : EntityDB<TrackedEntityInstanceEventFilter> {
    override fun toDomain(): TrackedEntityInstanceEventFilter {
        return TrackedEntityInstanceEventFilter.builder()
            .id(id?.toLong())
            .trackedEntityInstanceFilter(trackedEntityInstanceFilter)
            .programStage(programStage)
            .eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
            .eventCreatedPeriod(FilterPeriodDB(periodFrom, periodTo).toDomain())
            .assignedUserMode(assignedUserMode?.let { AssignedUserMode.valueOf(it) })
            .build()
    }
}

internal fun TrackedEntityInstanceEventFilter.toDB(): TrackedEntityInstanceEventFilterDB {
    return TrackedEntityInstanceEventFilterDB(
        trackedEntityInstanceFilter = trackedEntityInstanceFilter()!!,
        programStage = programStage(),
        eventStatus = eventStatus()?.name,
        periodFrom = eventCreatedPeriod().toDB().periodFrom,
        periodTo = eventCreatedPeriod().toDB().periodTo,
        assignedUserMode = assignedUserMode()?.name,
    )
}
