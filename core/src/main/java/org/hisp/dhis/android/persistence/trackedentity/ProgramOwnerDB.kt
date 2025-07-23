package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "ProgramOwner",
    foreignKeys = [
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
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["ownerOrgUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["program", "trackedEntityInstance"],
)
internal data class ProgramOwnerDB(
    val program: String,
    val trackedEntityInstance: String,
    val ownerOrgUnit: String,
    override val syncState: SyncStateDB?,
) : EntityDB<ProgramOwner>, DataObjectDB {

    override fun toDomain(): ProgramOwner {
        return ProgramOwner.builder().apply {
            program(program)
            trackedEntityInstance(trackedEntityInstance)
            ownerOrgUnit(ownerOrgUnit)
            syncState?.let { syncState(it.toDomain()) }
        }.build()
    }
}

internal fun ProgramOwner.toDB(): ProgramOwnerDB {
    return ProgramOwnerDB(
        program = program(),
        trackedEntityInstance = trackedEntityInstance(),
        ownerOrgUnit = ownerOrgUnit(),
        syncState = syncState()?.toDB(),
    )
}
