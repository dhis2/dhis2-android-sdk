package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.option.OptionSetDB

@Entity(
    tableName = "TrackedEntityAttribute",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class TrackedEntityAttributeDB(
    @PrimaryKey
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
    override val deleted: Boolean?,
    val pattern: String?,
    val sortOrderInListNoProgram: Int?,
    val optionSet: String?,
    val valueType: String?,
    val expression: String?,
    val programScope: Boolean?,
    val displayInListNoProgram: Boolean?,
    val generated: Boolean?,
    val displayOnVisitSchedule: Boolean?,
    val orgunitScope: Boolean?,
    val uniqueProperty: Boolean?,
    val inherit: Boolean?,
    val formName: String?,
    val fieldMask: String?,
    override val color: String?,
    override val icon: String?,
    val displayFormName: String?,
    val aggregationType: String?,
    val confidential: Boolean?,
) : EntityDB<TrackedEntityAttribute>, BaseNameableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): TrackedEntityAttribute {
        return TrackedEntityAttribute.builder().apply {
            applyBaseNameableFields(this@TrackedEntityAttributeDB)
            applyStyleFields(this@TrackedEntityAttributeDB)
            pattern(pattern)
            sortOrderInListNoProgram(sortOrderInListNoProgram)
            optionSet?.let { optionSet(ObjectWithUidDB(it).toDomain()) }
            valueType(valueType?.let { ValueType.valueOf(it) })
            expression(expression)
            programScope(programScope)
            displayInListNoProgram(displayInListNoProgram)
            generated(generated)
            displayOnVisitSchedule(displayOnVisitSchedule)
            orgUnitScope(orgunitScope)
            unique(uniqueProperty)
            inherit(inherit)
            formName(formName)
            fieldMask(fieldMask)
            displayFormName(displayFormName)
            aggregationType(aggregationType?.let { AggregationType.valueOf(it) })
            confidential(confidential)
        }.build()
    }
}

internal fun TrackedEntityAttribute.toDB(): TrackedEntityAttributeDB {
    return TrackedEntityAttributeDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        pattern = pattern(),
        sortOrderInListNoProgram = sortOrderInListNoProgram(),
        optionSet = optionSet()?.uid(),
        valueType = valueType()?.name,
        expression = expression(),
        programScope = programScope(),
        displayInListNoProgram = programScope(),
        generated = programScope(),
        displayOnVisitSchedule = programScope(),
        orgunitScope = programScope(),
        uniqueProperty = programScope(),
        inherit = programScope(),
        formName = formName(),
        fieldMask = fieldMask(),
        color = style()?.color(),
        icon = style()?.icon(),
        displayFormName = displayFormName(),
        aggregationType = aggregationType()?.name,
        confidential = programScope(),
        deleted = deleted(),
    )
}
