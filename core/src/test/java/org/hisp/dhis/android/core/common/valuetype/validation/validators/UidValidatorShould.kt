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

import org.hisp.dhis.android.core.common.valuetype.validation.failures.UidFailure
import org.junit.Test

class UidValidatorShould : ValidatorShouldHelper<UidFailure>(UidValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("E2vUc6z2CV6")
        valueShouldSuccess("FsDo0TAPHPI")
        valueShouldSuccess("01234567890")
        valueShouldSuccess("abcdefghijk")
        valueShouldSuccess("LMNOPQRSTUW")
    }

    @Test
    fun `Should fail when passing values with less than eleven chars`() {
        valueShouldFail("FsDo0TAPHP", UidFailure.LessThanElevenCharsException)
    }

    @Test
    fun `Should fail when passing values with more than eleven chars`() {
        valueShouldFail("FsDo0TAPHPI2", UidFailure.MoreThanElevenCharsException)
        valueShouldFail("FsDo0TAPHPI FsDo0TAPHPI", UidFailure.MoreThanElevenCharsException)
        valueShouldFail(".FsDo0TAPHPI", UidFailure.MoreThanElevenCharsException)
    }

    @Test
    fun `Should fail when passing malformed values`() {
        valueShouldFail("ñsDo0TAPHPI", UidFailure.MalformedUidException)
        valueShouldFail("ÁsDo0TAPHPI", UidFailure.MalformedUidException)
        valueShouldFail("ásDo0TAPHPI", UidFailure.MalformedUidException)
        valueShouldFail(".sDo0TAPHPI", UidFailure.MalformedUidException)
    }
}
