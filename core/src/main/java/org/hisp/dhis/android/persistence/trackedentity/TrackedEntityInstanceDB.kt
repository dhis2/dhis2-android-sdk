package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.GeometryDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
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
    override val syncState: SyncStateDB?,
    val aggregatedSyncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<TrackedEntityInstance>, DataObjectDB, DeletableObjectDB {
    override fun toDomain(): TrackedEntityInstance {
        return TrackedEntityInstance.builder().apply {
            id(id?.toLong())
            uid(uid)
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            createdAtClient(createdAtClient.toJavaDate())
            lastUpdatedAtClient(lastUpdatedAtClient.toJavaDate())
            organisationUnit(organisationUnit)
            trackedEntityType(trackedEntityType)
            geometry(GeometryDB(geometryType, geometryCoordinates).toDomain())
            syncState?.let { syncState(it.toDomain()) }
            aggregatedSyncState?.let { aggregatedSyncState(it.toDomain()) }
            deleted(deleted)
        }.build()
    }
}

internal fun TrackedEntityInstance.toDB(): TrackedEntityInstanceDB {
    val geometryDB = geometry().toDB()

    return TrackedEntityInstanceDB(
        uid = uid(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        createdAtClient = createdAtClient().dateFormat(),
        lastUpdatedAtClient = lastUpdatedAtClient().dateFormat(),
        organisationUnit = organisationUnit(),
        trackedEntityType = trackedEntityType(),
        geometryType = geometryDB.geometryType,
        geometryCoordinates = geometryDB.geometryCoordinates,
        syncState = syncState()?.toDB(),
        aggregatedSyncState = aggregatedSyncState()?.toDB(),
        deleted = deleted(),
    )
}
