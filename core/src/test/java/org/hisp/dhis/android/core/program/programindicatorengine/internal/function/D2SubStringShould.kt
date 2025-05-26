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
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class D2SubStringShould {
    private val context: ExprContext = mock()
    private val visitor: CommonExpressionVisitor = mock()
    private val mockedFirstExpr: ExprContext = mock()
    private val mockedSecondExpr: ExprContext = mock()
    private val mockedThirdExpr: ExprContext = mock()

    private val functionToTest = D2Substring()

    @Before
    fun setUp() {
        whenever(context.expr(0)).thenReturn(mockedFirstExpr)
        whenever(context.expr(1)).thenReturn(mockedSecondExpr)
        whenever(context.expr(2)).thenReturn(mockedThirdExpr)
    }

    @Test
    fun return_empty_string_for_null_inputs() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn(null)
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("0")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("0")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("10")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("")
    }

    @Test
    fun return_substring_from_start_index_to_end_index_of_input_string() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("abcdef")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("0")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("0")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("abcdef")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("0")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("1")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("a")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("abcdef")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("-10")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("1")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("a")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("abcdef")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("2")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("4")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("cd")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("abcdef")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("2")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("10")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("cdef")
    }

    @Test(expected = ParserExceptionWithoutContext::class)
    fun throw_parser_exception_without_context_if_start_index_is_a_text() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("test_variable_one")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("variable")
        functionToTest.evaluate(context, visitor)
    }

    @Test(expected = ParserExceptionWithoutContext::class)
    fun throw_parser_exception_without_context_if_end_index_is_a_text() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("test_variable_one")
        whenever(visitor.castStringVisit(mockedSecondExpr)).thenReturn("3")
        whenever(visitor.castStringVisit(mockedThirdExpr)).thenReturn("ede")
        functionToTest.evaluate(context, visitor)
    }
}
