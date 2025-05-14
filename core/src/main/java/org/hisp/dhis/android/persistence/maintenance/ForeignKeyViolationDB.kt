package org.hisp.dhis.android.persistence.maintenance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate

@Entity(tableName = "ForeignKeyViolation")
internal data class ForeignKeyViolationDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val fromTable: String?,
    val fromColumn: String?,
    val toTable: String?,
    val toColumn: String?,
    val notFoundValue: String?,
    val fromObjectUid: String?,
    val fromObjectRow: String?,
    val created: String?,
) {
    fun toDomain(): ForeignKeyViolation {
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
