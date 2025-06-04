package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.itemfilter.toDB

@Entity(
    tableName = "ProgramStage",
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
internal data class ProgramStageDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val displayExecutionDateLabel: String?,
    val allowGenerateNextVisit: Boolean?,
    val validCompleteOnly: Boolean?,
    val reportDateToUse: String?,
    val openAfterEnrollment: Boolean?,
    val repeatable: Boolean?,
    val formType: String?,
    val displayGenerateEventBox: Boolean?,
    val generatedByEnrollmentDate: Boolean?,
    val autoGenerateEvent: Boolean?,
    val sortOrder: Int?,
    val hideDueDate: Boolean?,
    val blockEntryForm: Boolean?,
    val minDaysFromStart: Int?,
    val standardInterval: Int?,
    val program: String,
    val periodType: String?,
    val accessDataWrite: AccessDB?,
    val remindCompleted: Boolean?,
    val description: String?,
    val displayDescription: String?,
    val featureType: String?,
    override val color: String?,
    override val icon: String?,
    val enableUserAssignment: Boolean?,
    val displayDueDateLabel: String?,
    val validationStrategy: String?,
    val displayProgramStageLabel: String?,
    val displayEventLabel: String?,
) : EntityDB<ProgramStage>, BaseIdentifiableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): ProgramStage {
        return ProgramStage.builder().apply {
            applyBaseIdentifiableFields(this@ProgramStageDB)
            applyStyleFields(this@ProgramStageDB)
            id(id?.toLong())
            description(description)
            displayDescription(displayDescription)
            displayExecutionDateLabel(displayExecutionDateLabel)
            displayDueDateLabel(displayDueDateLabel)
            allowGenerateNextVisit(allowGenerateNextVisit)
            validCompleteOnly(validCompleteOnly)
            reportDateToUse(reportDateToUse)
            openAfterEnrollment(openAfterEnrollment)
            repeatable(repeatable)
            formType?.let { formType(FormType.valueOf(it)) }
            displayGenerateEventBox(displayGenerateEventBox)
            generatedByEnrollmentDate(generatedByEnrollmentDate)
            autoGenerateEvent(autoGenerateEvent)
            sortOrder(sortOrder)
            hideDueDate(hideDueDate)
            blockEntryForm(blockEntryForm)
            minDaysFromStart(minDaysFromStart)
            standardInterval(standardInterval)
            enableUserAssignment(enableUserAssignment)
            periodType?.let { periodType(PeriodType.valueOf(it)) }
            program(ObjectWithUid.create(program))
            accessDataWrite?.let { access(it.toDomain()) }
            remindCompleted(remindCompleted)
            featureType?.let { featureType(FeatureType.valueOf(it)) }
            validationStrategy?.let { validationStrategy(ValidationStrategy.valueOf(it)) }
            displayProgramStageLabel(displayProgramStageLabel)
            displayEventLabel(displayEventLabel)
        }.build()
    }
}

internal fun ProgramStage.toDB(): ProgramStageDB {
    return ProgramStageDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        displayExecutionDateLabel = displayExecutionDateLabel(),
        allowGenerateNextVisit = allowGenerateNextVisit(),
        validCompleteOnly = validCompleteOnly(),
        reportDateToUse = reportDateToUse(),
        openAfterEnrollment = openAfterEnrollment(),
        repeatable = repeatable(),
        formType = formType()?.name,
        displayGenerateEventBox = displayGenerateEventBox(),
        generatedByEnrollmentDate = generatedByEnrollmentDate(),
        autoGenerateEvent = autoGenerateEvent(),
        sortOrder = sortOrder(),
        hideDueDate = hideDueDate(),
        blockEntryForm = blockEntryForm(),
        minDaysFromStart = minDaysFromStart(),
        standardInterval = standardInterval(),
        program = program()!!.uid(),
        periodType = periodType()?.name,
        accessDataWrite = access().toDB(),
        remindCompleted = remindCompleted(),
        description = description(),
        displayDescription = displayDescription(),
        featureType = featureType()?.name,
        color = style()?.color(),
        icon = style()?.icon(),
        enableUserAssignment = enableUserAssignment(),
        displayDueDateLabel = displayDueDateLabel(),
        validationStrategy = validationStrategy()?.name,
        displayProgramStageLabel = displayProgramStageLabel(),
        displayEventLabel = displayEventLabel(),
    )
}
