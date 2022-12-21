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
package org.hisp.dhis.android.core.parser.internal.expression

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.junit.Assert.assertThrows
import org.junit.Test

class ParserUtilsShould {

    @Test
    fun parse_expression_date() {
        mapOf(
            "2022-12-10" to listOf(2022, 12, 10, "2022-12-10"),
            "2022-05-08" to listOf(2022, 5, 8, "2022-05-08"),
            "2022-5-8" to listOf(2022, 5, 8, "2022-05-08"),
        ).forEach { (str, tokens) ->
            val date = ParserUtils.parseExpressionDate(str)
            assertThat(date.year).isEqualTo(tokens[0])
            assertThat(date.monthNumber).isEqualTo(tokens[1])
            assertThat(date.dayOfMonth).isEqualTo(tokens[2])
            assertThat(date.toString()).isEqualTo(tokens[3])
        }
    }

    @Test
    fun parse_invalid_date() {
        listOf(
            "",
            "null",
            "2022-08",
            "2022-13-35",
        ).forEach {
            assertThrows(ParserExceptionWithoutContext::class.java) { ParserUtils.parseExpressionDate(it) }
        }
    }
}
