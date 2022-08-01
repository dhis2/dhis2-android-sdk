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

package org.hisp.dhis.android.core.program.programindicatorengine.internal.function;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class D2ZpvcShould {
    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    private D2Zpvc functionToTest = new D2Zpvc();

    @Test
    public void return_count_of_non_negative_values_in_arguments() {
        ExpressionParser.ExprContext mockNegative = mock(ExpressionParser.ExprContext.class);
        ExpressionParser.ExprContext mockZero = mock(ExpressionParser.ExprContext.class);
        ExpressionParser.ExprContext mockPositive = mock(ExpressionParser.ExprContext.class);

        when(context.expr()).thenReturn(Lists.newArrayList(mockNegative, mockZero, mockPositive));
        when(visitor.castStringVisit(mockNegative)).thenReturn("-1");
        when(visitor.castStringVisit(mockZero)).thenReturn("0");
        when(visitor.castStringVisit(mockPositive)).thenReturn("2");

        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo("2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_for_no_number_argument() {
        ExpressionParser.ExprContext mockText = mock(ExpressionParser.ExprContext.class);
        ExpressionParser.ExprContext mockNull = mock(ExpressionParser.ExprContext.class);
        ExpressionParser.ExprContext mockNumber = mock(ExpressionParser.ExprContext.class);

        when(context.expr()).thenReturn(Lists.newArrayList(mockText, mockNull, mockNumber));
        when(visitor.castStringVisit(mockText)).thenReturn("sxsx");

        functionToTest.evaluate(context, visitor);
    }
}
