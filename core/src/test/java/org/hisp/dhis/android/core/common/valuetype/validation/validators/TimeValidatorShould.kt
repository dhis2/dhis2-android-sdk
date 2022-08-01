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

import org.hisp.dhis.android.core.common.valuetype.validation.failures.TimeFailure
import org.junit.Test

class TimeValidatorShould : ValidatorShouldHelper<TimeFailure>(TimeValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("00:00")
        valueShouldSuccess("0:00")
        valueShouldSuccess("02:20")
        valueShouldSuccess("2:20")
        valueShouldSuccess("11:59")
        valueShouldSuccess("12:00")
        valueShouldSuccess("12:59")
        valueShouldSuccess("23:59")
    }

    @Test
    fun `Should fail when passing malformed values`() {
        valueShouldFail("", TimeFailure.ParseException)
        valueShouldFail("sdf", TimeFailure.ParseException)
        valueShouldFail("00", TimeFailure.ParseException)
        valueShouldFail("0:", TimeFailure.ParseException)
        valueShouldFail(":0", TimeFailure.ParseException)
        valueShouldFail(":", TimeFailure.ParseException)
        valueShouldFail("2", TimeFailure.ParseException)
        valueShouldFail("23", TimeFailure.ParseException)
        valueShouldFail("24", TimeFailure.ParseException)
        valueShouldFail("08:85", TimeFailure.ParseException)
        valueShouldFail("26:25", TimeFailure.ParseException)
        valueShouldFail("-05:25", TimeFailure.ParseException)
        valueShouldFail("-00:00", TimeFailure.ParseException)
        valueShouldFail("-00:00", TimeFailure.ParseException)
        valueShouldFail("00:1985", TimeFailure.ParseException)
        valueShouldFail("24:00", TimeFailure.ParseException)
        valueShouldFail("02:2", TimeFailure.ParseException)
        valueShouldFail("2:2", TimeFailure.ParseException)
        valueShouldFail("0:0", TimeFailure.ParseException)
    }
}
