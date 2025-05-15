package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields

@Entity(
    tableName = "ProgramIndicator",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
    ],
)
internal data class ProgramIndicatorDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val displayInForm: Boolean?,
    val expression: String?,
    val dimensionItem: String?,
    val filter: String?,
    val decimals: Int?,
    val program: String,
    val aggregationType: String?,
    val analyticsType: String?,
) : EntityDB<ProgramIndicator>, BaseNameableObjectDB {

    override fun toDomain(): ProgramIndicator {
        return ProgramIndicator.builder().apply {
            applyBaseNameableFields(this@ProgramIndicatorDB)
            id(id?.toLong())
            displayInForm(displayInForm)
            expression(expression)
            dimensionItem(dimensionItem)
            filter(filter)
            decimals(decimals)
            program(ObjectWithUid.create(program))
            aggregationType?.let { aggregationType(AggregationType.valueOf(it)) }
            analyticsType?.let { analyticsType(AnalyticsType.valueOf(it)) }
        }.build()
    }
}

internal fun ProgramIndicator.toDB(): ProgramIndicatorDB {
    return ProgramIndicatorDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        displayInForm = displayInForm(),
        expression = expression(),
        dimensionItem = dimensionItem(),
        filter = filter(),
        decimals = decimals(),
        program = program()!!.uid(),
        aggregationType = aggregationType()?.name,
        analyticsType = analyticsType()?.name,
    )
}
