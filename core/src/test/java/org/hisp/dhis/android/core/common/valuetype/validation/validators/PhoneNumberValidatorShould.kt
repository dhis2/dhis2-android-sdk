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

import org.hisp.dhis.android.core.common.valuetype.validation.failures.PhoneNumberFailure
import org.junit.Test

class PhoneNumberValidatorShould : ValidatorShouldHelper<PhoneNumberFailure>(PhoneNumberValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("652214478")
        valueShouldSuccess("+34 521 587 422")
        valueShouldSuccess("0034 521 587 422")
        valueShouldSuccess("0034521587422")
        valueShouldSuccess("+34521587422")
        valueShouldSuccess("+34 (521)-58 7422")
        valueShouldSuccess("+55 11 99999-5555")
        valueShouldSuccess("+65 6511 9266")
        valueShouldSuccess("+86 21 2230 1000")
        valueShouldSuccess("+9124 4723300")
        valueShouldSuccess("+821012345678")
        valueShouldSuccess("+593 7 282-3889")
        valueShouldSuccess("(+44) 0848 9123 456")
        valueShouldSuccess("+1 284 852 5500")
        valueShouldSuccess("+1 345 9490088")
        valueShouldSuccess("+32 2 702-9200")
    }

    @Test
    fun `Should fail with a number format exception when value is malformed`() {
        valueShouldFail("5fe2", PhoneNumberFailure.MalformedPhoneNumberException)
        valueShouldFail("987", PhoneNumberFailure.MalformedPhoneNumberException)
        valueShouldFail("984534534432324325347", PhoneNumberFailure.MalformedPhoneNumberException)
        valueShouldFail("234234234234ó", PhoneNumberFailure.MalformedPhoneNumberException)
        valueShouldFail("", PhoneNumberFailure.MalformedPhoneNumberException)
    }
}
