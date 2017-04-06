package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ProgramRuleVariableValueTests {

    @Mock
    private TrackedEntityDataValue dataValue;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void valuesShouldBePropagatedCorrectly() {
        ProgramRuleVariableValue variableValue = ProgramRuleVariableValue.create(
                "test_value", Arrays.asList("test_candidate_one", "test_candidate_two"), ValueType.TEXT, true
        );

        assertThat(variableValue.value()).isEqualTo("test_value");
        assertThat(variableValue.valueCandidates().get(0)).isEqualTo("test_candidate_one");
        assertThat(variableValue.valueCandidates().get(1)).isEqualTo("test_candidate_two");
        assertThat(variableValue.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(variableValue.hasValue()).isEqualTo(true);
    }

    @Test
    public void createShouldThrowOnNullDataValue() {
        try {
            ProgramRuleVariableValue.create(null, Arrays.<String>asList(), ValueType.TEXT, false);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullValueType() {
        try {
            ProgramRuleVariableValue.create("test_value", Arrays.<String>asList(), null, false);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullCandidateList() {
        try {
            ProgramRuleVariableValue.create("test_value", null, ValueType.TEXT, false);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullHasValueFlag() {
        try {
            ProgramRuleVariableValue.create("test_value", Arrays.<String>asList(), ValueType.TEXT, null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void valueCandidatesListShouldBeImmutable() {
        List<String> valueCandidates = new ArrayList<>();
        valueCandidates.add("test_candidate_one");
        valueCandidates.add("test_candidate_two");

        ProgramRuleVariableValue variableValue = ProgramRuleVariableValue.create(
                "test_value", valueCandidates, ValueType.TEXT, true);

        valueCandidates.clear();

        assertThat(variableValue.valueCandidates().size()).isEqualTo(2);
        assertThat(variableValue.valueCandidates().get(0)).isEqualTo("test_candidate_one");
        assertThat(variableValue.valueCandidates().get(1)).isEqualTo("test_candidate_two");

        try {
            variableValue.valueCandidates().add("test_candidate_three");
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void valueCandidatesListShouldBeImmutableWhenEmpty() {
        ProgramRuleVariableValue variableValue = ProgramRuleVariableValue.create(
                "test_value", ValueType.TEXT, true);

        try {
            variableValue.valueCandidates().add("test_candidate_three");
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void defaultValueShouldCorrespondToValueType() {
        assertThat(ProgramRuleVariableValue.create(ValueType.TEXT).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.TIME).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.EMAIL).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.LETTER).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.USERNAME).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.LONG_TEXT).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.COORDINATE).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.PHONE_NUMBER).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.FILE_RESOURCE).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.DATE).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.DATETIME).value()).isEqualTo("''");
        assertThat(ProgramRuleVariableValue.create(ValueType.NUMBER).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.UNIT_INTERVAL).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.PERCENTAGE).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.INTEGER).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.INTEGER_POSITIVE).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.INTEGER_NEGATIVE).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.INTEGER_ZERO_OR_POSITIVE).value()).isEqualTo("0");
        assertThat(ProgramRuleVariableValue.create(ValueType.BOOLEAN).value()).isEqualTo("false");
        assertThat(ProgramRuleVariableValue.create(ValueType.TRUE_ONLY).value()).isEqualTo("false");
    }

    @Test
    public void assignValueShouldInstantiateNewObjectCorrectly() {
        ProgramRuleVariableValue variableValue = ProgramRuleVariableValue.create(ValueType.TEXT);

        assertThat(variableValue.value()).isEqualTo("''");
        assertThat(variableValue.hasValue()).isEqualTo(false);
        assertThat(variableValue.valueType()).isEqualTo(ValueType.TEXT);

        ProgramRuleVariableValue modifiedValue = variableValue.assignValue("test_value");
        assertThat(modifiedValue.value()).isEqualTo("test_value");
        assertThat(modifiedValue.hasValue()).isEqualTo(true);
        assertThat(modifiedValue.valueType()).isEqualTo(ValueType.TEXT);
    }
}
