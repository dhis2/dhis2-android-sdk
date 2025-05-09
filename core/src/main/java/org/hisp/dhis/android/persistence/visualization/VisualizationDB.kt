package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Visualization",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class VisualizationDB(
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
    val displayFormName: String?,
    val title: String?,
    val displayTitle: String?,
    val subtitle: String?,
    val displaySubtitle: String?,
    val type: String?,
    val hideTitle: Int?,
    val hideSubtitle: Int?,
    val hideEmptyColumns: Int?,
    val hideEmptyRows: Int?,
    val hideEmptyRowItems: String?,
    val hideLegend: Int?,
    val showHierarchy: Int?,
    val rowTotals: Int?,
    val rowSubTotals: Int?,
    val colTotals: Int?,
    val colSubTotals: Int?,
    val showDimensionLabels: Int?,
    val percentStackedValues: Int?,
    val noSpaceBetweenColumns: Int?,
    val skipRounding: Int?,
    val displayDensity: String?,
    val digitGroupSeparator: String?,
    val legendShowKey: String?,
    val legendStyle: String?,
    val legendSetId: String?,
    val legendStrategy: String?,
    val aggregationType: String?,
)
