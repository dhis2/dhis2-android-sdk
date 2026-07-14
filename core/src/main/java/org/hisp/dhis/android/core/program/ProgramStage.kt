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
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithStyleKt
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.period.PeriodType
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class ProgramStage(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    val description: String?,
    val displayDescription: String?,
    val displayExecutionDateLabel: String?,
    val displayDueDateLabel: String?,
    val allowGenerateNextVisit: Boolean?,
    val validCompleteOnly: Boolean?,
    val reportDateToUse: String?,
    val openAfterEnrollment: Boolean?,
    val repeatable: Boolean?,
    val featureType: FeatureType?,
    val formType: FormType?,
    val displayGenerateEventBox: Boolean?,
    val generatedByEnrollmentDate: Boolean?,
    val autoGenerateEvent: Boolean?,
    val sortOrder: Int?,
    val hideDueDate: Boolean?,
    val blockEntryForm: Boolean?,
    val minDaysFromStart: Int?,
    val standardInterval: Int?,
    val enableUserAssignment: Boolean?,
    internal val programStageSections: List<ProgramStageSection>?,
    internal val programStageDataElements: List<ProgramStageDataElement>?,
    val periodType: PeriodType?,
    val program: ObjectWithUid?,
    val access: Access,
    val remindCompleted: Boolean?,
    val validationStrategy: ValidationStrategy?,
    val displayProgramStageLabel: String?,
    val displayEventLabel: String?,
    val displayEventsLabel: String?,
    val attributeValues: List<AttributeValue>?,
    override val style: ObjectStyle,
) : BaseIdentifiableObject, CoreObject, ObjectWithStyleKt {

    fun description(): String? = description
    fun displayDescription(): String? = displayDescription

    @Deprecated("since v41, replaced by displayExecutionDateLabel()")
    fun executionDateLabel(): String? = displayExecutionDateLabel
    fun displayExecutionDateLabel(): String? = displayExecutionDateLabel

    @Deprecated("since v41, replaced by displayDueDateLabel()")
    fun dueDateLabel(): String? = displayDueDateLabel
    fun displayDueDateLabel(): String? = displayDueDateLabel

    fun allowGenerateNextVisit(): Boolean? = allowGenerateNextVisit
    fun validCompleteOnly(): Boolean? = validCompleteOnly
    fun reportDateToUse(): String? = reportDateToUse
    fun openAfterEnrollment(): Boolean? = openAfterEnrollment
    fun repeatable(): Boolean? = repeatable
    fun featureType(): FeatureType? = featureType
    fun formType(): FormType? = formType
    fun displayGenerateEventBox(): Boolean? = displayGenerateEventBox
    fun generatedByEnrollmentDate(): Boolean? = generatedByEnrollmentDate
    fun autoGenerateEvent(): Boolean? = autoGenerateEvent
    fun sortOrder(): Int? = sortOrder
    fun hideDueDate(): Boolean? = hideDueDate
    fun blockEntryForm(): Boolean? = blockEntryForm
    fun minDaysFromStart(): Int? = minDaysFromStart
    fun standardInterval(): Int? = standardInterval
    fun enableUserAssignment(): Boolean? = enableUserAssignment
    internal fun programStageSections(): List<ProgramStageSection>? = programStageSections
    internal fun programStageDataElements(): List<ProgramStageDataElement>? = programStageDataElements
    fun periodType(): PeriodType? = periodType
    fun program(): ObjectWithUid? = program
    fun access(): Access = access
    fun remindCompleted(): Boolean? = remindCompleted
    fun validationStrategy(): ValidationStrategy? = validationStrategy

    @Deprecated("since v41, replaced by displayProgramStageLabel()")
    fun programStageLabel(): String? = displayProgramStageLabel
    fun displayProgramStageLabel(): String? = displayProgramStageLabel

    @Deprecated("since v41, replaced by displayEventLabel()")
    fun eventLabel(): String? = displayEventLabel
    fun displayEventLabel(): String? = displayEventLabel

    fun displayEventsLabel(): String? = displayEventsLabel
    fun attributeValues(): List<AttributeValue>? = attributeValues

    fun toBuilder(): Builder = ProgramStageBuilder.from(this)

    class Builder : ProgramStageBuilder() {
        @Deprecated("replaced by displayExecutionDateLabel(String)")
        fun executionDateLabel(executionDateLabel: String?): Builder =
            displayExecutionDateLabel(executionDateLabel)

        @Deprecated("replaced by displayDueDateLabel(String)")
        fun dueDateLabel(dueDateLabel: String?): Builder =
            displayDueDateLabel(dueDateLabel)

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
            .style(ObjectStyle())
            .enableUserAssignment(false)
    }
}
