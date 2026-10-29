package org.hisp.dhis.android.persistence.event

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import org.hisp.dhis.android.core.event.internal.EventSync
import org.hisp.dhis.android.core.util.dateFormatNonNull
import org.hisp.dhis.android.core.util.toJavaDateNonNull
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "EventSync",
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
            name = "eventsync_program_orgunit_workinglists",
            value = ["program", "organisationUnitIdsHash", "workingListsHash"],
            unique = true,
        ),
    ],
)
internal data class EventSyncDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val program: String?,
    val organisationUnitIdsHash: Int,
    val downloadLimit: Int,
    val lastUpdated: String,
    val workingListsHash: Int?,
) : EntityDB<EventSync> {

    override fun toDomain(): EventSync {
        return EventSync.builder()
            .program(program)
            .organisationUnitIdsHash(organisationUnitIdsHash)
            .downloadLimit(downloadLimit)
            .workingListsHash(workingListsHash)
            .lastUpdated(lastUpdated.toJavaDateNonNull())
            .build()
    }
}

internal fun EventSync.toDB(): EventSyncDB {
    return EventSyncDB(
        program = program(),
        organisationUnitIdsHash = organisationUnitIdsHash(),
        downloadLimit = downloadLimit(),
        workingListsHash = workingListsHash(),
        lastUpdated = lastUpdated().dateFormatNonNull(),
    )
}
