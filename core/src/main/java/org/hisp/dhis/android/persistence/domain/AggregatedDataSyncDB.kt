package org.hisp.dhis.android.persistence.domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataSync
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataset.DataSetDB

@Entity(
    tableName = "AggregatedDataSync",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class AggregatedDataSyncDB(
    @PrimaryKey
    val dataSet: String,
    val periodType: String,
    val pastPeriods: Int,
    val futurePeriods: Int,
    val dataElementsHash: Int,
    val organisationUnitsHash: Int,
    val lastUpdated: String,
) : EntityDB<AggregatedDataSync> {

    override fun toDomain(): AggregatedDataSync {
        return AggregatedDataSync.builder()
            .dataSet(dataSet)
            .periodType(PeriodType.valueOf(periodType))
            .pastPeriods(pastPeriods)
            .futurePeriods(futurePeriods)
            .dataElementsHash(dataElementsHash)
            .organisationUnitsHash(organisationUnitsHash)
            .lastUpdated(lastUpdated.toJavaDate())
            .build()
    }
}

internal fun AggregatedDataSync.toDB(): AggregatedDataSyncDB {
    return AggregatedDataSyncDB(
        dataSet = dataSet(),
        periodType = periodType().name,
        pastPeriods = pastPeriods(),
        futurePeriods = futurePeriods(),
        dataElementsHash = dataElementsHash(),
        organisationUnitsHash = organisationUnitsHash(),
        lastUpdated = lastUpdated().dateFormat()!!,
    )
}
