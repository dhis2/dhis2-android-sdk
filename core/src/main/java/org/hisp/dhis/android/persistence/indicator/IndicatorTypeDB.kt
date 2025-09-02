package org.hisp.dhis.android.persistence.indicator

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "IndicatorType")
internal data class IndicatorTypeDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val number: Boolean?,
    val factor: Int?,
) : EntityDB<IndicatorType>, BaseIdentifiableObjectDB {

    override fun toDomain(): IndicatorType {
        return IndicatorType.builder()
            .applyBaseIdentifiableFields(this@IndicatorTypeDB)
            .number(number)
            .factor(factor)
            .build()
    }
}

internal fun IndicatorType.toDB(): IndicatorTypeDB {
    return IndicatorTypeDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        number = number(),
        factor = factor(),
    )
}
