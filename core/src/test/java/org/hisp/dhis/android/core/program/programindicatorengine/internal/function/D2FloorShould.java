package org.hisp.dhis.android.core.program.programindicatorengine.internal.function;

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class D2FloorShould {

    @Mock
    private ExpressionParser.ExprContext context;

    @Mock
    private CommonExpressionVisitor visitor;

    @Mock
    private ExpressionParser.ExprContext mockedFirstExpr;

    private D2Floor floor = new D2Floor();

    @Before
    public void setUp() {
        when(context.expr(0)).thenReturn(mockedFirstExpr);
    }

    @Test
    public void evaluateMustReturnFlooredValue() {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("4.1");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("4");

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("0.8");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("0");

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("5.1");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("5");

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("1.0");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("1");

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-9.3");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("-10");

        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("-5.9");
        assertThat(floor.evaluate(context, visitor)).isEqualTo("-6");
    }

    @Test
    public void return_zero_when_number_is_invalid() {
        when(visitor.castStringVisit(mockedFirstExpr)).thenReturn("not a number");

        assertThat(floor.evaluate(context, visitor)).isEqualTo("0");
    }
}
