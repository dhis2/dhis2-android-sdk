package org.hisp.dhis.android.persistence.maintenance

import androidx.room.Entity
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "ForeignKeyViolation",
    primaryKeys = ["fromTable", "fromColumn", "toTable", "toColumn", "notFoundValue", "fromObjectUid"]
)
internal data class ForeignKeyViolationDB(
    val fromTable: String?,
    val fromColumn: String?,
    val toTable: String?,
    val toColumn: String?,
    val notFoundValue: String?,
    val fromObjectUid: String?,
    val fromObjectRow: String?,
    val created: String?,
) : EntityDB<ForeignKeyViolation> {

    override fun toDomain(): ForeignKeyViolation {
        return ForeignKeyViolation.builder()
            .fromTable(fromTable)
            .fromColumn(fromColumn)
            .toTable(toTable)
            .toColumn(toColumn)
            .notFoundValue(notFoundValue)
            .fromObjectUid(fromObjectUid)
            .fromObjectRow(fromObjectRow)
            .created(created.toJavaDate())
            .build()
    }
}

internal fun ForeignKeyViolation.toDB(): ForeignKeyViolationDB {
    return ForeignKeyViolationDB(
        fromTable = fromTable(),
        fromColumn = fromColumn(),
        toTable = toTable(),
        toColumn = toColumn(),
        notFoundValue = notFoundValue(),
        fromObjectUid = fromObjectUid(),
        fromObjectRow = fromObjectRow(),
        created = created().dateFormat(),
    )
}
