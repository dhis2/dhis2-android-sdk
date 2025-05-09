package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimension
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.persistence.visualization.TrackerVisualizationDimensionRepetitionDB.Companion.toDB
import org.json.JSONArray

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
    val id: Int? = 0,
    val trackerVisualization: String,
    val position: String,
    val dimension: String,
    val dimensionType: String?,
    val program: String?,
    val programStage: String?,
    val items: String?,
    val filter: String?,
    val repetition: String?,
) {
    fun toDomain(): TrackerVisualizationDimension {
        return TrackerVisualizationDimension.builder()
            .trackerVisualization(trackerVisualization)
            .position(position.let { LayoutPosition.valueOf(it) })
            .dimension(dimension)
            .dimensionType(dimensionType)
            .program(ObjectWithUid.create(program))
            .programStage(ObjectWithUid.create(programStage))
            .items(items?.let {
                val jsonArray = JSONArray(it)
                (0 until jsonArray.length()).map { i ->
                    ObjectWithUid.create(jsonArray.getString(i))
                }
            })
            .filter(filter)
            .repetition(repetition?.let {
                KotlinxJsonParser.instance.decodeFromString<TrackerVisualizationDimensionRepetitionDB>(
                    it,
                ).toDomain()
            })
            .build()
    }
}

internal fun TrackerVisualizationDimension.toDB(): TrackerVisualizationDimensionDB {
    return TrackerVisualizationDimensionDB(
        trackerVisualization = trackerVisualization()!!,
        position = position()!!.name,
        dimension = dimension()!!,
        dimensionType = dimensionType(),
        program = program()?.uid(),
        programStage = programStage()?.uid(),
        items = items()?.let { items ->
            JSONArray().apply {
                items.forEach { put(it.uid()) }
            }.toString()
        },
        filter = filter(),
        repetition = repetition()?.let {
            KotlinxJsonParser.instance.encodeToString(
                TrackerVisualizationDimensionRepetitionDB.serializer(),
                it.toDB(),
            )
        }
    )
}
