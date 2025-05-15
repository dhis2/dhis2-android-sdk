package org.hisp.dhis.android.persistence.event

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "EventFilter",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["organisationUnit"]),
    ],
)
internal data class EventFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val program: String,
    val programStage: String?,
    val description: String?,
    val followUp: Boolean?,
    val organisationUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
    val orderProperty: String?,
    val displayColumnOrder: StringListDB?,
    val events: StringListDB?,
    val eventStatus: String?,
    val eventDate: DateFilterPeriodDB?,
    val dueDate: DateFilterPeriodDB?,
    val lastUpdatedDate: DateFilterPeriodDB?,
    val completedDate: DateFilterPeriodDB?,
) : EntityDB<EventFilter>, BaseIdentifiableObjectDB {
    override fun toDomain(): EventFilter {
        return EventFilter.builder()
            .applyBaseIdentifiableFields(this@EventFilterDB)
            .program(program)
            .programStage(programStage)
            .description(description)
            .eventQueryCriteria(
                EventQueryCriteriaDB(
                    followUp = followUp,
                    organisationUnit = organisationUnit,
                    ouMode = ouMode,
                    assignedUserMode = assignedUserMode,
                    orderProperty = orderProperty,
                    displayColumnOrder = displayColumnOrder,
                    events = events,
                    eventStatus = eventStatus,
                    eventDate = eventDate,
                    dueDate = dueDate,
                    lastUpdatedDate = lastUpdatedDate,
                    completedDate = completedDate,
                ).toDomain(),
            )
            .build()
    }
}

internal fun EventFilter.toDB(): EventFilterDB {
    val eventQueryCriteria = eventQueryCriteria()?.toDB()

    return EventFilterDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created()?.dateFormat(),
        lastUpdated = lastUpdated()?.dateFormat(),
        program = program()!!,
        programStage = programStage(),
        description = description(),
        followUp = eventQueryCriteria?.followUp,
        organisationUnit = eventQueryCriteria?.organisationUnit,
        ouMode = eventQueryCriteria?.ouMode,
        assignedUserMode = eventQueryCriteria?.assignedUserMode,
        orderProperty = eventQueryCriteria?.orderProperty,
        displayColumnOrder = eventQueryCriteria?.displayColumnOrder,
        events = eventQueryCriteria?.events,
        eventStatus = eventQueryCriteria?.eventStatus,
        eventDate = eventQueryCriteria?.eventDate,
        dueDate = eventQueryCriteria?.dueDate,
        lastUpdatedDate = eventQueryCriteria?.lastUpdatedDate,
        completedDate = eventQueryCriteria?.completedDate,
    )
}
