package org.hisp.dhis.android.core.program.programindicatorengine.internal.function;

import org.hamcrest.CoreMatchers;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class D2AddDaysShould {
    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedDateExpr;

    @Mock
    private ExpressionParser.ExprContext mockedIntExpr;

    private D2AddDays functionToTest = new D2AddDays();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedDateExpr);
        when(context.expr(1)).thenReturn(mockedIntExpr);
    }

    @Test
    public void return_new_date_with_days_added() {
        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2011-01-01");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("6.0");
        assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is(("2011-01-07")));

        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-10");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("1");
        assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is(("2010-10-11")));

        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-10");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("1.3");
        assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is(("2010-10-11")));

        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-10-31");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("1");
        assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is(("2010-11-01")));

        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-12-01");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("31");
        assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is(("2011-01-01")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_runtime_exception_if_first_argument_is_invalid() {
        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("bad date");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("6");

        functionToTest.evaluate(context, visitor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_second_argument_is_invalid() {
        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("2010-01-01");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("bad number");

        functionToTest.evaluate(context, visitor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_first_and_second_argument_is_invalid() {
        when(visitor.castStringVisit(mockedDateExpr)).thenReturn("bad date");
        when(visitor.castStringVisit(mockedIntExpr)).thenReturn("bad number");

        functionToTest.evaluate(context, visitor);
    }
}
