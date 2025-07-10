package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["program", "trackedEntityInstance"], unique = true),
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"]),
        Index(value = ["ownerOrgUnit"]),
    ],
)
internal data class ProgramOwnerDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
