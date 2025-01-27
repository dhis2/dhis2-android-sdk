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

package org.hisp.dhis.android.network.programstage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor
import org.hisp.dhis.android.network.attribute.AttributeValueDTO
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.AccessDTO
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithStyleDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

@Serializable
internal data class ProgramStageDTO(
    @SerialName("id") override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val style: ObjectWithStyleDTO?,
    val description: String?,
    val displayDescription: String?,
    val executionDateLabel: String?,
    val displayExecutionDateLabel: String?,
    val dueDateLabel: String?,
    val displayDueDateLabel: String?,
    val allowGenerateNextVisit: Boolean?,
    val validCompleteOnly: Boolean?,
    val reportDateToUse: String?,
    val openAfterEnrollment: Boolean?,
    val repeatable: Boolean?,
    val featureType: String?,
    val formType: String?,
    val displayGenerateEventBox: Boolean?,
    val generatedByEnrollmentDate: Boolean?,
    val autoGenerateEvent: Boolean?,
    val sortOrder: Int?,
    val hideDueDate: Boolean?,
    val blockEntryForm: Boolean?,
    val minDaysFromStart: Int?,
    val standardInterval: Int?,
    val enableUserAssignment: Boolean?,
    val programStageSections: List<ProgramStageSectionDTO>? = emptyList(),
    val programStageDataElements: List<ProgramStageDataElementDTO>? = emptyList(),
    val periodType: String?,
    val program: ObjectWithUidDTO?,
    val access: AccessDTO?,
    val remindCompleted: Boolean?,
    val validationStrategy: String?,
    val programStageLabel: String?,
    val displayProgramStageLabel: String?,
    val eventLabel: String?,
    val displayEventLabel: String?,
    val attributeValues: List<AttributeValueDTO>? = emptyList(),
) : BaseIdentifiableObjectDTO {
    fun toDomain(): ProgramStage {
        return ProgramStage.builder().apply {
            applyBaseIdentifiableFields(this@ProgramStageDTO)
            style?.let { style(style.toDomain()) }
            description(description)
            displayDescription(displayDescription)
            displayExecutionDateLabel(displayExecutionDateLabel ?: executionDateLabel)
            displayDueDateLabel(displayDueDateLabel ?: dueDateLabel)
            allowGenerateNextVisit(allowGenerateNextVisit)
            validCompleteOnly(validCompleteOnly)
            reportDateToUse(reportDateToUse)
            openAfterEnrollment(openAfterEnrollment)
            repeatable(repeatable)
            featureType(featureType?.let { FeatureType.valueOf(it) })
            formType(formType?.let { FormType.valueOf(it) })
            displayGenerateEventBox(displayGenerateEventBox)
            generatedByEnrollmentDate(generatedByEnrollmentDate)
            autoGenerateEvent(autoGenerateEvent)
            sortOrder(sortOrder)
            hideDueDate(hideDueDate)
            blockEntryForm(blockEntryForm)
            minDaysFromStart(minDaysFromStart)
            standardInterval(standardInterval)
            enableUserAssignment(enableUserAssignment)
            ProgramStageInternalAccessor.insertProgramStageSections(
                this,
                programStageSections?.map { it.toDomain() },
            )
            ProgramStageInternalAccessor.insertProgramStageDataElements(
                this,
                programStageDataElements?.map { it.toDomain() },
            )
            periodType(periodType?.let { PeriodType.valueOf(it) })
            program(program?.toDomain())
            access?.let { access(access.toDomain()) }
            remindCompleted(remindCompleted)
            validationStrategy(validationStrategy?.let { ValidationStrategy.valueOf(it) })
            displayProgramStageLabel(displayProgramStageLabel ?: programStageLabel)
            displayEventLabel(displayEventLabel ?: eventLabel)
            attributeValues(attributeValues?.map { it.toDomain() })
        }
            .build()
    }
}

@Serializable
internal class ProgramStagePayload(
    override val pager: PagerDTO?,
    @SerialName("programStages") override val items: List<ProgramStageDTO> = emptyList(),
) : PayloadJson<ProgramStageDTO>(pager, items)
