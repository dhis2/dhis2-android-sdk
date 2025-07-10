package org.hisp.dhis.android.persistence.legendset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.legendset.DataElementLegendSetLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "DataElementLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
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
        Index(value = ["dataElement", "legendSet"], unique = true),
        Index(value = ["dataElement"]),
        Index(value = ["legendSet"]),
    ],
)
internal data class DataElementLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataElement: String,
    val legendSet: String,
    val sortOrder: Int?,
) : EntityDB<DataElementLegendSetLink> {

    override fun toDomain(): DataElementLegendSetLink {
        return DataElementLegendSetLink.builder()
            .dataElement(dataElement)
            .legendSet(legendSet)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun DataElementLegendSetLink.toDB(): DataElementLegendSetLinkDB {
    return DataElementLegendSetLinkDB(
        dataElement = dataElement()!!,
        legendSet = legendSet()!!,
        sortOrder = sortOrder(),
    )
}
