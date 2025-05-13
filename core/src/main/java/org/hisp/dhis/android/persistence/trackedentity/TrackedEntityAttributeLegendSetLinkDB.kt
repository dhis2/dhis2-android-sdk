package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeLegendSetLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.legendset.LegendSetDB

@Entity(
    tableName = "TrackedEntityAttributeLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = LegendSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["legendSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["trackedEntityAttribute", "legendSet"], unique = true),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["legendSet"]),
    ],
)
internal data class TrackedEntityAttributeLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val trackedEntityAttribute: String,
    val legendSet: String,
    val sortOrder: Int?,
) : EntityDB<TrackedEntityAttributeLegendSetLink> {

    override fun toDomain(): TrackedEntityAttributeLegendSetLink {
        return TrackedEntityAttributeLegendSetLink.builder()
            .id(id?.toLong())
            .trackedEntityAttribute(trackedEntityAttribute)
            .legendSet(legendSet)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun TrackedEntityAttributeLegendSetLink.toDB(): TrackedEntityAttributeLegendSetLinkDB {
    return TrackedEntityAttributeLegendSetLinkDB(
        trackedEntityAttribute = trackedEntityAttribute(),
        legendSet = legendSet(),
        sortOrder = sortOrder(),
    )
}
