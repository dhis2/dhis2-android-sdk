package org.hisp.dhis.android.persistence.dataelement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB

@Entity(
    tableName = "DataElementOperand",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["", ""],
)
internal data class DataElementOperandDB(
    @PrimaryKey
    val uid: String,
    val dataElement: String?,
    val categoryOptionCombo: String?,
) : EntityDB<DataElementOperand> {

    override fun toDomain(): DataElementOperand {
        return DataElementOperand.builder().apply {
            uid(uid)
            deleted(false)
            dataElement?.let { dataElement(ObjectWithUidDB(it).toDomain()) }
            categoryOptionCombo?.let { categoryOptionCombo(ObjectWithUidDB(it).toDomain()) }
        }.build()
    }
}

internal fun DataElementOperand.toDB(): DataElementOperandDB {
    return DataElementOperandDB(
        uid = uid()!!,
        dataElement = dataElement()?.uid(),
        categoryOptionCombo = categoryOptionCombo()?.uid(),
    )
}
