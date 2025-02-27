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

package org.hisp.dhis.android.network.validationrule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

@Serializable
internal data class ValidationRuleDTO(
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
    val instruction: String?,
    val importance: String,
    val operator: String,
    val periodType: String,
    val skipFormValidation: Boolean,
    val leftSide: ValidationRuleExpressionDTO,
    val rightSide: ValidationRuleExpressionDTO,
    val organisationUnitLevels: List<Int>,
) : BaseNameableObjectDTO {
    fun toDomain(): ValidationRule? {
        return if (leftSide.expression != null && rightSide.expression != null) {
            ValidationRule.builder()
                .applyBaseNameableFields(this)
                .instruction(instruction)
                .importance(ValidationRuleImportance.valueOf(importance))
                .operator(ValidationRuleOperator.valueOf(operator))
                .periodType(PeriodType.valueOf(periodType))
                .skipFormValidation(skipFormValidation)
                .leftSide(leftSide.toDomain())
                .rightSide(rightSide.toDomain())
                .organisationUnitLevels(organisationUnitLevels)
                .build()
        } else {
            null
        }
    }
}

@Serializable
internal class ValidationRulePayload(
    override val pager: PagerDTO?,
    @SerialName("validationRules") override val items: List<ValidationRuleDTO> = emptyList(),
) : PayloadJson<ValidationRuleDTO>(pager, items) {
    fun mapNotNullItems(
        transform: (ValidationRuleDTO) -> ValidationRule?,
    ): PayloadJson<ValidationRule> {
        return PayloadJson(pager, items.mapNotNull(transform))
    }
}

@Serializable
internal class ValidationRuleDatasetPayload(
    override val pager: PagerDTO?,
    @SerialName("validationRules") override val items: List<ObjectWithUidDTO> = emptyList(),
) : PayloadJson<ObjectWithUidDTO>(pager, items)
