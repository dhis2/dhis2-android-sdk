package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "TrackedEntityInstanceFilter",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class TrackedEntityInstanceFilterDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val color: String?,
    override val icon: String?,
    override val deleted: Boolean?,
    val program: String,
    val description: String?,
    val sortOrder: Int?,
    val enrollmentStatus: String?,
    val followUp: Boolean?,
    val organisationUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
    val orderProperty: String?,
    val displayColumnOrder: StringListDB?,
    val eventStatus: String?,
    val eventDate: DateFilterPeriodDB?,
    val lastUpdatedDate: DateFilterPeriodDB?,
    val programStage: String?,
    val trackedEntityInstances: StringListDB?,
    val enrollmentIncidentDate: DateFilterPeriodDB?,
    val enrollmentCreatedDate: DateFilterPeriodDB?,
    val trackedEntityType: String?,
) : EntityDB<TrackedEntityInstanceFilter>, BaseIdentifiableObjectDB, ObjectWithStyleDB {
    override fun toDomain(): TrackedEntityInstanceFilter {
        return TrackedEntityInstanceFilter.builder().apply {
            applyBaseIdentifiableFields(this@TrackedEntityInstanceFilterDB)
            applyStyleFields(this@TrackedEntityInstanceFilterDB)
            program(ObjectWithUid.create(program))
            description(description)
            sortOrder(sortOrder)
            entityQueryCriteria(
                EntityQueryCriteriaDB(
                    enrollmentStatus = enrollmentStatus,
                    followUp = followUp,
                    organisationUnit = organisationUnit,
                    ouMode = ouMode,
                    assignedUserMode = assignedUserMode,
                    orderProperty = orderProperty,
                    displayColumnOrder = displayColumnOrder,
                    eventStatus = eventStatus,
                    eventDate = eventDate,
                    lastUpdatedDate = lastUpdatedDate,
                    programStage = programStage,
                    trackedEntityInstances = trackedEntityInstances,
                    enrollmentIncidentDate = enrollmentIncidentDate,
                    enrollmentCreatedDate = enrollmentCreatedDate,
                    trackedEntityType = trackedEntityType,
                ).toDomain(),
            )
        }.build()
    }
}

internal fun TrackedEntityInstanceFilter.toDB(): TrackedEntityInstanceFilterDB {
    val entityQueryCriteriaDB = entityQueryCriteria().toDB()

    return TrackedEntityInstanceFilterDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        color = style()?.color(),
        icon = style()?.icon(),
        program = program()!!.uid(),
        description = description(),
        sortOrder = sortOrder(),
        enrollmentStatus = entityQueryCriteriaDB.enrollmentStatus,
        followUp = entityQueryCriteriaDB.followUp,
        organisationUnit = entityQueryCriteriaDB.organisationUnit,
        ouMode = entityQueryCriteriaDB.ouMode,
        assignedUserMode = entityQueryCriteriaDB.assignedUserMode,
        orderProperty = entityQueryCriteriaDB.orderProperty,
        displayColumnOrder = entityQueryCriteriaDB.displayColumnOrder,
        eventStatus = entityQueryCriteriaDB.eventStatus,
        eventDate = entityQueryCriteriaDB.eventDate,
        lastUpdatedDate = entityQueryCriteriaDB.lastUpdatedDate,
        programStage = entityQueryCriteriaDB.programStage,
        trackedEntityInstances = entityQueryCriteriaDB.trackedEntityInstances,
        enrollmentIncidentDate = entityQueryCriteriaDB.enrollmentIncidentDate,
        enrollmentCreatedDate = entityQueryCriteriaDB.enrollmentCreatedDate,
        trackedEntityType = entityQueryCriteriaDB.trackedEntityType,
        deleted = deleted(),
    )
}
