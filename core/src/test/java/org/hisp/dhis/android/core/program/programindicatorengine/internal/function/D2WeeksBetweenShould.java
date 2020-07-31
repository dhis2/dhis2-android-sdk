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
public class D2WeeksBetweenShould {

    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedFirstExpr;

    @Mock
    private ExpressionParser.ExprContext mockedSecondExpr;

    private D2WeeksBetween functionToTest = new D2WeeksBetween();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedFirstExpr);
        when(context.expr(1)).thenReturn(mockedSecondExpr);
    }

    @Test
    public void return_zero_if_some_date_is_not_present() {
        assertWeeksBetween(null, null, "0");
        assertWeeksBetween(null, "", "0");
        assertWeeksBetween("", null, "0");
        assertWeeksBetween("", "", "0");
    }

    @Test
    public void return_difference_of_weeks_of_two_dates() {
        assertWeeksBetween("2010-10-15", "2010-10-22", "1");
        assertWeeksBetween("2010-09-30", "2010-10-15", "2");
        assertWeeksBetween("2016-01-01", "2016-01-31", "4");
        assertWeeksBetween("2010-12-31", "2011-01-01", "0");

        assertWeeksBetween("2010-10-22", "2010-10-15", "-1");
        assertWeeksBetween("2010-10-15", "2010-09-30", "-2");
        assertWeeksBetween("2016-01-31", "2016-01-01", "-4");
        assertWeeksBetween("2011-01-01", "2010-12-31", "0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_first_date_is_invalid() {
        assertWeeksBetween("bad date", "2010-01-01", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_second_date_is_invalid() {
        assertWeeksBetween("2010-01-01", "bad date", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_first_and_second_date_is_invalid() {
        assertWeeksBetween("bad date", "bad date", null);
    }

    private void assertWeeksBetween(String startDate, String endDate, String weeksBetween) {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn(startDate);
        when(visitor.castStringVisit(mockedSecondExpr)).thenReturn(endDate);
        MatcherAssert
                .assertThat(functionToTest.evaluate(context, visitor), CoreMatchers.<Object>is((weeksBetween)));
    }
}
