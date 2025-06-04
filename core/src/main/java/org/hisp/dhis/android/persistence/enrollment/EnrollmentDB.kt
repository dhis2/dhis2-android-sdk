package org.hisp.dhis.android.persistence.enrollment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.GeometryDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.itemfilter.toDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDB

@Entity(
    tableName = "Enrollment",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"]),
    ],
)
internal data class EnrollmentDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val organisationUnit: String,
    val program: String,
    val enrollmentDate: String?,
    val incidentDate: String?,
    val followup: Boolean?,
    val status: String?,
    val trackedEntityInstance: String,
    override val syncState: SyncStateDB?,
    val aggregatedSyncState: SyncStateDB?,
    val geometryType: String?,
    val geometryCoordinates: String?,
    override val deleted: Boolean?,
    val completedDate: String?,
) : EntityDB<Enrollment>, DataObjectDB, DeletableObjectDB {

    override fun toDomain(): Enrollment {
        return Enrollment.builder().apply {
            id(id?.toLong())
            uid(uid)
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(lastUpdatedAtClient.toJavaDate())
            organisationUnit(organisationUnit)
            program(program)
            enrollmentDate(enrollmentDate.toJavaDate())
            incidentDate(incidentDate.toJavaDate())
            followUp(followup)
            status?.let { status(EnrollmentStatus.valueOf(it)) }
            trackedEntityInstance(trackedEntityInstance)
            syncState?.let { syncState(it.toDomain()) }
            aggregatedSyncState?.let { aggregatedSyncState(it.toDomain()) }
            geometry(GeometryDB(geometryType, geometryCoordinates).toDomain())
            deleted(deleted)
            completedDate(completedDate.toJavaDate())
        }.build()
    }
}

internal fun Enrollment.toDB(): EnrollmentDB {
    val geometryDB = geometry().toDB()

    return EnrollmentDB(
        uid = uid(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        createdAtClient = createdAtClient().dateFormat(),
        lastUpdatedAtClient = lastUpdatedAtClient().dateFormat(),
        organisationUnit = organisationUnit()!!,
        program = program()!!,
        enrollmentDate = enrollmentDate().dateFormat(),
        incidentDate = incidentDate().dateFormat(),
        followup = followUp(),
        status = status()?.name,
        trackedEntityInstance = trackedEntityInstance()!!,
        syncState = syncState()?.toDB(),
        aggregatedSyncState = aggregatedSyncState()?.toDB(),
        geometryType = geometryDB.geometryType,
        geometryCoordinates = geometryDB.geometryCoordinates,
        deleted = deleted(),
        completedDate = completedDate().dateFormat(),
    )
}
