package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "TrackerVisualizationDimension",
    foreignKeys = [
        ForeignKey(
            entity = TrackerVisualizationDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackerVisualization"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
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
    indices = [
        Index(value = ["trackerVisualization"]),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
    ],
)
internal data class TrackerVisualizationDimensionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val trackerVisualization: String,
    val position: String,
    val dimension: String,
    val dimensionType: String?,
    val program: String?,
    val programStage: String?,
    val items: String?,
    val filter: String?,
    val repetition: String?,
)
