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
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class D2AddDaysShould {
    private val context: ExprContext = mock()
    private val visitor: CommonExpressionVisitor = mock()
    private val mockedDateExpr: ExprContext = mock()
    private val mockedIntExpr: ExprContext = mock()

    private val functionToTest = D2AddDays()

    @Before
    fun setUp() {
        whenever(context.expr(0)).thenReturn(mockedDateExpr)
        whenever(context.expr(1)).thenReturn(mockedIntExpr)
    }

    @Test
    fun return_new_date_with_days_added() {
        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2011-01-01")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("6.0")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2011-01-07")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-10")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("1")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2010-10-11")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-10")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("1.3")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2010-10-11")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-31")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("1")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2010-11-01")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-12-01")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("31")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2011-01-01")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-12-01T00:00:00.000")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("10")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2010-12-11")

        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-12-01T04:35:12.123")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("10")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2010-12-11")
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_runtime_exception_if_first_argument_is_invalid() {
        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("bad date")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("6")
        functionToTest.evaluate(context, visitor)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_if_second_argument_is_invalid() {
        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-01-01")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("bad number")
        functionToTest.evaluate(context, visitor)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_if_first_and_second_argument_is_invalid() {
        whenever(visitor.castStringVisit(mockedDateExpr)).thenReturn("bad date")
        whenever(visitor.castStringVisit(mockedIntExpr)).thenReturn("bad number")
        functionToTest.evaluate(context, visitor)
    }
}
