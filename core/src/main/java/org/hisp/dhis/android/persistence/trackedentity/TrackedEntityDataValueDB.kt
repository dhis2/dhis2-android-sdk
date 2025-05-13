package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.event.EventDB

@Entity(
    tableName = "TrackedEntityDataValue",
    foreignKeys = [
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["event"]),
        Index(value = ["dataElement"]),
        Index(value = ["event", "dataElement"], unique = true),
    ],
)
internal data class TrackedEntityDataValueDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val event: String,
    val dataElement: String,
    val storedBy: String?,
    val value: String?,
    val created: String?,
    val lastUpdated: String?,
    val providedElsewhere: Boolean?,
    val syncState: String?,
) : EntityDB<TrackedEntityDataValue> {

    override fun toDomain(): TrackedEntityDataValue {
        return TrackedEntityDataValue.builder()
            .id(id?.toLong())
            .event(event)
            .dataElement(dataElement)
            .storedBy(storedBy)
            .value(value)
            .created(created?.toJavaDate())
            .lastUpdated(lastUpdated?.toJavaDate())
            .providedElsewhere(providedElsewhere)
            .syncState(syncState?.let { State.valueOf(it) })
            .build()
    }
}

internal fun TrackedEntityDataValue.toDB(): TrackedEntityDataValueDB {
    return TrackedEntityDataValueDB(
        event = event()!!,
        dataElement = dataElement()!!,
        storedBy = storedBy(),
        value = value(),
        created = created()?.dateFormat(),
        lastUpdated = lastUpdated()?.dateFormat(),
        providedElsewhere = providedElsewhere(),
        syncState = syncState()?.name,
    )
}
