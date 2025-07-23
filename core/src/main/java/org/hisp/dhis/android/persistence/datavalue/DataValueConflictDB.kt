package org.hisp.dhis.android.persistence.datavalue

import androidx.room.Entity
import org.hisp.dhis.android.core.datavalue.DataValueConflict
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "DataValueConflict",
    primaryKeys = ["dataElement", "period", "orgUnit", "categoryOptionCombo", "attributeOptionCombo"],
)
internal data class DataValueConflictDB(
    val conflict: String?,
    val value: String?,
    val attributeOptionCombo: String?,
    val categoryOptionCombo: String?,
    val dataElement: String?,
    val period: String?,
    val orgUnit: String?,
    val errorCode: String?,
    val status: String?,
    val created: String?,
    val displayDescription: String?,
) : EntityDB<DataValueConflict> {

    override fun toDomain(): DataValueConflict {
        return DataValueConflict.builder().apply {
            conflict(conflict)
            value(value)
            attributeOptionCombo(attributeOptionCombo)
            categoryOptionCombo(categoryOptionCombo)
            dataElement(dataElement)
            period(period)
            orgUnit(orgUnit)
            errorCode(errorCode)
            status?.let { status(ImportStatus.valueOf(it)) }
            created(created.toJavaDate())
            displayDescription(displayDescription)
        }.build()
    }
}

internal fun DataValueConflict.toDB(): DataValueConflictDB {
    return DataValueConflictDB(
        conflict = conflict(),
        value = value(),
        attributeOptionCombo = attributeOptionCombo(),
        categoryOptionCombo = categoryOptionCombo(),
        dataElement = dataElement(),
        period = period(),
        orgUnit = orgUnit(),
        errorCode = errorCode(),
        status = status()?.name,
        created = created().dateFormat(),
        displayDescription = displayDescription(),
    )
}
