package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.DataInputPeriod
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "DataInputPeriod",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["dataSet", "period", "openingDate", "closingDate"],
)
internal data class DataInputPeriodDB(
    val dataSet: String,
    val period: String,
    val openingDate: String?,
    val closingDate: String?,
) : EntityDB<DataInputPeriod> {

    override fun toDomain(): DataInputPeriod {
        return DataInputPeriod.builder()
            .dataSet(ObjectWithUid.create(dataSet))
            .period(ObjectWithUid.create(period))
            .openingDate(openingDate.toJavaDate())
            .closingDate(closingDate.toJavaDate())
            .build()
    }
}

internal fun DataInputPeriod.toDB(): DataInputPeriodDB {
    return DataInputPeriodDB(
        dataSet = dataSet()!!.uid(),
        period = period().uid(),
        openingDate = openingDate().dateFormat(),
        closingDate = closingDate().dateFormat(),
    )
}
