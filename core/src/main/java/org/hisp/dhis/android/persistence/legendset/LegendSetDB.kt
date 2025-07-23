package org.hisp.dhis.android.persistence.legendset

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "LegendSet")
internal data class LegendSetDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val symbolizer: String?,
) : EntityDB<LegendSet>, BaseIdentifiableObjectDB {

    override fun toDomain(): LegendSet {
        return LegendSet.builder()
            .applyBaseIdentifiableFields(this@LegendSetDB)
            .symbolizer(symbolizer)
            .build()
    }
}

internal fun LegendSet.toDB(): LegendSetDB {
    return LegendSetDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        symbolizer = symbolizer(),
    )
}
