package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramRuleAction
import org.hisp.dhis.android.core.program.ProgramRuleActionType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.option.OptionDB
import org.hisp.dhis.android.persistence.option.OptionGroupDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "ProgramRuleAction",
    foreignKeys = [
        ForeignKey(
            entity = ProgramRuleDB::class,
            parentColumns = ["uid"],
            childColumns = ["programRule"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
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
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["option"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OptionGroupDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionGroup"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class ProgramRuleActionDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val data: String?,
    val content: String?,
    val location: String?,
    val trackedEntityAttribute: String?,
    val programIndicator: String?,
    val programStageSection: String?,
    val programRuleActionType: String?,
    val programStage: String?,
    val dataElement: String?,
    val programRule: String,
    val option: String?,
    val optionGroup: String?,
    val displayContent: String?,
) : EntityDB<ProgramRuleAction>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramRuleAction {
        return ProgramRuleAction.builder().apply {
            applyBaseIdentifiableFields(this@ProgramRuleActionDB)
            data(data)
            content(content)
            location(location)
            trackedEntityAttribute?.let { trackedEntityAttribute(ObjectWithUid.create(it)) }
            programIndicator?.let { programIndicator(ObjectWithUid.create(it)) }
            programStageSection?.let { programStageSection(ObjectWithUid.create(it)) }
            programRuleActionType(programRuleActionType?.let { ProgramRuleActionType.valueOf(it) })
            programStage?.let { programStage(ObjectWithUid.create(it)) }
            dataElement?.let { dataElement(ObjectWithUid.create(it)) }
            programRule(ObjectWithUid.create(programRule))
            option?.let { option(ObjectWithUid.create(it)) }
            optionGroup?.let { optionGroup(ObjectWithUid.create(it)) }
            displayContent(displayContent)
        }.build()
    }
}

internal fun ProgramRuleAction.toDB(): ProgramRuleActionDB {
    return ProgramRuleActionDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        data = data(),
        content = content(),
        location = location(),
        trackedEntityAttribute = trackedEntityAttribute()?.uid(),
        programIndicator = programIndicator()?.uid(),
        programStageSection = programStageSection()?.uid(),
        programRuleActionType = programRuleActionType()?.name,
        programStage = programStage()?.uid(),
        dataElement = dataElement()?.uid(),
        programRule = programRule()!!.uid(),
        option = option()?.uid(),
        optionGroup = optionGroup()?.uid(),
        displayContent = displayContent(),
    )
}
