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
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class D2ZpvcShould {
    private val context: ExprContext = mock()
    private val visitor: CommonExpressionVisitor = mock()

    private val functionToTest = D2Zpvc()

    @Test
    fun return_count_of_non_negative_values_in_arguments() {
        val mockNegative = Mockito.mock(ExprContext::class.java)
        val mockZero = Mockito.mock(ExprContext::class.java)
        val mockPositive = Mockito.mock(ExprContext::class.java)

        whenever(context.expr()).thenReturn(listOf(mockNegative, mockZero, mockPositive))
        whenever(visitor.castStringVisit(mockNegative)).thenReturn("-1")
        whenever(visitor.castStringVisit(mockZero)).thenReturn("0")
        whenever(visitor.castStringVisit(mockPositive)).thenReturn("2")
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun throw_illegal_argument_exception_for_no_number_argument() {
        val mockText = Mockito.mock(ExprContext::class.java)
        val mockNull = Mockito.mock(ExprContext::class.java)
        val mockNumber = Mockito.mock(ExprContext::class.java)
        whenever(context.expr()).thenReturn(listOf(mockText, mockNull, mockNumber))
        whenever(visitor.castStringVisit(mockText)).thenReturn("sxsx")
        functionToTest.evaluate(context, visitor)
    }
}
