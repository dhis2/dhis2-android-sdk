package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val id: Int = 0,
    val programIndicator: String,
    val boundaryTarget: String?,
    val analyticsPeriodBoundaryType: String?,
    val offsetPeriods: Int?,
    val offsetPeriodType: String?,
)
