package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.settings.AnalyticsTeiAttribute
import org.hisp.dhis.android.core.settings.WHONutritionComponent
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "AnalyticsTeiAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = AnalyticsTeiSettingDB::class,
            parentColumns = ["uid"],
            childColumns = ["teiSetting"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["teiSetting", "attribute"],
)
internal data class AnalyticsTeiAttributeDB(
    val teiSetting: String,
    val whoComponent: String?,
    val attribute: String,
) : EntityDB<AnalyticsTeiAttribute> {

    override fun toDomain(): AnalyticsTeiAttribute {
        return AnalyticsTeiAttribute.builder().apply {
            teiSetting(teiSetting)
            whoComponent?.let { whoComponent(WHONutritionComponent.valueOf(it)) }
            attribute(attribute)
        }
            .build()
    }
}

internal fun AnalyticsTeiAttribute.toDB(): AnalyticsTeiAttributeDB {
    return AnalyticsTeiAttributeDB(
        teiSetting = teiSetting()!!,
        whoComponent = whoComponent()?.name,
        attribute = attribute(),
    )
}
