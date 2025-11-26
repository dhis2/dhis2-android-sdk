package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.settings.AnalyticsTeiSetting
import org.hisp.dhis.android.core.settings.ChartType
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "AnalyticsTeiSetting",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class AnalyticsTeiSettingDB(
    @PrimaryKey
    val uid: String,
    val name: String,
    val shortName: String,
    val program: String,
    val programStage: String?,
    val period: String?,
    val type: String,
) : EntityDB<AnalyticsTeiSetting> {

    override fun toDomain(): AnalyticsTeiSetting {
        return AnalyticsTeiSetting.builder().apply {
            uid(uid)
            name(name)
            shortName(shortName)
            program(program)
            programStage(programStage)
            period?.let { period(PeriodType.valueOf(it)) }
            type(ChartType.valueOf(type))
        }.build()
    }
}

internal fun AnalyticsTeiSetting.toDB(): AnalyticsTeiSettingDB {
    return AnalyticsTeiSettingDB(
        uid = uid(),
        name = name(),
        shortName = shortName(),
        program = program(),
        programStage = programStage(),
        period = period()?.name,
        type = type().name,
    )
}
