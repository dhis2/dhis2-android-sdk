package org.hisp.dhis.android.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleVariableAttributeTests {

    @Test
    public void createShouldThrowOnNullName() {
        try {
            RuleVariableAttribute.create(null, "test_attribute", RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullTrackedEntityAttribute() {
        try {
            RuleVariableAttribute.create("test_variable", null, RuleValueType.TEXT);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldThrowOnNullTrackedEntityAttributeType() {
        try {
            RuleVariableAttribute.create("test_variable", "test_attribute", null);
            fail("NullPointerException is expected, but nothing was thrown");
        } catch (NullPointerException exception) {
            // noop
        }
    }

    @Test
    public void createShouldPropagatePropertiesCorrectly() {
        RuleVariableAttribute ruleVariableAttribute = RuleVariableAttribute.create(
                "test_variable", "test_attribute", RuleValueType.NUMERIC);

        assertThat(ruleVariableAttribute.name()).isEqualTo("test_variable");
        assertThat(ruleVariableAttribute.trackedEntityAttribute()).isEqualTo("test_attribute");
        assertThat(ruleVariableAttribute.trackedEntityAttributeType()).isEqualTo(RuleValueType.NUMERIC);
    }
}
