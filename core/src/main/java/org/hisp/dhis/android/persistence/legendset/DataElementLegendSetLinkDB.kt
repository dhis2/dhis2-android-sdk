package org.hisp.dhis.android.persistence.legendset

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["dataElement", "legendSet"],
)
internal data class DataElementLegendSetLinkDB(
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
