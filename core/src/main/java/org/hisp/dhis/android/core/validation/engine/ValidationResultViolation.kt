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

package org.hisp.dhis.android.core.validation.engine

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.validation.ValidationRule

@ModelBuilder
data class ValidationResultViolation(
    val validationRule: ValidationRule,
    val period: String,
    val organisationUnitUid: String,
    val attributeOptionComboUid: String?,
    val leftSideEvaluation: ValidationResultSideEvaluation,
    val rightSideEvaluation: ValidationResultSideEvaluation,
) {
    fun validationRule(): ValidationRule = validationRule
    fun period(): String = period
    fun organisationUnitUid(): String = organisationUnitUid
    fun attributeOptionComboUid(): String? = attributeOptionComboUid
    fun leftSideEvaluation(): ValidationResultSideEvaluation = leftSideEvaluation
    fun rightSideEvaluation(): ValidationResultSideEvaluation = rightSideEvaluation

    fun dataElementUids(): Set<DataElementOperand> =
        leftSideEvaluation.dataElementUids + rightSideEvaluation.dataElementUids

    fun toBuilder(): Builder = ValidationResultViolationBuilder.from(this)

    class Builder : ValidationResultViolationBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
