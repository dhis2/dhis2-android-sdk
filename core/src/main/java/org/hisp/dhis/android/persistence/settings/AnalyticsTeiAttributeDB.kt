package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["attribute"]),
        Index(value = ["teiSetting"]),
    ],
)
internal data class AnalyticsTeiAttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val teiSetting: String,
    val whoComponent: String?,
    val attribute: String,
) : EntityDB<AnalyticsTeiAttribute> {

    override fun toDomain(): AnalyticsTeiAttribute {
        return AnalyticsTeiAttribute.builder()
            .id(id?.toLong())
            .teiSetting(teiSetting)
            .whoComponent(whoComponent?.let { WHONutritionComponent.valueOf(it) })
            .attribute(attribute)
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
