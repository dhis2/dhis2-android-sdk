/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.program

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramInternalAccessor
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.network.attribute.AttributeValueDTO
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.AccessDTO
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithStyleDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields
import org.hisp.dhis.android.network.trackedentitytype.TrackedEntityTypeDTO

@Serializable
internal data class ProgramDTO(
    override val id: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val style: ObjectWithStyleDTO?,
    val version: Int?,
    val onlyEnrollOnce: Boolean?,
    val enrollmentDateLabel: String?,
    val displayEnrollmentDateLabel: String?,
    val displayIncidentDate: Boolean?,
    val incidentDateLabel: String?,
    val displayIncidentDateLabel: String?,
    val registration: Boolean?,
    val selectEnrollmentDatesInFuture: Boolean?,
    val dataEntryMethod: Boolean?,
    val ignoreOverdueEvents: Boolean?,
    val selectIncidentDatesInFuture: Boolean?,
    val useFirstStageDuringRegistration: Boolean?,
    val displayFrontPageList: Boolean?,
    val programType: String?,
    val programTrackedEntityAttributes: List<ProgramTrackedEntityAttributeDTO>?,
    val relatedProgram: ObjectWithUidDTO?,
    val trackedEntityType: TrackedEntityTypeDTO?,
    val categoryCombo: ObjectWithUidDTO?,
    val access: AccessDTO?,
    val programRuleVariables: List<ProgramRuleVariableDTO>?,
    val expiryDays: Int?,
    val completeEventsExpiryDays: Int?,
    val expiryPeriodType: String?,
    val minAttributesRequiredToSearch: Int?,
    val maxTeiCountToReturn: Int?,
    val programSections: List<ProgramSectionDTO>?,
    val featureType: String?,
    val accessLevel: String?,
    val enrollmentLabel: String?,
    val displayEnrollmentLabel: String?,
    val followUpLabel: String?,
    val displayFollowUpLabel: String?,
    val orgUnitLabel: String?,
    val displayOrgUnitLabel: String?,
    val relationshipLabel: String?,
    val displayRelationshipLabel: String?,
    val noteLabel: String?,
    val displayNoteLabel: String?,
    val trackedEntityAttributeLabel: String?,
    val displayTrackedEntityAttributeLabel: String?,
    val programStageLabel: String?,
    val displayProgramStageLabel: String?,
    val eventLabel: String?,
    val displayEventLabel: String?,
    val attributeValues: List<AttributeValueDTO>?,
) : BaseNameableObjectDTO {
    @Suppress("ComplexMethod")
    fun toDomain(): Program {
        return Program.builder().apply {
            applyBaseNameableFields(this@ProgramDTO)
            style?.let { style(it.toDomain()) }
            version(version)
            onlyEnrollOnce(onlyEnrollOnce)
            displayEnrollmentDateLabel(displayEnrollmentDateLabel ?: enrollmentDateLabel)
            displayIncidentDate(displayIncidentDate)
            displayIncidentDateLabel(displayIncidentDateLabel ?: incidentDateLabel)
            registration(registration)
            selectEnrollmentDatesInFuture(selectEnrollmentDatesInFuture)
            dataEntryMethod(dataEntryMethod)
            ignoreOverdueEvents(ignoreOverdueEvents)
            selectIncidentDatesInFuture(selectIncidentDatesInFuture)
            useFirstStageDuringRegistration(useFirstStageDuringRegistration)
            displayFrontPageList(displayFrontPageList)
            programType(programType?.let { ProgramType.valueOf(programType) })
            ProgramInternalAccessor.insertProgramTrackedEntityAttributes(
                this,
                programTrackedEntityAttributes?.map { it.toDomain() },
            )
            relatedProgram(relatedProgram?.toDomain())
            trackedEntityType(trackedEntityType?.toDomain())
            categoryCombo(categoryCombo?.toDomain())
            access?.let { access(it.toDomain()) }
            ProgramInternalAccessor.insertProgramRuleVariables(this, programRuleVariables?.map { it.toDomain() })
            expiryDays(expiryDays)
            completeEventsExpiryDays(completeEventsExpiryDays)
            expiryPeriodType(expiryPeriodType?.let { PeriodType.valueOf(expiryPeriodType) })
            minAttributesRequiredToSearch(minAttributesRequiredToSearch)
            maxTeiCountToReturn(maxTeiCountToReturn)
            ProgramInternalAccessor.insertProgramSections(this, programSections?.map { it.toDomain() })
            featureType(featureType?.let { FeatureType.valueOf(featureType) })
            accessLevel(accessLevel?.let { AccessLevel.valueOf(accessLevel) })
            displayEnrollmentLabel(displayEnrollmentLabel ?: enrollmentLabel)
            displayFollowUpLabel(displayFollowUpLabel ?: followUpLabel)
            displayOrgUnitLabel(displayOrgUnitLabel ?: orgUnitLabel)
            displayRelationshipLabel(displayRelationshipLabel ?: relationshipLabel)
            displayNoteLabel(displayNoteLabel ?: noteLabel)
            displayTrackedEntityAttributeLabel(displayTrackedEntityAttributeLabel ?: trackedEntityAttributeLabel)
            displayProgramStageLabel(displayProgramStageLabel ?: programStageLabel)
            displayEventLabel(displayEventLabel ?: eventLabel)
            attributeValues?.let { attributeValues(it.map { it.toDomain() }) }
        }.build()
    }
}

@Serializable
internal class ProgramPayload(
    override val pager: PagerDTO? = null,
    @SerialName("programs") override val items: List<ProgramDTO> = emptyList(),
) : PayloadJson<ProgramDTO>(pager, items)
