package org.hisp.dhis.android.persistence.indicator

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields

@Entity(
    tableName = "Indicator",
    foreignKeys = [
        ForeignKey(
            entity = IndicatorTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicatorType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class IndicatorDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    override val deleted: Boolean?,
    val annualized: Boolean?,
    val indicatorType: String?,
    val numerator: String?,
    val numeratorDescription: String?,
    val denominator: String?,
    val denominatorDescription: String?,
    val url: String?,
    val decimals: Int?,
    override val color: String?,
    override val icon: String?,
) : EntityDB<Indicator>, BaseNameableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): Indicator {
        return Indicator.builder().apply {
            applyBaseNameableFields(this@IndicatorDB)
            applyStyleFields(this@IndicatorDB)
            annualized(annualized)
            indicatorType?.let { indicatorType(ObjectWithUid.create(indicatorType)) }
            numerator(numerator)
            numeratorDescription(numeratorDescription)
            denominator(denominator)
            denominatorDescription(denominatorDescription)
            url(url)
            decimals(decimals)
        }.build()
    }
}

internal fun Indicator.toDB(): IndicatorDB {
    return IndicatorDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        annualized = annualized(),
        indicatorType = indicatorType()?.uid(),
        numerator = numerator(),
        numeratorDescription = numeratorDescription(),
        denominator = denominator(),
        denominatorDescription = denominatorDescription(),
        url = url(),
        decimals = decimals(),
        color = style()?.color(),
        icon = style()?.icon(),
        deleted = deleted(),
    )
}
