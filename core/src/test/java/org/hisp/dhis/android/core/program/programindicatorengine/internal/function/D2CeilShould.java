package org.hisp.dhis.android.core.program.programindicatorengine.internal.function;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class D2CeilShould {

    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedFirstExpr;

    private D2Ceil ceil = new D2Ceil();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedFirstExpr);
    }

    @Test
    public void evaluateMustReturnCeiledValue() {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("4.1");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("5"));

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0.8");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("1"));

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("5.1");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("6"));

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("1");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("1"));

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-9.3");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("-9"));

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-5.9");
        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("-5"));
    }

    @Test
    public void return_zero_when_number_is_invalid() {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("not a number");

        MatcherAssert.assertThat(ceil.evaluate(context, visitor), CoreMatchers.<Object>is("0"));
    }
}
