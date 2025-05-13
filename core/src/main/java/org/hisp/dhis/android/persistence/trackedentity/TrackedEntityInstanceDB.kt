package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.GeometryDB
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB

@Entity(
    tableName = "TrackedEntityInstance",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["trackedEntityType"]),
    ],
)
internal data class TrackedEntityInstanceDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val organisationUnit: String?,
    val trackedEntityType: String?,
    val geometryType: String?,
    val geometryCoordinates: String?,
    val syncState: String?,
    val aggregatedSyncState: String?,
    val deleted: Boolean?,
) : EntityDB<TrackedEntityInstance> {
    override fun toDomain(): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .id(id?.toLong())
            .uid(uid)
            .created(created?.toJavaDate())
            .lastUpdated(lastUpdated?.toJavaDate())
            .createdAtClient(createdAtClient?.toJavaDate())
            .lastUpdatedAtClient(lastUpdatedAtClient?.toJavaDate())
            .organisationUnit(organisationUnit)
            .trackedEntityType(trackedEntityType)
            .geometry(GeometryDB(geometryType, geometryCoordinates).toDomain())
            .syncState(syncState?.let { State.valueOf(it) })
            .aggregatedSyncState(aggregatedSyncState?.let { State.valueOf(it) })
            .deleted(deleted)
            .build()
    }
}

internal fun TrackedEntityInstance.toDB(): TrackedEntityInstanceDB {
    return TrackedEntityInstanceDB(
        uid = uid(),
        created = created()?.dateFormat(),
        lastUpdated = lastUpdated()?.dateFormat(),
        createdAtClient = createdAtClient()?.dateFormat(),
        lastUpdatedAtClient = lastUpdatedAtClient()?.dateFormat(),
        organisationUnit = organisationUnit(),
        trackedEntityType = trackedEntityType(),
        geometryType = geometry().toDB().geometryType,
        geometryCoordinates = geometry().toDB().geometryCoordinates,
        syncState = syncState()?.name,
        aggregatedSyncState = aggregatedSyncState()?.name,
        deleted = deleted(),
    )
}
