package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "AnalyticsPeriodBoundary",
    foreignKeys = [
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programIndicator", "boundaryTarget", "analyticsPeriodBoundaryType"],
)
internal data class AnalyticsPeriodBoundaryDB(
    val programIndicator: String,
    val boundaryTarget: String?,
    val analyticsPeriodBoundaryType: String?,
    val offsetPeriods: Int?,
    val offsetPeriodType: String?,
) : EntityDB<AnalyticsPeriodBoundary> {

    override fun toDomain(): AnalyticsPeriodBoundary {
        return AnalyticsPeriodBoundary.builder().apply {
            programIndicator(programIndicator)
            boundaryTarget(boundaryTarget)
            analyticsPeriodBoundaryType?.let { analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.valueOf(it)) }
            offsetPeriods(offsetPeriods)
            offsetPeriodType?.let { offsetPeriodType(PeriodType.valueOf(it)) }
        }.build()
    }
}

internal fun AnalyticsPeriodBoundary.toDB(): AnalyticsPeriodBoundaryDB {
    return AnalyticsPeriodBoundaryDB(
        programIndicator = programIndicator()!!,
        boundaryTarget = boundaryTarget(),
        analyticsPeriodBoundaryType = analyticsPeriodBoundaryType()?.name,
        offsetPeriods = offsetPeriods(),
        offsetPeriodType = offsetPeriodType()?.name,
    )
}
