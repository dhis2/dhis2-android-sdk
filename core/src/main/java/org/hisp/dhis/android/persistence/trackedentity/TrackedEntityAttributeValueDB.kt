package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.itemfilter.toDB

@Entity(
    tableName = "TrackedEntityAttributeValue",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
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
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["trackedEntityInstance"]),
        Index(value = ["trackedEntityInstance", "trackedEntityAttribute"], unique = true),
    ],
)
internal data class TrackedEntityAttributeValueDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val created: String?,
    val lastUpdated: String?,
    val value: String?,
    val trackedEntityAttribute: String,
    val trackedEntityInstance: String,
    override val syncState: SyncStateDB?,
) : EntityDB<TrackedEntityAttributeValue>, DataObjectDB {

    override fun toDomain(): TrackedEntityAttributeValue {
        return TrackedEntityAttributeValue.builder().apply {
            id(id?.toLong())
            created(created.toJavaDate())
            lastUpdated(lastUpdated.toJavaDate())
            value(value)
            trackedEntityAttribute(trackedEntityAttribute)
            trackedEntityInstance(trackedEntityInstance)
            syncState?.let { syncState(it.toDomain()) }
        }.build()
    }
}

internal fun TrackedEntityAttributeValue.toDB(): TrackedEntityAttributeValueDB {
    return TrackedEntityAttributeValueDB(
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        value = value(),
        trackedEntityAttribute = trackedEntityAttribute()!!,
        trackedEntityInstance = trackedEntityInstance()!!,
        syncState = syncState()?.toDB(),
    )
}
