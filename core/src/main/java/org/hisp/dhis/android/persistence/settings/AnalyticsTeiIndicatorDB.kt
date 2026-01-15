package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.settings.AnalyticsTeiIndicator
import org.hisp.dhis.android.core.settings.WHONutritionComponent
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramIndicatorDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "AnalyticsTeiIndicator",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
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
    primaryKeys = ["teiSetting", "indicator"],
)
internal data class AnalyticsTeiIndicatorDB(
    @ParentColumn val teiSetting: String,
    val whoComponent: String?,
    val programStage: String?,
    val indicator: String,
) : EntityDB<AnalyticsTeiIndicator> {

    override fun toDomain(): AnalyticsTeiIndicator {
        return AnalyticsTeiIndicator.builder().apply {
            teiSetting(teiSetting)
            whoComponent?.let { whoComponent(WHONutritionComponent.valueOf(it)) }
            programStage(programStage)
            indicator(indicator)
        }.build()
    }
}

internal fun AnalyticsTeiIndicator.toDB(): AnalyticsTeiIndicatorDB {
    return AnalyticsTeiIndicatorDB(
        teiSetting = teiSetting()!!,
        whoComponent = whoComponent()?.name,
        programStage = programStage(),
        indicator = indicator(),
    )
}
