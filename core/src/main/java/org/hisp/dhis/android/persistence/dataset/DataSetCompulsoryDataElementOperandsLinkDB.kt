package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementOperandDB

@Entity(
    tableName = "DataSetCompulsoryDataElementOperandsLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementOperandDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElementOperand"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["dataSet", "dataElementOperand"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["dataElementOperand"]),
    ],
)
internal data class DataSetCompulsoryDataElementOperandsLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataSet: String,
    val dataElementOperand: String,
) : EntityDB<DataSetCompulsoryDataElementOperandLink> {

    override fun toDomain(): DataSetCompulsoryDataElementOperandLink {
        return DataSetCompulsoryDataElementOperandLink.builder()
            .dataSet(dataSet)
            .dataElementOperand(dataElementOperand)
            .build()
    }
}

internal fun DataSetCompulsoryDataElementOperandLink.toDB(): DataSetCompulsoryDataElementOperandsLinkDB {
    return DataSetCompulsoryDataElementOperandsLinkDB(
        dataSet = dataSet()!!,
        dataElementOperand = dataElementOperand()!!,
    )
}
