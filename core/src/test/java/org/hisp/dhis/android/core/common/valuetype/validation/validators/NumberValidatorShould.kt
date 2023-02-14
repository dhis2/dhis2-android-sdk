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

package org.hisp.dhis.android.core.common.valuetype.validation.validators

import org.hisp.dhis.android.core.common.valuetype.validation.failures.NumberFailure
import org.junit.Test

class NumberValidatorShould : ValidatorShouldHelper<NumberFailure>(NumberValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("0")
        valueShouldSuccess("4")
        valueShouldSuccess("+4")
        valueShouldSuccess("254.3")
        valueShouldSuccess("98.000005")
        valueShouldSuccess("-6.299")
    }

    @Test
    fun `Should fail when passing scientific notation values`() {
        valueShouldFail("3e-01", NumberFailure.ScientificNotationException)
        valueShouldFail("3e-1", NumberFailure.ScientificNotationException)
        valueShouldFail("12e10", NumberFailure.ScientificNotationException)
        valueShouldFail("3.2e23", NumberFailure.ScientificNotationException)
        valueShouldFail("37.e88", NumberFailure.ScientificNotationException)
    }

    @Test
    fun `Should fail when passing leading zeros`() {
        valueShouldFail("0000005.20", NumberFailure.LeadingZeroException)
        valueShouldFail("+0003", NumberFailure.LeadingZeroException)
    }

    @Test
    fun `Should fail when passing malformed values`() {
        valueShouldFail("", NumberFailure.NumberFormatException)
        valueShouldFail("sdf", NumberFailure.NumberFormatException)
        valueShouldFail("254,3", NumberFailure.NumberFormatException)
        valueShouldFail("25.035,21", NumberFailure.NumberFormatException)
        valueShouldFail("25,035.21", NumberFailure.NumberFormatException)
        valueShouldFail("10:45", NumberFailure.NumberFormatException)
        valueShouldFail("10a", NumberFailure.NumberFormatException)
        valueShouldFail("10USD", NumberFailure.NumberFormatException)
        valueShouldFail("A1.23", NumberFailure.NumberFormatException)
        valueShouldFail("π", NumberFailure.NumberFormatException)
        valueShouldFail("ln(2)", NumberFailure.NumberFormatException)
        valueShouldFail("2/3", NumberFailure.NumberFormatException)
        valueShouldFail(".5", NumberFailure.NumberFormatException)
    }
}
