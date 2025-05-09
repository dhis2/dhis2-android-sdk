package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationOutputType
import org.hisp.dhis.android.core.visualization.TrackerVisualizationType
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeDB

@Entity(
    tableName = "TrackerVisualization",
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
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["trackedEntityType"]),
    ],
)
internal data class TrackerVisualizationDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val description: String?,
    val displayDescription: String?,
    val type: String?,
    val outputType: String?,
    val program: String?,
    val programStage: String?,
    val trackedEntityType: String?,
) {
    fun toDomain(): TrackerVisualization {
        return TrackerVisualization.builder()
            .uid(uid)
            .code(code)
            .name(name)
            .displayName(displayName)
            .created(created.toJavaDate())
            .lastUpdated(lastUpdated.toJavaDate())
            .description(description)
            .displayDescription(displayDescription)
            .type(type?.let { TrackerVisualizationType.valueOf(it) })
            .outputType(outputType?.let { TrackerVisualizationOutputType.valueOf(it) })
            .program(ObjectWithUid.create(program))
            .programStage(ObjectWithUid.create(programStage))
            .trackedEntityType(ObjectWithUid.create(trackedEntityType))
            .build()
    }
}

internal fun TrackerVisualization.toDB(): TrackerVisualizationDB {
    return TrackerVisualizationDB(
        uid = this.uid(),
        code = this.code(),
        name = this.name(),
        displayName = this.displayName(),
        created = this.created().dateFormat(),
        lastUpdated = this.lastUpdated().dateFormat(),
        description = this.description(),
        displayDescription = this.displayDescription(),
        type = this.type()?.name,
        outputType = this.outputType()?.name,
        program = this.program()?.uid(),
        programStage = this.programStage()?.uid(),
        trackedEntityType = this.trackedEntityType()?.uid(),
    )
}
