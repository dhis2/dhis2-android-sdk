package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.DataSetElement
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "DataSetDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["dataSet", "dataElement"],
)
internal data class DataSetDataElementLinkDB(
    val dataSet: String,
    val dataElement: String,
    val categoryCombo: String?,
) : EntityDB<DataSetElement> {

    override fun toDomain(): DataSetElement {
        return DataSetElement.builder().apply {
            dataSet(ObjectWithUid.create(dataSet))
            dataElement(ObjectWithUid.create(dataElement))
            categoryCombo?.let { categoryCombo(ObjectWithUid.create(it)) }
        }.build()
    }
}

internal fun DataSetElement.toDB(): DataSetDataElementLinkDB {
    return DataSetDataElementLinkDB(
        dataSet = dataSet()!!.uid(),
        dataElement = dataElement().uid(),
        categoryCombo = categoryCombo()?.uid(),
    )
}
