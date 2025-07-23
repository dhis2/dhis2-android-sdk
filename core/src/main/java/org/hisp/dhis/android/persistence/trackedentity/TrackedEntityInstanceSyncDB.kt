package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceSync
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "TrackedEntityInstanceSync",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["program", "organisationUnitIdsHash"],
)
internal data class TrackedEntityInstanceSyncDB(
    val program: String?,
    val organisationUnitIdsHash: Int?,
    val downloadLimit: Int,
    val lastUpdated: String,
) : EntityDB<TrackedEntityInstanceSync> {
    override fun toDomain(): TrackedEntityInstanceSync {
        return TrackedEntityInstanceSync.builder()
            .program(program)
            .organisationUnitIdsHash(organisationUnitIdsHash!!)
            .downloadLimit(downloadLimit)
            .lastUpdated(lastUpdated.toJavaDate())
            .build()
    }
}

internal fun TrackedEntityInstanceSync.toDB(): TrackedEntityInstanceSyncDB {
    return TrackedEntityInstanceSyncDB(
        program = program(),
        organisationUnitIdsHash = organisationUnitIdsHash(),
        downloadLimit = downloadLimit(),
        lastUpdated = lastUpdated().dateFormat()!!,
    )
}
