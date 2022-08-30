/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.arch.helpers

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.valuetype.validation.failures.IntegerPositiveFailure
import org.hisp.dhis.android.core.common.valuetype.validation.validators.IntegerPositiveValidator
import org.junit.Assert.fail
import org.junit.Test

class ResultShould {

    private val validator = IntegerPositiveValidator

    @Test
    fun `Should return the same value when succeeding using when`() {
        val value = "5"
        when (val result = validator.validate(value)) {
            is Result.Success -> Truth.assertThat(result.value).isEqualTo(value)
            is Result.Failure -> fail()
        }
    }

    @Test
    fun `Should return a failure when the validator fails`() {
        val value = "-5"
        when (val result = validator.validate(value)) {
            is Result.Success -> fail()
            is Result.Failure -> Truth.assertThat(result.failure).isEqualTo(IntegerPositiveFailure.ValueIsNegative)
        }
    }

    @Test
    fun `Should return the same value when succeeding using fold`() {
        val value = "5"
        validator.validate(value).fold(
            onSuccess = {
                Truth.assertThat(it).isEqualTo(value)
            },
            onFailure = {
                fail()
            }
        )
    }

    @Test
    fun `Should succeed when entering the right value`() {
        Truth.assertThat(validator.validate("5").succeeded).isTrue()
    }
}
