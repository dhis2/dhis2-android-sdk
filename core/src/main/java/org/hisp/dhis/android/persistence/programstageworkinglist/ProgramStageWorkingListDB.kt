package org.hisp.dhis.android.persistence.programstageworkinglist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageQueryCriteria
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "ProgramStageWorkingList",
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
            childColumns = ["orgUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class ProgramStageWorkingListDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val description: String?,
    val program: String,
    val programStage: String,
    val eventStatus: String?,
    val eventCreatedAt: DateFilterPeriodDB?,
    val eventOccurredAt: DateFilterPeriodDB?,
    val eventScheduledAt: DateFilterPeriodDB?,
    val enrollmentStatus: String?,
    val enrolledAt: DateFilterPeriodDB?,
    val enrollmentOccurredAt: DateFilterPeriodDB?,
    val orderProperty: String?,
    val displayColumnOrder: StringListDB?,
    val orgUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
) : EntityDB<ProgramStageWorkingList>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramStageWorkingList {
        return ProgramStageWorkingList.builder()
            .applyBaseIdentifiableFields(this)
            .description(description)
            .program(ObjectWithUidDB(program).toDomain())
            .programStage(ObjectWithUidDB(programStage).toDomain())
            .programStageQueryCriteria(
                ProgramStageQueryCriteria.builder()
                    .eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
                    .eventCreatedAt(eventCreatedAt?.toDomain())
                    .eventOccurredAt(eventOccurredAt?.toDomain())
                    .eventScheduledAt(eventScheduledAt?.toDomain())
                    .enrollmentStatus(enrollmentStatus?.let { EnrollmentStatus.valueOf(it) })
                    .enrolledAt(enrolledAt?.toDomain())
                    .enrollmentOccurredAt(enrollmentOccurredAt?.toDomain())
                    .order(orderProperty)
                    .displayColumnOrder(displayColumnOrder?.toDomain())
                    .orgUnit(orgUnit)
                    .ouMode(ouMode?.let { OrganisationUnitMode.valueOf(it) })
                    .assignedUserMode(assignedUserMode?.let { AssignedUserMode.valueOf(it) })
                    .build(),
            )
            .build()
    }
}

internal fun ProgramStageWorkingList.toDB(): ProgramStageWorkingListDB {
    return ProgramStageWorkingListDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        description = description(),
        program = program().uid(),
        programStage = programStage().uid(),
        eventStatus = programStageQueryCriteria()?.eventStatus()?.name,
        eventCreatedAt = programStageQueryCriteria()?.eventCreatedAt()?.toDB(),
        eventOccurredAt = programStageQueryCriteria()?.eventOccurredAt()?.toDB(),
        eventScheduledAt = programStageQueryCriteria()?.eventScheduledAt()?.toDB(),
        enrollmentStatus = programStageQueryCriteria()?.enrollmentStatus()?.name,
        enrolledAt = programStageQueryCriteria()?.enrolledAt()?.toDB(),
        enrollmentOccurredAt = programStageQueryCriteria()?.enrollmentOccurredAt()?.toDB(),
        orderProperty = programStageQueryCriteria()?.order(),
        displayColumnOrder = programStageQueryCriteria()?.displayColumnOrder()?.toDB(),
        orgUnit = programStageQueryCriteria()?.orgUnit(),
        ouMode = programStageQueryCriteria()?.ouMode()?.name,
        assignedUserMode = programStageQueryCriteria()?.assignedUserMode()?.name,
        deleted = deleted(),
    )
}
