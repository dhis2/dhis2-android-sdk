package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.settings.AnalyticsTeiDataElement
import org.hisp.dhis.android.core.settings.WHONutritionComponent
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "AnalyticsTeiDataElement",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
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
    primaryKeys = ["teiSetting", "dataElement"],
)
internal data class AnalyticsTeiDataElementDB(
    @ParentColumn val teiSetting: String,
    val whoComponent: String?,
    val programStage: String?,
    val dataElement: String,
) : EntityDB<AnalyticsTeiDataElement> {

    override fun toDomain(): AnalyticsTeiDataElement {
        return AnalyticsTeiDataElement.builder().apply {
            teiSetting(teiSetting)
            whoComponent?.let { whoComponent(WHONutritionComponent.valueOf(it)) }
            programStage(programStage)
            dataElement(dataElement)
        }.build()
    }
}

internal fun AnalyticsTeiDataElement.toDB(): AnalyticsTeiDataElementDB {
    return AnalyticsTeiDataElementDB(
        teiSetting = teiSetting()!!,
        whoComponent = whoComponent()?.name,
        programStage = programStage(),
        dataElement = dataElement(),
    )
}
