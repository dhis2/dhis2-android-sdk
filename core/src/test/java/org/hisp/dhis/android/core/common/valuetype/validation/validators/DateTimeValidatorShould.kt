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

import org.hisp.dhis.android.core.common.valuetype.validation.failures.DateTimeFailure
import org.junit.Test

class DateTimeValidatorShould : ValidatorShouldHelper<DateTimeFailure>(DateTimeValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("2021-04-15T23:59")
        valueShouldSuccess("0001-04-15T23:59")
        valueShouldSuccess("1995-01-01T01:01")
    }

    @Test
    fun `Should fail when passing malformed values`() {
        valueShouldFail("", DateTimeFailure.ParseException)
        valueShouldFail("asd", DateTimeFailure.ParseException)
        valueShouldFail("12021-04-15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("2021-40-15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("2021-04-80T25:59", DateTimeFailure.ParseException)
        valueShouldFail("2021-04-15T23:70", DateTimeFailure.ParseException)
        valueShouldFail("2021-00-15T23:20", DateTimeFailure.ParseException)
        valueShouldFail("2021-01-00T23:20", DateTimeFailure.ParseException)
        valueShouldFail("2021-01-02N23:20", DateTimeFailure.ParseException)
        valueShouldFail("-2021-04-15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("-0000-04-15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("2021-04-15T23:59-", DateTimeFailure.ParseException)
        valueShouldFail("2021/04/15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("0000-04-15T23:59", DateTimeFailure.ParseException)
        valueShouldFail("1995-1-1T1:01", DateTimeFailure.ParseException)
        valueShouldFail("0004-1-1T1:01", DateTimeFailure.ParseException)
    }
}
