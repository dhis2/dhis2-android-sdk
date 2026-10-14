/*
 *  Copyright (c) 2004-2026, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.helpers.AccessHelper.defaultAccess
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithStyleKt
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class Program(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
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
    val programType: ProgramType?,
    internal val programTrackedEntityAttributes: List<ProgramTrackedEntityAttribute>?,
    val relatedProgram: ObjectWithUid?,
    val trackedEntityType: TrackedEntityType?,
    val categoryCombo: ObjectWithUid,
    val access: Access,
    internal val programRuleVariables: List<ProgramRuleVariable>?,
    val expiryDays: Int?,
    val completeEventsExpiryDays: Int?,
    val expiryPeriodType: PeriodType?,
    val minAttributesRequiredToSearch: Int?,
    val maxTeiCountToReturn: Int?,
    internal val programSections: List<ProgramSection>?,
    val featureType: FeatureType?,
    val accessLevel: AccessLevel?,
    val displayEnrollmentLabel: String?,
    val displayEnrollmentsLabel: String?,
    val displayFollowUpLabel: String?,
    val displayOrgUnitLabel: String?,
    val displayRelationshipLabel: String?,
    val displayRelationshipsLabel: String?,
    val displayNoteLabel: String?,
    val displayTrackedEntityAttributeLabel: String?,
    val displayProgramStageLabel: String?,
    val displayProgramStagesLabel: String?,
    val displayEventLabel: String?,
    val displayEventsLabel: String?,
    val attributeValues: List<AttributeValue>?,
    val enrollmentCategoryCombo: ObjectWithUid,
    internal val categoryMappings: List<CategoryMapping>?,
    override val style: ObjectStyle,
) : BaseNameableObject, CoreObject, ObjectWithStyleKt {

    fun version(): Int? = version
    fun onlyEnrollOnce(): Boolean? = onlyEnrollOnce

    @Deprecated("since v41, replaced by displayEnrollmentDateLabel()")
    fun enrollmentDateLabel(): String? = displayEnrollmentDateLabel
    fun displayEnrollmentDateLabel(): String? = displayEnrollmentDateLabel
    fun displayIncidentDate(): Boolean? = displayIncidentDate

    @Deprecated("since v41, replaced by displayIncidentDateLabel()")
    fun incidentDateLabel(): String? = displayIncidentDateLabel
    fun displayIncidentDateLabel(): String? = displayIncidentDateLabel
    fun registration(): Boolean? = registration
    fun selectEnrollmentDatesInFuture(): Boolean? = selectEnrollmentDatesInFuture
    fun dataEntryMethod(): Boolean? = dataEntryMethod
    fun ignoreOverdueEvents(): Boolean? = ignoreOverdueEvents
    fun selectIncidentDatesInFuture(): Boolean? = selectIncidentDatesInFuture
    fun useFirstStageDuringRegistration(): Boolean? = useFirstStageDuringRegistration
    fun displayFrontPageList(): Boolean? = displayFrontPageList
    fun programType(): ProgramType? = programType
    internal fun programTrackedEntityAttributes(): List<ProgramTrackedEntityAttribute>? = programTrackedEntityAttributes
    fun relatedProgram(): ObjectWithUid? = relatedProgram
    fun trackedEntityType(): TrackedEntityType? = trackedEntityType
    fun categoryCombo(): ObjectWithUid = categoryCombo
    fun access(): Access = access
    internal fun programRuleVariables(): List<ProgramRuleVariable>? = programRuleVariables
    fun expiryDays(): Int? = expiryDays
    fun completeEventsExpiryDays(): Int? = completeEventsExpiryDays
    fun expiryPeriodType(): PeriodType? = expiryPeriodType
    fun minAttributesRequiredToSearch(): Int? = minAttributesRequiredToSearch
    fun maxTeiCountToReturn(): Int? = maxTeiCountToReturn
    internal fun programSections(): List<ProgramSection>? = programSections
    fun featureType(): FeatureType? = featureType
    fun accessLevel(): AccessLevel? = accessLevel

    @Deprecated("since v41, replaced by displayEnrollmentLabel()")
    fun enrollmentLabel(): String? = displayEnrollmentLabel
    fun displayEnrollmentLabel(): String? = displayEnrollmentLabel

    fun displayEnrollmentsLabel(): String? = displayEnrollmentsLabel

    fun displayRelationshipsLabel(): String? = displayRelationshipsLabel

    @Deprecated("since v41, replaced by displayFollowUpLabel()")
    fun followUpLabel(): String? = displayFollowUpLabel
    fun displayFollowUpLabel(): String? = displayFollowUpLabel

    @Deprecated("since v41, replaced by displayOrgUnitLabel()")
    fun orgUnitLabel(): String? = displayOrgUnitLabel
    fun displayOrgUnitLabel(): String? = displayOrgUnitLabel

    @Deprecated("since v41, replaced by displayRelationshipLabel()")
    fun relationshipLabel(): String? = displayRelationshipLabel
    fun displayRelationshipLabel(): String? = displayRelationshipLabel

    @Deprecated("since v41, replaced by displayNoteLabel()")
    fun noteLabel(): String? = displayNoteLabel
    fun displayNoteLabel(): String? = displayNoteLabel

    @Deprecated("since v41, replaced by displayTrackedEntityAttributeLabel()")
    fun trackedEntityAttributeLabel(): String? = displayTrackedEntityAttributeLabel
    fun displayTrackedEntityAttributeLabel(): String? = displayTrackedEntityAttributeLabel

    @Deprecated("since v41, replaced by displayProgramStageLabel()")
    fun programStageLabel(): String? = displayProgramStageLabel
    fun displayProgramStageLabel(): String? = displayProgramStageLabel

    fun displayProgramStagesLabel(): String? = displayProgramStagesLabel

    @Deprecated("since v41, replaced by displayEventLabel()")
    fun eventLabel(): String? = displayEventLabel
    fun displayEventLabel(): String? = displayEventLabel

    fun displayEventsLabel(): String? = displayEventsLabel

    fun attributeValues(): List<AttributeValue>? = attributeValues
    fun enrollmentCategoryCombo(): ObjectWithUid = enrollmentCategoryCombo
    internal fun categoryMappings(): List<CategoryMapping>? = categoryMappings

    fun toBuilder(): Builder = ProgramBuilder.from(this)

    @Suppress("TooManyFunctions")
    class Builder : ProgramBuilder() {
        @Deprecated("replaced by displayEnrollmentDateLabel(String)")
        fun enrollmentDateLabel(enrollmentDateLabel: String?): Builder =
            displayEnrollmentDateLabel(enrollmentDateLabel)

        @Deprecated("replaced by displayIncidentDateLabel(String)")
        fun incidentDateLabel(incidentDateLabel: String?): Builder =
            displayIncidentDateLabel(incidentDateLabel)

        @Deprecated("replaced by displayEnrollmentLabel(String)")
        fun enrollmentLabel(enrollmentLabel: String?): Builder =
            displayEnrollmentLabel(enrollmentLabel)

        @Deprecated("replaced by displayFollowUpLabel(String)")
        fun followUpLabel(followUpLabel: String?): Builder =
            displayFollowUpLabel(followUpLabel)

        @Deprecated("replaced by displayOrgUnitLabel(String)")
        fun orgUnitLabel(orgUnitLabel: String?): Builder =
            displayOrgUnitLabel(orgUnitLabel)

        @Deprecated("replaced by displayRelationshipLabel(String)")
        fun relationshipLabel(relationshipLabel: String?): Builder =
            displayRelationshipLabel(relationshipLabel)

        @Deprecated("replaced by displayNoteLabel(String)")
        fun noteLabel(noteLabel: String?): Builder =
            displayNoteLabel(noteLabel)

        @Deprecated("replaced by displayTrackedEntityAttributeLabel(String)")
        fun trackedEntityAttributeLabel(trackedEntityAttributeLabel: String?): Builder =
            displayTrackedEntityAttributeLabel(trackedEntityAttributeLabel)

        @Deprecated("replaced by displayProgramStageLabel(String)")
        fun programStageLabel(programStageLabel: String?): Builder =
            displayProgramStageLabel(programStageLabel)

        @Deprecated("replaced by displayEventLabel(String)")
        fun eventLabel(eventLabel: String?): Builder =
            displayEventLabel(eventLabel)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .access(defaultAccess())
            .accessLevel(AccessLevel.OPEN)
            .style(ObjectStyle())
    }
}
