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
package org.hisp.dhis.android.core.program.programindicatorengine.internal.function

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class D2MinutesBetweenShould {
    private val context: ExprContext = mock()
    private val visitor: CommonExpressionVisitor = mock()
    private val mockedFirstExpr: ExprContext = mock()
    private val mockedSecondExpr: ExprContext = mock()

    private val functionToTest = D2MinutesBetween()

    @Before
    fun setUp() {
        whenever(context.expr(0)).thenReturn(mockedFirstExpr)
        whenever(context.expr(1)).thenReturn(mockedSecondExpr)
    }

    @Test
    fun return_zero_if_some_date_is_not_present() {
        assertMinutesBetween(null, null, "0")
        assertMinutesBetween(null, "", "0")
        assertMinutesBetween("", null, "0")
        assertMinutesBetween("", "", "0")
    }

    @Test
    fun evaluate_correct_number_of_minutes() {
        assertMinutesBetween("2010-10-20T00:00:00.000", "2010-10-20T00:05:00.000", "5")
        assertMinutesBetween("2010-10-20T00:00:00.000", "2010-10-20T01:05:00.000", "65")
        assertMinutesBetween("2010-10-20T23:58:00.000", "2010-10-21T00:02:04.000", "4")
        assertMinutesBetween("2010-10-20T00:05:00.000", "2010-10-20T00:00:00.000", "-5")
        assertMinutesBetween("2010-10-20T01:05:00.000", "2010-10-20T00:00:00.000", "-65")
        assertMinutesBetween("2010-10-21T00:02:04.000", "2010-10-20T23:58:00.000", "-4")
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_if_first_date_is_invalid() {
        assertMinutesBetween("bad date", "2010-01-01", null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_if_second_date_is_invalid() {
        assertMinutesBetween("2010-01-01", "bad date", null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_if_first_and_second_date_is_invalid() {
        assertMinutesBetween("bad date", "bad date", null)
    }

    private fun assertMinutesBetween(startDate: String?, endDate: String?, daysBetween: String?) {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn(startDate)
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn(endDate)
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo(daysBetween)
    }
}
