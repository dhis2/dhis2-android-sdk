package org.hisp.dhis.android.persistence.period

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "Period",
    indices = [
        Index(value = ["periodId"], unique = true),
    ],
)
internal data class PeriodDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val periodId: String?,
    val periodType: String?,
    val startDate: String?,
    val endDate: String?,
) : EntityDB<Period> {

    override fun toDomain(): Period {
        return Period.builder()
            .id(id?.toLong())
            .periodId(periodId)
            .periodType(periodType?.let { PeriodType.valueOf(it) })
            .startDate(startDate.toJavaDate())
            .endDate(endDate.toJavaDate())
            .build()
    }
}

internal fun Period.toDB(): PeriodDB {
    return PeriodDB(
        periodId = periodId(),
        periodType = periodType()?.name,
        startDate = startDate().dateFormat(),
        endDate = endDate().dateFormat(),
    )
}
