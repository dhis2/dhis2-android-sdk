/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import org.hisp.dhis.android.core.common.valuetype.validation.failures.CoordinateFailure
import org.junit.Test

class CoordinateValidatorShould : ValidatorShouldHelper<CoordinateFailure>(CoordinateValidator) {

    @Test
    fun `Should success when passing valid values`() {
        valueShouldSuccess("[-11.876444,9.032566]")
        valueShouldSuccess("[+90.0, -127.554334]")
        valueShouldSuccess("[45, 180]")
        valueShouldSuccess("[-90, -180]")
        valueShouldSuccess("[+90, +180]")
        valueShouldSuccess("[0,0]")
        valueShouldSuccess("[47.1231231, 179.99999999]")
    }

    @Test
    fun `Should fail when passing malformed values`() {
        valueShouldFail("[-90., -180.]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[-368.532519,16.858788]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[-1,181]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[-181,1]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[+90.1, -100.111]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[-91, 123.456]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[-91,123.456]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("[045, 180]", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("", CoordinateFailure.CoordinateMalformedException)
        valueShouldFail("egs", CoordinateFailure.CoordinateMalformedException)
    }
}
