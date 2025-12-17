package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(
            name = "teisync_program_orgunit_workinglists",
            value = ["program", "organisationUnitIdsHash", "workingListsHash"],
            unique = true,
        ),
    ],
)
internal data class TrackedEntityInstanceSyncDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val program: String?,
    val organisationUnitIdsHash: Int,
    val downloadLimit: Int,
    val lastUpdated: String,
    val workingListsHash: Int?,
) : EntityDB<TrackedEntityInstanceSync> {
    override fun toDomain(): TrackedEntityInstanceSync {
        return TrackedEntityInstanceSync.builder()
            .program(program)
            .organisationUnitIdsHash(organisationUnitIdsHash)
            .downloadLimit(downloadLimit)
            .workingListsHash(workingListsHash)
            .lastUpdated(lastUpdated.toJavaDate())
            .build()
    }
}

internal fun TrackedEntityInstanceSync.toDB(): TrackedEntityInstanceSyncDB {
    return TrackedEntityInstanceSyncDB(
        program = program(),
        organisationUnitIdsHash = organisationUnitIdsHash(),
        downloadLimit = downloadLimit(),
        workingListsHash = workingListsHash(),
        lastUpdated = lastUpdated().dateFormat()!!,
    )
}
