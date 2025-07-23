package org.hisp.dhis.android.persistence.legendset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.legendset.IndicatorLegendSetLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.indicator.IndicatorDB

@Entity(
    tableName = "IndicatorLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
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
    primaryKeys = ["indicator", "legendSet"],
)
internal data class IndicatorLegendSetLinkDB(
    val indicator: String,
    val legendSet: String,
    val sortOrder: Int?,
) : EntityDB<IndicatorLegendSetLink> {

    override fun toDomain(): IndicatorLegendSetLink {
        return IndicatorLegendSetLink.builder()
            .indicator(indicator)
            .legendSet(legendSet)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun IndicatorLegendSetLink.toDB(): IndicatorLegendSetLinkDB {
    return IndicatorLegendSetLinkDB(
        indicator = indicator()!!,
        legendSet = legendSet()!!,
        sortOrder = sortOrder(),
    )
}
