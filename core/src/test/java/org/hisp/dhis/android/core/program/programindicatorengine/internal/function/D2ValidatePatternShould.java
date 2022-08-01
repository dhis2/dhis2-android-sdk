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

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class D2ValidatePatternShould {
    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedFirstExpr;

    @Mock
    private ExpressionParser.ExprContext mockedSecondExpr;

    private D2ValidatePattern functionToTest = new D2ValidatePattern();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedFirstExpr);
        when(context.expr(1)).thenReturn(mockedSecondExpr);
    }

    @Test
    public void return_true_if_pattern_match() {
        assertValidatePattern("123", "123", "true");
        assertValidatePattern("27123456789", "27\\d{2}\\d{3}\\d{4}", "true");
        assertValidatePattern("27123456789", "27\\d{9}", "true");
        assertValidatePattern("abc123", "abc123", "true");
        assertValidatePattern("9999/99/9", "\\d{4}/\\d{2}/\\d", "true");
        assertValidatePattern("9999/99/9", "[0-9]{4}/[0-9]{2}/[0-9]", "true");
    }

    @Test
    public void return_false_for_non_matching_pairs() {
        assertValidatePattern("1999/99/9", "\\[9]{4}/\\d{2}/\\d", "false");
        assertValidatePattern("9999/99/", "[0-9]{4}/[0-9]{2}/[0-9]", "false");
        assertValidatePattern("abc123", "xyz", "false");
        assertValidatePattern("abc123", "^bc", "false");
        assertValidatePattern("abc123", "abc12345", "false");
        assertValidatePattern("123", "567", "false");
    }

    private void assertValidatePattern(String input, String regex, String isPatternValid) {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn(input);
        when(visitor.castStringVisit(mockedSecondExpr)).thenReturn(regex);
        assertThat(functionToTest.evaluate(context, visitor)).isEqualTo(isPatternValid);
    }
}
