package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableValueTests {

    @Mock
    private RuleDataValue ruleDataValue;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void valuesShouldBePropagatedCorrectly() {
        RuleVariableValue variableValue = RuleVariableValue.create(
                "test_value", RuleValueType.TEXT, Arrays.asList(
                        "test_value_candidate_one", "test_value_candidate_two"
                )
        );

        assertThat(variableValue.value()).isEqualTo("test_value");
        assertThat(variableValue.valueType()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_candidate_one");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_candidate_two");
    }

    @Test
    public void createShouldThrowOnNullValueType() {
        try {
            RuleVariableValue.create("test_value", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullCandidateList() {
        try {
            RuleVariableValue.create("test_value", RuleValueType.TEXT, null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldReturnValueWithImmutableCandidates() {

    }
}
