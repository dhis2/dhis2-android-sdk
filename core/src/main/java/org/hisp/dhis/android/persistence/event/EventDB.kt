package org.hisp.dhis.android.persistence.event

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.GeometryDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "Event",
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
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
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
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["attributeOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["enrollment"]),
        Index(value = ["organisationUnit"]),
        Index(value = ["attributeOptionCombo"]),
    ],
)
internal data class EventDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val enrollment: String?,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val status: String?,
    val geometryType: String?,
    val geometryCoordinates: String?,
    val program: String,
    val programStage: String,
    val organisationUnit: String,
    val eventDate: String?,
    val completedDate: String?,
    val dueDate: String?,
    override val syncState: SyncStateDB?,
    val aggregatedSyncState: SyncStateDB?,
    val attributeOptionCombo: String?,
    override val deleted: Boolean?,
    val assignedUser: String?,
    val completedBy: String?,
) : EntityDB<Event>, DataObjectDB, DeletableObjectDB {

    override fun toDomain(): Event {
        return Event.builder().apply {
            id(id?.toLong())
            uid(uid)
            enrollment(enrollment)
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(lastUpdatedAtClient.toJavaDate())
            status?.let { status(EventStatus.valueOf(it)) }
            geometry(GeometryDB(geometryType, geometryCoordinates).toDomain())
            program(program)
            programStage(programStage)
            organisationUnit(organisationUnit)
            eventDate(eventDate.toJavaDate())
            completedDate(completedDate.toJavaDate())
            dueDate(dueDate.toJavaDate())
            syncState?.let { syncState(it.toDomain()) }
            aggregatedSyncState?.let { aggregatedSyncState(it.toDomain()) }
            attributeOptionCombo(attributeOptionCombo)
            deleted(deleted)
            assignedUser(assignedUser)
            completedBy(completedBy)
        }.build()
    }
}

internal fun Event.toDB(): EventDB {
    return EventDB(
        uid = uid(),
        enrollment = enrollment(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        createdAtClient = createdAtClient().dateFormat(),
        lastUpdatedAtClient = lastUpdatedAtClient().dateFormat(),
        status = status()?.name,
        geometryType = geometry().toDB().geometryType,
        geometryCoordinates = geometry().toDB().geometryCoordinates,
        program = program()!!,
        programStage = programStage()!!,
        organisationUnit = organisationUnit()!!,
        eventDate = eventDate().dateFormat(),
        completedDate = completedDate().dateFormat(),
        dueDate = dueDate().dateFormat(),
        syncState = syncState()?.toDB(),
        aggregatedSyncState = aggregatedSyncState()?.toDB(),
        attributeOptionCombo = attributeOptionCombo(),
        deleted = deleted(),
        assignedUser = assignedUser(),
        completedBy = completedBy()
    )
}
