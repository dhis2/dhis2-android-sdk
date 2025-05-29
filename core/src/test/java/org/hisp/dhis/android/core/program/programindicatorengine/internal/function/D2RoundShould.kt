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
class D2RoundShould {
    private val context: ExprContext = mock()
    private val visitor: CommonExpressionVisitor = mock()
    private val mockedFirstExpr: ExprContext = mock()

    private val round = D2Round()

    @Before
    fun setUp() {
        whenever(context.expr(0)).thenReturn(mockedFirstExpr)
    }

    @Test
    fun return_argument_rounded_up_to_nearest_whole_number() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0")
        assertThat(round.evaluate(context, visitor)).isEqualTo("0")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0.8")
        assertThat(round.evaluate(context, visitor)).isEqualTo("1")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0.4999")
        assertThat(round.evaluate(context, visitor)).isEqualTo("0")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0.5001")
        assertThat(round.evaluate(context, visitor)).isEqualTo("1")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-9.3")
        assertThat(round.evaluate(context, visitor)).isEqualTo("-9")
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-9.8")
        assertThat(round.evaluate(context, visitor)).isEqualTo("-10")
    }

    @Test
    fun return_zero_when_number_is_invalid() {
        whenever(visitor.castStringVisit(mockedFirstExpr)).thenReturn("not a number")
        assertThat(round.evaluate(context, visitor)).isEqualTo("0")
    }
}
