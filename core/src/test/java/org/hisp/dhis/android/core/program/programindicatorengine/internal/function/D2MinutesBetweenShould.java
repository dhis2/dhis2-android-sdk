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
public class D2MinutesBetweenShould {
    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedFirstExpr;

    @Mock
    private ExpressionParser.ExprContext mockedSecondExpr;

    private D2MinutesBetween functionToTest = new D2MinutesBetween();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedFirstExpr);
        when(context.expr(1)).thenReturn(mockedSecondExpr);
    }

    @Test
    public void return_zero_if_some_date_is_not_present() {
        assertMinutesBetween(null, null, "0");
        assertMinutesBetween(null, "", "0");
        assertMinutesBetween("", null, "0");
        assertMinutesBetween("", "", "0");
    }

    @Test
    public void evaluate_correct_number_of_minutes() {
        assertMinutesBetween("2010-10-20T00:00:00.000", "2010-10-20T00:05:00.000", "5");
        assertMinutesBetween("2010-10-20T00:00:00.000", "2010-10-20T01:05:00.000", "65");
        assertMinutesBetween("2010-10-20T23:58:00.000", "2010-10-21T00:02:04.000", "4");

        assertMinutesBetween("2010-10-20T00:05:00.000", "2010-10-20T00:00:00.000", "-5");
        assertMinutesBetween("2010-10-20T01:05:00.000", "2010-10-20T00:00:00.000", "-65");
        assertMinutesBetween("2010-10-21T00:02:04.000", "2010-10-20T23:58:00.000", "-4");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_first_date_is_invalid() {
        assertMinutesBetween("bad date", "2010-01-01", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_second_date_is_invalid() {
        assertMinutesBetween("2010-01-01", "bad date", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_first_and_second_date_is_invalid() {
        assertMinutesBetween("bad date", "bad date", null);
    }

    private void assertMinutesBetween(String startDate, String endDate, String daysBetween) {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn(startDate);
        when(visitor.castStringVisit(mockedSecondExpr)).thenReturn(endDate);
        MatcherAssert.assertThat(functionToTest.evaluate(context, visitor),
                CoreMatchers.<Object>is((daysBetween)));
    }
}
