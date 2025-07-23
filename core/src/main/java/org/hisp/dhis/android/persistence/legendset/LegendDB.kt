package org.hisp.dhis.android.persistence.legendset

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "Legend",
    foreignKeys = [
        ForeignKey(
            entity = LegendSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["legendSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class LegendDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val startValue: Double?,
    val endValue: Double?,
    val color: String?,
    val legendSet: String?,
) : EntityDB<Legend>, BaseIdentifiableObjectDB {

    override fun toDomain(): Legend {
        return Legend.builder().apply {
            applyBaseIdentifiableFields(this@LegendDB)
            startValue(startValue)
            endValue(endValue)
            color(color)
            legendSet?.let { legendSet(ObjectWithUid.create(it)) }
        }.build()
    }
}

internal fun Legend.toDB(): LegendDB {
    return LegendDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        startValue = startValue(),
        endValue = endValue(),
        color = color(),
        legendSet = legendSet()?.uid(),
    )
}
