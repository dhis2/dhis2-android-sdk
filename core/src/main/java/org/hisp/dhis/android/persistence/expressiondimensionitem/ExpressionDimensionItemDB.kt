package org.hisp.dhis.android.persistence.expressiondimensionitem

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItem
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "ExpressionDimensionItem")
internal data class ExpressionDimensionItemDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val expression: String?,
) : EntityDB<ExpressionDimensionItem>, BaseIdentifiableObjectDB {

    override fun toDomain(): ExpressionDimensionItem {
        return ExpressionDimensionItem.builder()
            .applyBaseIdentifiableFields(this@ExpressionDimensionItemDB)
            .expression(expression)
            .build()
    }
}

internal fun ExpressionDimensionItem.toDB(): ExpressionDimensionItemDB {
    return ExpressionDimensionItemDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        expression = expression(),
        deleted = deleted(),
    )
}
