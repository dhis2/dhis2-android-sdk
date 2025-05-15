package org.hisp.dhis.android.persistence.indicator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataset.DataSetDB

@Entity(
    tableName = "DataSetIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["dataSet", "indicator"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["indicator"]),
    ],
)
internal data class DataSetIndicatorLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataSet: String,
    val indicator: String,
) : EntityDB<DataSetIndicatorLink> {

    override fun toDomain(): DataSetIndicatorLink {
        return DataSetIndicatorLink.builder()
            .id(id?.toLong())
            .dataSet(dataSet)
            .indicator(indicator)
            .build()
    }
}

internal fun DataSetIndicatorLink.toDB(): DataSetIndicatorLinkDB {
    return DataSetIndicatorLinkDB(
        dataSet = dataSet()!!,
        indicator = indicator()!!,
    )
}
