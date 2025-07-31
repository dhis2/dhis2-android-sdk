package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeDB

@Entity(
    tableName = "Program",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class ProgramDB(
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
    val version: Int?,
    val onlyEnrollOnce: Boolean?,
    val displayEnrollmentDateLabel: String?,
    val displayIncidentDate: Boolean?,
    val displayIncidentDateLabel: String?,
    val registration: Boolean?,
    val selectEnrollmentDatesInFuture: Boolean?,
    val dataEntryMethod: Boolean?,
    val ignoreOverdueEvents: Boolean?,
    val selectIncidentDatesInFuture: Boolean?,
    val useFirstStageDuringRegistration: Boolean?,
    val displayFrontPageList: Boolean?,
    val programType: String?,
    val relatedProgram: String?,
    val trackedEntityType: String?,
    val categoryCombo: String?,
    val accessDataWrite: AccessDB?,
    val expiryDays: Int?,
    val completeEventsExpiryDays: Int?,
    val expiryPeriodType: String?,
    val minAttributesRequiredToSearch: Int?,
    val maxTeiCountToReturn: Int?,
    val featureType: String?,
    val accessLevel: String?,
    val color: String?,
    val icon: String?,
    val displayEnrollmentLabel: String?,
    val displayFollowUpLabel: String?,
    val displayOrgUnitLabel: String?,
    val displayRelationshipLabel: String?,
    val displayNoteLabel: String?,
    val displayTrackedEntityAttributeLabel: String?,
    val displayProgramStageLabel: String?,
    val displayEventLabel: String?,
) : EntityDB<Program>, BaseNameableObjectDB {

    override fun toDomain(): Program {
        return Program.builder().apply {
            applyBaseNameableFields(this@ProgramDB)
            version(version)
            onlyEnrollOnce(onlyEnrollOnce)
            displayEnrollmentDateLabel(displayEnrollmentDateLabel)
            displayIncidentDate(displayIncidentDate)
            displayIncidentDateLabel(displayIncidentDateLabel)
            registration(registration)
            selectEnrollmentDatesInFuture(selectEnrollmentDatesInFuture)
            dataEntryMethod(dataEntryMethod)
            ignoreOverdueEvents(ignoreOverdueEvents)
            selectIncidentDatesInFuture(selectIncidentDatesInFuture)
            useFirstStageDuringRegistration(useFirstStageDuringRegistration)
            displayFrontPageList(displayFrontPageList)
            programType?.let { programType(ProgramType.valueOf(it)) }
            relatedProgram?.let { relatedProgram(ObjectWithUid.create(it)) }
            relatedProgram?.let { relatedProgram(ObjectWithUidDB(it).toDomain()) }
            trackedEntityType?.let { trackedEntityType(TrackedEntityType.builder().uid(it).build()) }
            categoryCombo?.let { categoryCombo(ObjectWithUid.create(it)) }
            accessDataWrite?.let { access(it.toDomain()) }
            expiryDays(expiryDays)
            completeEventsExpiryDays(completeEventsExpiryDays)
            expiryPeriodType?.let { expiryPeriodType(PeriodType.valueOf(it)) }
            minAttributesRequiredToSearch(minAttributesRequiredToSearch)
            maxTeiCountToReturn(maxTeiCountToReturn)
            featureType?.let { featureType(FeatureType.valueOf(it)) }
            accessLevel?.let { accessLevel(AccessLevel.valueOf(it)) }
            style(ObjectStyle.builder().color(color).icon(icon).build())
            displayEnrollmentLabel(displayEnrollmentLabel)
            displayFollowUpLabel(displayFollowUpLabel)
            displayOrgUnitLabel(displayOrgUnitLabel)
            displayRelationshipLabel(displayRelationshipLabel)
            displayNoteLabel(displayNoteLabel)
            displayTrackedEntityAttributeLabel(displayTrackedEntityAttributeLabel)
            displayProgramStageLabel(displayProgramStageLabel)
            displayEventLabel(displayEventLabel)
        }.build()
    }
}

internal fun Program.toDB(): ProgramDB {
    return ProgramDB(
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
        version = version(),
        onlyEnrollOnce = onlyEnrollOnce(),
        displayEnrollmentDateLabel = displayEnrollmentDateLabel(),
        displayIncidentDate = displayIncidentDate(),
        displayIncidentDateLabel = displayIncidentDateLabel(),
        registration = registration(),
        selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture(),
        dataEntryMethod = dataEntryMethod(),
        ignoreOverdueEvents = ignoreOverdueEvents(),
        selectIncidentDatesInFuture = selectIncidentDatesInFuture(),
        useFirstStageDuringRegistration = useFirstStageDuringRegistration(),
        displayFrontPageList = displayFrontPageList(),
        programType = programType()?.name,
        relatedProgram = relatedProgram()?.uid(),
        trackedEntityType = trackedEntityType()?.uid(),
        categoryCombo = categoryCombo()?.uid() ?: CategoryComboDB.Companion.DEFAULT_UID,
        accessDataWrite = access().toDB(),
        expiryDays = expiryDays(),
        completeEventsExpiryDays = completeEventsExpiryDays(),
        expiryPeriodType = expiryPeriodType()?.name,
        minAttributesRequiredToSearch = minAttributesRequiredToSearch(),
        maxTeiCountToReturn = maxTeiCountToReturn(),
        featureType = featureType()?.name,
        accessLevel = accessLevel()?.name,
        color = style()?.color(),
        icon = style()?.icon(),
        displayEnrollmentLabel = displayEnrollmentLabel(),
        displayFollowUpLabel = displayFollowUpLabel(),
        displayOrgUnitLabel = displayOrgUnitLabel(),
        displayRelationshipLabel = displayRelationshipLabel(),
        displayNoteLabel = displayNoteLabel(),
        displayTrackedEntityAttributeLabel = displayTrackedEntityAttributeLabel(),
        displayProgramStageLabel = displayProgramStageLabel(),
        displayEventLabel = displayEventLabel(),
        deleted = deleted(),
    )
}
