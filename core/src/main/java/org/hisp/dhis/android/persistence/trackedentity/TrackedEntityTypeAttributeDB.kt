package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "TrackedEntityTypeAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["trackedEntityType"]),
        Index(value = ["trackedEntityAttribute"]),
    ],
)
internal data class TrackedEntityTypeAttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val trackedEntityType: String?,
    val trackedEntityAttribute: String?,
    val displayInList: Boolean?,
    val mandatory: Boolean?,
    val searchable: Boolean?,
    val sortOrder: Int?,
) : EntityDB<TrackedEntityTypeAttribute> {
    override fun toDomain(): TrackedEntityTypeAttribute {
        return TrackedEntityTypeAttribute.builder()
            .trackedEntityType(ObjectWithUid.create(trackedEntityType))
            .trackedEntityAttribute(trackedEntityAttribute?.let { ObjectWithUid.create(it) })
            .displayInList(displayInList)
            .mandatory(mandatory?.let { it })
            .searchable(searchable)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun TrackedEntityTypeAttribute.toDB(): TrackedEntityTypeAttributeDB {
    return TrackedEntityTypeAttributeDB(
        trackedEntityType = trackedEntityType().uid(),
        trackedEntityAttribute = trackedEntityAttribute()?.uid(),
        displayInList = displayInList(),
        mandatory = mandatory(),
        searchable = searchable(),
        sortOrder = sortOrder(),
    )
}
