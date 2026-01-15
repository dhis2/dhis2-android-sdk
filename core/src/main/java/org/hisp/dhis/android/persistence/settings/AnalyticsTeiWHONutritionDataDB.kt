package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionData
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionGender
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionGenderValues
import org.hisp.dhis.android.core.settings.WHONutritionChartType
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "AnalyticsTeiWHONutritionData",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["genderAttribute"],
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
    primaryKeys = ["teiSetting", "genderAttribute"],
)
internal data class AnalyticsTeiWHONutritionDataDB(
    @ParentColumn val teiSetting: String,
    val chartType: String?,
    val genderAttribute: String,
    val genderFemale: String?,
    val genderMale: String?,
) : EntityDB<AnalyticsTeiWHONutritionData> {

    override fun toDomain(): AnalyticsTeiWHONutritionData {
        return AnalyticsTeiWHONutritionData.builder().apply {
            teiSetting(teiSetting)
            chartType?.let { chartType(WHONutritionChartType.valueOf(it)) }
            gender(
                AnalyticsTeiWHONutritionGender.builder()
                    .attribute(genderAttribute)
                    .values(
                        AnalyticsTeiWHONutritionGenderValues.builder()
                            .female(genderFemale)
                            .male(genderMale)
                            .build(),
                    )
                    .build(),
            )
        }.build()
    }
}

internal fun AnalyticsTeiWHONutritionData.toDB(): AnalyticsTeiWHONutritionDataDB {
    return AnalyticsTeiWHONutritionDataDB(
        teiSetting = teiSetting()!!,
        chartType = chartType()?.name,
        genderAttribute = gender().attribute(),
        genderFemale = gender().values().female(),
        genderMale = gender().values().male(),
    )
}
