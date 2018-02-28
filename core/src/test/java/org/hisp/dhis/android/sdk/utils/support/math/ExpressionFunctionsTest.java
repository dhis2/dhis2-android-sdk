package org.hisp.dhis.android.sdk.utils.support.math;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.sdk.utils.api.ValueType.INTEGER;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.addDays;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.ceil;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.concatenate;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.condition;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.count;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.countIfValue;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.countIfZeroPos;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.daysBetween;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.floor;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.hasValue;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.lastEventDate;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.left;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.length;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.modulus;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.monthsBetween;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.oizp;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.right;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.round;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.split;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.substring;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.validatePattern;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.weeksBetween;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.yearsBetween;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.zing;
import static org.hisp.dhis.android.sdk.utils.support.math.ExpressionFunctions.zpvc;
import static org.junit.Assert.assertThat;

import static java.util.Arrays.asList;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.utils.api.ValueType;
import org.hisp.dhis.android.sdk.utils.services.VariableService;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionFunctionsTest {
    private static Object trueValue = new Object();
    private static Object falseValue = new Object();

    @Test
    public void zingShouldReturnNullForNullInput() {
        assertThat(zing(null), is(equalTo(null)));
    }

    @Test
    public void zingShouldReturnSameValueForNonNegativeInput() {
        assertThat(zing(0), is(equalTo(0.0)));
        assertThat(zing(1), is(equalTo(1.0)));
        assertThat(zing(5), is(equalTo(5.0)));
        assertThat(zing(0.1), is(equalTo(0.1)));
        assertThat(zing(1.1), is(equalTo(1.1)));
    }

    @Test
    public void zingShouldReturnZeroForNegativeInput() {
        assertThat(zing(-1), is(equalTo(0.0)));
        assertThat(zing(-10), is(equalTo(0.0)));
        assertThat(zing(-1.1), is(equalTo(0.0)));
    }

    @Test
    public void oizpShouldReturnNullForNullInput() {
        assertThat(oizp(null), is(equalTo(null)));
    }

    @Test
    public void oizpShouldReturnOneForNonNegativeInputs() {
        assertThat(oizp(0), is(equalTo(1.0)));
        assertThat(oizp(1), is(equalTo(1.0)));
        assertThat(oizp(10), is(equalTo(1.0)));
    }

    @Test
    public void oizpShouldReturnZeroForNegativeInputs() {
        assertThat(oizp(-1), is(equalTo(0.0)));
        assertThat(oizp(-10), is(equalTo(0.0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void zpvcShouldReturnThrowIllegalArgumentExceptionForNullInput() {
        zpvc();
    }

    @Test
    public void zpvcShouldReturnCountOfNonNegativeValuesInArguments() {
        assertThat(zpvc(null, 0, 1, -1, 2, -2, 3), is(equalTo(4)));
    }

    @Test
    public void conditionShouldReturnFalseForUntrueConditions() {
        assertThat(condition("", trueValue, falseValue), is(equalTo(falseValue)));
        assertThat(condition("false", trueValue, falseValue), is(equalTo(falseValue)));
        assertThat(condition("0 == 1", trueValue, falseValue), is(equalTo(falseValue)));
        assertThat(condition("2*3 != 12/2", trueValue, falseValue), is(equalTo(falseValue)));
        assertThat(condition("0 > 1", trueValue, falseValue), is(equalTo(falseValue)));
        assertThat(condition("!true", trueValue, falseValue), is(equalTo(falseValue)));
    }

    @Test
    public void conditionShouldReturnTrueForTrueConditions() {
        assertThat(condition("true", trueValue, falseValue), is(equalTo(trueValue)));
        assertThat(condition("1 == 1", trueValue, falseValue), is(equalTo(trueValue)));
        assertThat(condition("2*3 == 12/2", trueValue, falseValue), is(equalTo(trueValue)));
        assertThat(condition("10 > 1", trueValue, falseValue), is(equalTo(trueValue)));
        assertThat(condition("!false", trueValue, falseValue), is(equalTo(trueValue)));
    }

    @Test
    public void daysBetweenShouldReturnZeroIfSomeDateIsNotPresent() throws ParseException {
        assertThat(daysBetween(null, null), is(equalTo(0)));
        assertThat(daysBetween(null, ""), is(equalTo(0)));
        assertThat(daysBetween("", null), is(equalTo(0)));
        assertThat(daysBetween("", ""), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void daysBetweenShouldRaiseIllegalArgumentExceptionIfFirstDateIsInvalid()
            throws ParseException {
        daysBetween("bad date", "2010-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void daysBetweenShouldRaiseIllegalArgumentExceptionIfSecondDateIsInvalid()
            throws ParseException {
        daysBetween("2010-01-01", "bad date");
    }

    @Test(expected = IllegalArgumentException.class)
    public void daysBetweenShouldRaiseIllegalArgumentExceptionIfFirstAndSecondDateIsInvalid()
            throws ParseException {
        daysBetween("bad date", "bad date");
    }

    @Test
    public void daysBetweenShouldReturnDifferenceOfDaysOfTwoDates() throws ParseException {
        assertThat(daysBetween("2010-10-15", "2010-10-20"), is(equalTo(5)));
        assertThat(daysBetween("2010-09-30", "2010-10-15"), is(equalTo(15)));
        assertThat(daysBetween("2010-12-31", "2011-01-01"), is(equalTo(1)));

        assertThat(daysBetween("2010-10-20", "2010-10-15"), is(equalTo(-5)));
        assertThat(daysBetween("2010-10-15", "2010-09-30"), is(equalTo(-15)));
        assertThat(daysBetween("2011-01-01", "2010-12-31"), is(equalTo(-1)));
    }

    @Test
    public void weeksBetweenShouldReturnZeroIfSomeDateIsNotPresent() throws ParseException {
        assertThat(weeksBetween(null, null), is(equalTo(0)));
        assertThat(weeksBetween(null, ""), is(equalTo(0)));
        assertThat(weeksBetween("", null), is(equalTo(0)));
        assertThat(weeksBetween("", ""), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void weeksBetweenShouldRaiseIllegalArgumentExceptionIfFirstDateIsInvalid()
            throws ParseException {
        weeksBetween("bad date", "2010-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void weeksBetweenShouldRaiseIllegalArgumentExceptionIfSecondDateIsInvalid()
            throws ParseException {
        weeksBetween("2010-01-01", "bad date");
    }

    @Test
    public void weeksBetweenShouldReturnDifferenceOfWeeksOfTwoDates() throws ParseException {
        assertThat(weeksBetween("2010-10-15", "2010-10-22"), is(equalTo(1)));
        assertThat(weeksBetween("2010-09-30", "2010-10-15"), is(equalTo(2)));
        assertThat(weeksBetween("2010-12-31", "2011-01-01"), is(equalTo(0)));

        assertThat(weeksBetween("2010-10-22", "2010-10-15"), is(equalTo(-1)));
        assertThat(weeksBetween("2010-10-15", "2010-09-30"), is(equalTo(-2)));
        assertThat(weeksBetween("2011-01-01", "2010-12-31"), is(equalTo(0)));
    }

    @Test
    public void monthsBetweenShouldReturnZeroIfSomeDateIsNotPresent() throws ParseException {
        assertThat(monthsBetween(null, null), is(equalTo(0)));
        assertThat(monthsBetween(null, ""), is(equalTo(0)));
        assertThat(monthsBetween("", null), is(equalTo(0)));
        assertThat(monthsBetween("", ""), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void monthsBetweenShouldRaiseIllegalArgumentExceptionIfFirstDateIsInvalid()
            throws ParseException {
        monthsBetween("bad date", "2010-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void monthsBetweenShouldRaiseIllegalArgumentExceptionIfSecondDateIsInvalid()
            throws ParseException {
        monthsBetween("2010-01-01", "bad date");
    }

    @Test
    public void monthsBetweenShouldReturnDifferenceOfMonthsOfTwoDates() throws ParseException {
        assertThat(monthsBetween("2010-10-15", "2010-10-22"), is(equalTo(0)));
        assertThat(monthsBetween("2010-09-30", "2010-10-31"), is(equalTo(1)));
        assertThat(monthsBetween("2013-01-31", "2013-02-01"), is(equalTo(1)));
        assertThat(monthsBetween("2016-01-01", "2016-07-31"), is(equalTo(6)));
        assertThat(monthsBetween("2015-01-01", "2016-06-30"), is(equalTo(17)));

        assertThat(monthsBetween("2010-10-22", "2010-10-15"), is(equalTo(0)));
        assertThat(monthsBetween("2010-10-31", "2010-09-30"), is(equalTo(-1)));
        assertThat(monthsBetween("2013-02-01", "2013-01-31"), is(equalTo(-1)));
        assertThat(monthsBetween("2016-07-31", "2016-01-01"), is(equalTo(-6)));
        assertThat(monthsBetween("2016-06-30", "2015-01-01"), is(equalTo(-17)));
    }

    @Test
    public void yearsBetweenShouldReturnZeroIfSomeDateIsNotPresent() throws ParseException {
        assertThat(yearsBetween(null, null), is(equalTo(0)));
        assertThat(yearsBetween(null, ""), is(equalTo(0)));
        assertThat(yearsBetween("", null), is(equalTo(0)));
        assertThat(yearsBetween("", ""), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void yearsBetweenShouldRaiseIllegalArgumentExceptionIfFirstDateIsInvalid()
            throws ParseException {
        yearsBetween("bad date", "2010-01-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void yearsBetweenShouldRaiseIllegalArgumentExceptionIfSecondDateIsInvalid()
            throws ParseException {
        yearsBetween("2010-01-01", "bad date");
    }

    @Test
    public void yearsBetweenShouldReturnDifferenceOfYearsOfTwoDates() throws ParseException {
        assertThat(yearsBetween("2010-10-15", "2010-10-22"), is(equalTo(0)));
        assertThat(yearsBetween("2010-09-30", "2011-10-31"), is(equalTo(1)));
        assertThat(yearsBetween("2010-01-01", "2016-06-30"), is(equalTo(6)));
        assertThat(yearsBetween("2015-01-01", "2016-06-30"), is(equalTo(1)));

        assertThat(yearsBetween("2010-10-22", "2010-10-22"), is(equalTo(0)));
        assertThat(yearsBetween("2011-10-31", "2010-09-30"), is(equalTo(-1)));
        assertThat(yearsBetween("2016-06-30", "2010-01-01"), is(equalTo(-6)));
        assertThat(yearsBetween("2016-06-30", "2015-01-01"), is(equalTo(-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void floorShouldRaiseIllegalArgumentExceptionForNullInput() {
        floor(null);
    }

    @Test
    public void floorShouldReturnArgumentRoundedDownToNearestWholeNumber() {
        assertThat(floor(0), is(equalTo(0)));
        assertThat(floor(0.8), is(equalTo(0)));
        assertThat(floor(1.0), is(equalTo(1)));
        assertThat(floor(-9.3), is(equalTo(-10)));
        assertThat(floor(5.9), is(equalTo(5)));
        assertThat(floor(5), is(equalTo(5)));
        assertThat(floor(-5), is(equalTo(-5)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void modulusShouldRaiseIllegalArgumentExceptionForNullInput() {
        modulus(null, null);
    }

    @Test(expected = ArithmeticException.class)
    public void modulusWithZeroDivisorShouldRaiseArithmeticException() {
        modulus(2, 0);
    }

    @Test
    public void modulusShouldReturnTheRemainderAfterDivisionOfInputs() {
        assertThat(modulus(0, 2), is(equalTo(0)));
        assertThat(modulus(11, 3), is(equalTo(2)));
        assertThat(modulus(-11, 3), is(equalTo(-2)));
        assertThat(modulus(11.5, 3.2), is(equalTo(2)));
        assertThat(modulus(-11.5, 3.2), is(equalTo(-2)));
    }

    @Test
    public void concatenateShouldReturnConcatenatedStrings() {
        assertThat(concatenate(), is(equalTo("")));
        assertThat(concatenate("hello"), is(equalTo("hello")));
        assertThat(concatenate("hello", " ", "there", "!"), is(equalTo("hello there!")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDaysShouldRaiseIllegalArgumentExceptionForEmptyDate() {
        addDays("", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDaysShouldRaiseIllegalArgumentExceptionForNullDaysDifference() {
        addDays("2010-10-10", null);
    }

    @Test
    public void addDaysShouldReturnNewDateWithDaysAdded() {
        assertThat(addDays("2010-10-10", 1), is(equalTo("2010-10-11")));
        assertThat(addDays("2010-10-31", 1), is(equalTo("2010-11-01")));
        assertThat(addDays("2010-12-01", 31), is(equalTo("2011-01-01")));
    }

    private class Variable {
        String variableValue;
        Boolean hasValue;
        List<String> allValues;
        String eventDate;
        ValueType variableType = INTEGER;

        public Variable hasNoValue() {
            this.hasValue = false;
            return this;
        }

        public Variable withValue(String variableValue) {
            this.variableValue = variableValue;
            return this;
        }

        public Variable withValues(List<String> allValues) {
            this.allValues = allValues;
            return this;
        }

        public Variable withEventDate(String eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public Variable withVariableType(ValueType variableType) {
            this.variableType = variableType;
            return this;
        }

        public ProgramRuleVariable build() {
            Boolean finalHasValue =
                    hasValue != null ? hasValue : variableValue != "null" && variableValue != "";
            ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
            programRuleVariable.setVariableType(variableType);
            programRuleVariable.setVariableValue(variableValue);
            programRuleVariable.setHasValue(finalHasValue);
            programRuleVariable.setAllValues(allValues);
            programRuleVariable.setVariableEventDate(eventDate);
            return programRuleVariable;
        }
    }

    private class ProgramRuleVariableMapSetter {
        private Map<String, ProgramRuleVariable> programRuleVariableMap = new HashMap<>();

        public ProgramRuleVariableMapSetter addVariable(String variableName,
                ProgramRuleVariable programRuleVariable) {
            programRuleVariableMap.put(variableName, programRuleVariable);
            return this;
        }

        public void set() {
            VariableService.getInstance().setProgramRuleVariableMap(programRuleVariableMap);
        }
    }

    @Test
    public void countShouldReturnZeroForNonExistingVariable() {
        new ProgramRuleVariableMapSetter().set();
        assertThat(count("nonexisting"), is(equalTo(0)));
    }

    @Test
    public void countShouldReturnZeroForProgramRuleVariableWithoutValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar",
                        new Variable().withValue("1.0").hasNoValue().withValues(
                                Arrays.asList("1.0", "2.0")).build())
                .set();
        assertThat(count("myvar"), is(equalTo(0)));
    }

    @Test
    public void countShouldReturnSizeOfValuesForProgramRuleVariableWithValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar",
                        new Variable().withValue("1.0").withValues(
                                Arrays.asList("1.0", "2.0")).build())
                .set();
        assertThat(count("myvar"), is(equalTo(2)));
    }

    @Test
    public void countShouldReturnOneForProgramRuleVariableWithUndefinedValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar", new Variable().withValue("1.0").build())
                .set();
        assertThat(count("myvar"), is(equalTo(1)));
    }

    @Test
    public void countIfZeroPosShouldReturnZeroForNonExistingVariable() {
        new ProgramRuleVariableMapSetter().set();
        assertThat(countIfZeroPos("nonexisting"), is(equalTo(0)));
    }

    @Test
    public void countIfZeroPosShouldReturnZeroForProgramRuleVariableWithoutValue() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar",
                        new Variable().withValue("1.0").hasNoValue().withValues(
                                Arrays.asList("1.0", "2.0")).build())
                .set();
        assertThat(countIfZeroPos("myvar"), is(equalTo(0)));
    }

    @Test
    public void
    countIfZeroPosShouldReturnSizeOfZeroOrPositiveValuesForProgramRuleVariableWithValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar",
                        new Variable().withValue("1.0").withValues(
                                Arrays.asList("1.0", null, "2.0", "-3.0")).build())
                .set();
        assertThat(countIfZeroPos("myvar"), is(equalTo(2)));
    }

    @Test
    public void
    countIfZeroPosShouldReturnZeroForProgramRuleVariableWithUndefinedValuesAndValueNotSet() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar", new Variable().build())
                .set();
        assertThat(countIfZeroPos("myvar"), is(equalTo(0)));
    }

    @Test
    public void
    countIfZeroPosShouldReturnOneForProgramRuleVariableWithValuesButNotDefinedAndValueSet() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar", new Variable().withValue("1.0").build())
                .set();
        assertThat(countIfZeroPos("myvar"), is(equalTo(1)));
    }

    @Test
    public void countIfValueShouldReturnZeroForNonExistingVariable() {
        new ProgramRuleVariableMapSetter().set();
        assertThat(countIfValue("nonexisting","textToCompare"), is(equalTo(0)));
    }

    @Test
    public void countIfValueShouldReturnCountOfMatchingValuesForProgramRuleVariableWithValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar",
                        new Variable().withValue("1.0").withValues(
                                Arrays.asList("1.0", null, "2.0", "1.0")).build())
                .set();
        assertThat(countIfValue("myvar","1.0"), is(equalTo(2)));
    }

    @Test
    public void countIfValueShouldReturnOneForProgramRuleVariableWithUnsetValues() {
        new ProgramRuleVariableMapSetter()
                .addVariable("myvar", new Variable().withValue("1.0").build())
                .set();
        assertThat(countIfValue("myvar","1.0"), is(equalTo(1)));
    }

    @Test
    public void ceilShouldReturnSmallestGreaterOrEqualThanAGivenNumber() {
        assertThat(ceil(0), is(equalTo(0.0)));
        assertThat(ceil(0.8), is(equalTo(1.0)));
        assertThat(ceil(1.0), is(equalTo(1.0)));
        assertThat(ceil(-9.3), is(equalTo(-9.0)));
    }

    @Test
    public void roundShouldReturnTheNearestIntegerOfAGivenNumber() {
        assertThat(round(0), is(equalTo(0L)));
        assertThat(round(0.8), is(equalTo(1L)));
        assertThat(round(0.4999), is(equalTo(0L)));
        assertThat(round(0.5001), is(equalTo(1L)));
        assertThat(round(-9.3), is(equalTo(-9L)));
        assertThat(round(-9.8), is(equalTo(-10L)));
    }

    @Test
    public void hasValueShouldReturnFalseForNonExistingVariable() {
        new ProgramRuleVariableMapSetter().set();
        assertThat(hasValue("nonexisting"), is(equalTo(false)));
    }

    @Test
    public void hasValueShouldReturnFalseForExistingVariableWithoutValue() {
        new ProgramRuleVariableMapSetter()
                .addVariable("some-var", new Variable().hasNoValue().build())
                .set();
        assertThat(hasValue("some-var"), is(equalTo(false)));
    }

    @Test
    public void hasValueShouldReturnTrueForExistingVariableWithValue() {
        new ProgramRuleVariableMapSetter()
                .addVariable("some-var", new Variable().withValue("1.0").build())
                .set();
        assertThat(hasValue("some-var"), is(equalTo(true)));
    }

    @Test
    public void lastEventDateShouldReturnEmptydStringForNonExistingVariable() {
        new ProgramRuleVariableMapSetter().set();
        assertThat(lastEventDate("nonexisting"), is(equalTo("")));
    }

    @Test
    public void lastEventDateShouldReturnEmptyStringForExistingVariableWithoutEventDate() {
        new ProgramRuleVariableMapSetter()
                .addVariable("some-var", new Variable().build())
                .set();
        assertThat(lastEventDate("some-var"), is(equalTo("")));
    }

    @Test
    public void lastEventDateShouldReturnDateForExistingVariableWithEventDate() {
        new ProgramRuleVariableMapSetter()
                .addVariable("some-var", new Variable().withEventDate("2010-10-20").build())
                .set();
        assertThat(lastEventDate("some-var"), is(equalTo("2010-10-20")));
    }

    @Test
    public void validatePatternShouldReturnFalseForNonMatchingPairs() {
        assertThat(validatePattern("abc123", "xyz"), is(equalTo(false)));
        assertThat(validatePattern("abc123", "^bc"), is(equalTo(false)));
        assertThat(validatePattern("abc123", "abc12345"), is(equalTo(false)));
        assertThat(validatePattern("1999/99/9", "\\[9]{4}/\\d{2}/\\d"), is(equalTo(false)));
        assertThat(validatePattern("9999/99/", "[0-9]{4}/[0-9]{2}/[0-9]"), is(equalTo(false)));
    }

    @Test
    public void validatePatternShouldReturnTrueForFullMatchingPairs() {
        assertThat(validatePattern("abc123", "abc123"), is(equalTo(true)));
        assertThat(validatePattern("abc123", "abc"), is(equalTo(false)));
        assertThat(validatePattern("abc123", "123"), is(equalTo(false)));
        assertThat(validatePattern(123, "12"), is(equalTo(false)));
        assertThat(validatePattern(123, "123"), is(equalTo(true)));
        assertThat(validatePattern("27123456789", "27\\d{2}\\d{3}\\d{4}"), is(equalTo(true)));
        assertThat(validatePattern("9999/99/9", "\\d{4}/\\d{2}/\\d"), is(equalTo(true)));
        assertThat(validatePattern("9999/99/9", "[0-9]{4}/[0-9]{2}/[0-9]"), is(equalTo(true)));
    }

    @Test
    public void validatePatternShouldWorkBeyondMaxIntValue() {
        assertThat(validatePattern(27123456789L, "27\\d{9}"), is(equalTo(true)));
    }

    @Test
    public void leftShouldReturnEmptyStringForNullInput() {
        assertThat(left(null, 0), is(equalTo("")));
        assertThat(left(null, 10), is(equalTo("")));
        assertThat(left(null, -10), is(equalTo("")));
    }

    @Test
    public void leftShouldReturnSubstringOfInputStringFromTheBeginning() {
        assertThat(left("abcdef", 0), is(equalTo("")));
        assertThat(left("abcdef", -5), is(equalTo("")));
        assertThat(left("abcdef", 2), is(equalTo("ab")));
        assertThat(left("abcdef", 30), is(equalTo("abcdef")));
    }

    @Test
    public void rightShouldReturnEmptyStringForNullInput() {
        assertThat(right(null, 0), is(equalTo("")));
        assertThat(right(null, 10), is(equalTo("")));
        assertThat(right(null, -10), is(equalTo("")));
    }

    @Test
    public void rightShouldReturnSubstringOfInputStringFromTheEnd() {
        assertThat(right("abcdef", 0), is(equalTo("")));
        assertThat(right("abcdef", -5), is(equalTo("")));
        assertThat(right("abcdef", 2), is(equalTo("ef")));
        assertThat(right("abcdef", 30), is(equalTo("abcdef")));
    }

    @Test
    public void lengthShouldReturnZeroForNullString() {
        assertThat(length(null), is(equalTo(0)));
    }

    @Test
    public void lengthShouldReturnLengthOfInputString() {
        assertThat(length(""), is(equalTo(0)));
        assertThat(length("abc"), is(equalTo(3)));
        assertThat(length("abcdef"), is(equalTo(6)));
    }

    @Test
    public void splitShouldReturnEmptyStringForNullInputs() {
        assertThat(split(null, null, 0), is(equalTo("")));
        assertThat(split("", null, 0), is(equalTo("")));
        assertThat(split(null, "", 0), is(equalTo("")));
    }

    @Test
    public void splitShouldReturnTheNthFieldOfTheSplitedInputString() {
        assertThat(split("a,b,c", ",", 0), is(equalTo("a")));
        assertThat(split("a,b,c", ",", 2), is(equalTo("c")));
        assertThat(split("a,;b,;c", ",;", 1), is(equalTo("b")));
    }

    @Test
    public void splitShouldReturnEmptyStringIfFieldIndexIsOutOfBounds() {
        assertThat(split("a,b,c", ",", 10), is(equalTo("")));
        assertThat(split("a,b,c", ",", -1), is(equalTo("")));
    }

    @Test
    public void substringShouldReturnEmptyStringForNullInput() {
        assertThat(substring(null, 0, 0), is(equalTo("")));
        assertThat(substring(null, 0, 10), is(equalTo("")));
    }

    @Test
    public void substringShouldReturnSubstringFromStartIndexToEndIndexOfInputString() {
        assertThat(substring("abcdef", 0, 0), is(equalTo("")));
        assertThat(substring("abcdef", 0, 1), is(equalTo("a")));
        assertThat(substring("abcdef", -10, 1), is(equalTo("a")));
        assertThat(substring("abcdef", 2, 4), is(equalTo("cd")));
        assertThat(substring("abcdef", 2, 10), is(equalTo("cdef")));
    }
}
