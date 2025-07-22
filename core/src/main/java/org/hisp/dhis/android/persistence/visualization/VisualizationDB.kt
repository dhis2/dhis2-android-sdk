package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.visualization.DigitGroupSeparator
import org.hisp.dhis.android.core.visualization.DisplayDensity
import org.hisp.dhis.android.core.visualization.HideEmptyItemStrategy
import org.hisp.dhis.android.core.visualization.LegendStrategy
import org.hisp.dhis.android.core.visualization.LegendStyle
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationLegend
import org.hisp.dhis.android.core.visualization.VisualizationType
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

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
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val description: String?,
    val displayDescription: String?,
    val displayFormName: String?,
    val title: String?,
    val displayTitle: String?,
    val subtitle: String?,
    val displaySubtitle: String?,
    val type: String?,
    val hideTitle: Boolean?,
    val hideSubtitle: Boolean?,
    val hideEmptyColumns: Boolean?,
    val hideEmptyRows: Boolean?,
    val hideEmptyRowItems: String?,
    val hideLegend: Boolean?,
    val showHierarchy: Boolean?,
    val rowTotals: Boolean?,
    val rowSubTotals: Boolean?,
    val colTotals: Boolean?,
    val colSubTotals: Boolean?,
    val showDimensionLabels: Boolean?,
    val percentStackedValues: Boolean?,
    val noSpaceBetweenColumns: Boolean?,
    val skipRounding: Boolean?,
    val displayDensity: String?,
    val digitGroupSeparator: String?,
    val legendShowKey: String?,
    val legendStyle: String?,
    val legendSetId: String?,
    val legendStrategy: String?,
    val aggregationType: String?,
) : EntityDB<Visualization>, BaseIdentifiableObjectDB {
    override fun toDomain(): Visualization {
        return Visualization.builder().apply {
            applyBaseIdentifiableFields(this@VisualizationDB)
            description(description)
            displayDescription(displayDescription)
            displayFormName(displayFormName)
            title(title)
            displayTitle(displayTitle)
            subtitle(subtitle)
            displaySubtitle(displaySubtitle)
            type(type?.let { VisualizationType.valueOf(it) })
            hideTitle(hideTitle)
            hideSubtitle(hideSubtitle)
            hideEmptyColumns(hideEmptyColumns)
            hideEmptyRows(hideEmptyRows)
            hideEmptyRowItems(hideEmptyRowItems?.let { HideEmptyItemStrategy.valueOf(it) })
            hideLegend(hideLegend)
            showHierarchy(showHierarchy)
            rowTotals(rowTotals)
            rowSubTotals(rowSubTotals)
            colTotals(colTotals)
            colSubTotals(colSubTotals)
            showDimensionLabels(showDimensionLabels)
            percentStackedValues(percentStackedValues)
            noSpaceBetweenColumns(noSpaceBetweenColumns)
            skipRounding(skipRounding)
            legend(
                if (sequenceOf(legendShowKey, legendStyle, legendSetId, legendStrategy).any { it != null }) {
                    VisualizationLegend.builder()
                        .showKey(legendShowKey == "1")
                        .style(legendStyle?.let { LegendStyle.valueOf(it) })
                        .set(legendSetId?.let { ObjectWithUid.create(it) })
                        .strategy(legendStrategy?.let { LegendStrategy.valueOf(it) })
                        .build()
                } else {
                    null
                },
            )
            displayDensity(displayDensity?.let { DisplayDensity.valueOf(it) })
            digitGroupSeparator(digitGroupSeparator?.let { DigitGroupSeparator.valueOf(it) })
            aggregationType(aggregationType?.let { AggregationType.valueOf(it) })
        }.build()
    }
}

internal fun Visualization.toDB(): VisualizationDB {
    return VisualizationDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        description = description(),
        displayDescription = displayDescription(),
        displayFormName = displayFormName(),
        title = title(),
        displayTitle = displayTitle(),
        subtitle = subtitle(),
        displaySubtitle = displaySubtitle(),
        type = type()?.name,
        hideTitle = hideTitle(),
        hideSubtitle = hideSubtitle(),
        hideEmptyColumns = hideEmptyColumns(),
        hideEmptyRows = hideEmptyRows(),
        hideEmptyRowItems = hideEmptyRowItems()?.name,
        hideLegend = hideLegend(),
        showHierarchy = showHierarchy(),
        rowTotals = rowTotals(),
        rowSubTotals = rowSubTotals(),
        colTotals = colTotals(),
        colSubTotals = colSubTotals(),
        showDimensionLabels = showDimensionLabels(),
        percentStackedValues = percentStackedValues(),
        noSpaceBetweenColumns = noSpaceBetweenColumns(),
        skipRounding = skipRounding(),
        displayDensity = displayDensity()?.name,
        digitGroupSeparator = digitGroupSeparator()?.name,
        legendShowKey = legend()?.showKey()?.let { if (it) "1" else "0" },
        legendStyle = legend()?.style()?.name,
        legendSetId = legend()?.set()?.uid(),
        legendStrategy = legend()?.strategy()?.name,
        aggregationType = aggregationType()?.name,
    )
}
