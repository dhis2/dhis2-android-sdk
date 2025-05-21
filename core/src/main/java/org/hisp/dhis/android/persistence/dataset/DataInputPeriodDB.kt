package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["dataSet"]),
    ],
)
internal data class DataInputPeriodDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataSet: String,
    val period: String,
    val openingDate: String?,
    val closingDate: String?,
) : EntityDB<DataInputPeriod> {

    override fun toDomain(): DataInputPeriod {
        return DataInputPeriod.builder()
            .id(id?.toLong())
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
