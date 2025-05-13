package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.dataset.SectionDisplayOptions
import org.hisp.dhis.android.core.dataset.SectionPivotMode
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "Section",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["dataSet"]),
    ],
)
internal data class SectionDB(
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
    val sortOrder: Int?,
    val dataSet: String,
    val showRowTotals: Boolean?,
    val showColumnTotals: Boolean?,
    val disableDataElementAutoGroup: Boolean?,
    val pivotMode: String?,
    val pivotedCategory: String?,
    val afterSectionText: String?,
    val beforeSectionText: String?,
) : EntityDB<Section>, BaseIdentifiableObjectDB {

    override fun toDomain(): Section {
        return Section.builder().apply {
            applyBaseIdentifiableFields(this@SectionDB)
            id(id?.toLong())
            description(description)
            sortOrder(sortOrder)
            dataSet(ObjectWithUid.create(dataSet))
            showRowTotals(showRowTotals)
            showColumnTotals(showColumnTotals)
            disableDataElementAutoGroup(disableDataElementAutoGroup)
            displayOptions(
                SectionDisplayOptions.builder().apply {
                    beforeSectionText(beforeSectionText)
                    afterSectionText(afterSectionText)
                    pivotMode?.let { pivotMode(SectionPivotMode.valueOf(it)) }
                    pivotedCategory(pivotedCategory)
                }.build(),
            )
        }.build()
    }
}

internal fun Section.toDB(): SectionDB {
    return SectionDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        description = description(),
        sortOrder = sortOrder(),
        dataSet = dataSet()!!.uid(),
        showRowTotals = showRowTotals(),
        showColumnTotals = showColumnTotals(),
        disableDataElementAutoGroup = disableDataElementAutoGroup(),
        pivotMode = displayOptions()?.pivotMode()?.name,
        pivotedCategory = displayOptions()?.pivotedCategory(),
        afterSectionText = displayOptions()?.afterSectionText(),
        beforeSectionText = displayOptions()?.beforeSectionText(),
    )
}
