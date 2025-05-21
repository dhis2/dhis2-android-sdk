package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["programIndicator"]),
    ],
)
internal data class AnalyticsPeriodBoundaryDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val programIndicator: String,
    val boundaryTarget: String?,
    val analyticsPeriodBoundaryType: String?,
    val offsetPeriods: Int?,
    val offsetPeriodType: String?,
) : EntityDB<AnalyticsPeriodBoundary> {

    override fun toDomain(): AnalyticsPeriodBoundary {
        return AnalyticsPeriodBoundary.builder().apply {
            id(id?.toLong())
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
        id = id()?.toInt(),
        programIndicator = programIndicator()!!,
        boundaryTarget = boundaryTarget(),
        analyticsPeriodBoundaryType = analyticsPeriodBoundaryType()?.name,
        offsetPeriods = offsetPeriods(),
        offsetPeriodType = offsetPeriodType()?.name,
    )
}
