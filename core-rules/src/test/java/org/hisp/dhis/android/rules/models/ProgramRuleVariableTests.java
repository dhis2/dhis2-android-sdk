package org.hisp.dhis.android.rules.models;

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
public class ProgramRuleVariableTests {

    @Mock
    private Option option;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void forDataElementShouldPropagatePropertiesCorrectly() {
        RuleVariable ruleVariable = RuleVariable.forDataElement(
                "test_variable_name", "test_program_stage", "test_data_element", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, Arrays.asList(option)
        );

        assertThat(ruleVariable.name()).isEqualTo("test_variable_name");
        assertThat(ruleVariable.programStage()).isEqualTo("test_program_stage");
        assertThat(ruleVariable.dataElement()).isEqualTo("test_data_element");
        assertThat(ruleVariable.dataElementValueType()).isEqualTo(ValueType.TEXT);
        assertThat(ruleVariable.trackedEntityAttribute()).isNull();
        assertThat(ruleVariable.trackedEntityAttributeType()).isNull();
        assertThat(ruleVariable.useCodeForOptionSet()).isEqualTo(false);
        assertThat(ruleVariable.sourceType())
                .isEqualTo(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        assertThat(ruleVariable.options().get(0)).isEqualTo(option);
    }

    @Test
    public void optionsShouldBeImmutableWhenCallingForDataElement() {
        List<Option> options = new ArrayList<>();
        options.add(option);

        RuleVariable ruleVariable = RuleVariable.forDataElement(
                "test_variable_name", "test_program_stage", "test_data_element", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, options
        );

        options.clear();

        assertThat(ruleVariable.options().size()).isEqualTo(1);
        assertThat(ruleVariable.options().get(0)).isEqualTo(option);

        try {
            ruleVariable.options().clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void forAttributeShouldPropagatePropertiesCorrectly() {
        RuleVariable ruleVariable = RuleVariable.forAttribute(
                "test_variable_name", "test_program_stage", "test_attribute", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, Arrays.asList(option)
        );

        assertThat(ruleVariable.name()).isEqualTo("test_variable_name");
        assertThat(ruleVariable.programStage()).isEqualTo("test_program_stage");
        assertThat(ruleVariable.dataElement()).isNull();
        assertThat(ruleVariable.dataElementValueType()).isNull();
        assertThat(ruleVariable.trackedEntityAttribute()).isEqualTo("test_attribute");
        assertThat(ruleVariable.trackedEntityAttributeType()).isEqualTo(ValueType.TEXT);
        assertThat(ruleVariable.useCodeForOptionSet()).isEqualTo(false);
        assertThat(ruleVariable.sourceType())
                .isEqualTo(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        assertThat(ruleVariable.options().get(0)).isEqualTo(option);
    }

    @Test
    public void optionsShouldBeImmutableWhenCallingForAttribute() {
        List<Option> options = new ArrayList<>();
        options.add(option);

        RuleVariable ruleVariable = RuleVariable.forAttribute(
                "test_variable_name", "test_program_stage", "test_attribute", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, options
        );

        options.clear();

        assertThat(ruleVariable.options().size()).isEqualTo(1);
        assertThat(ruleVariable.options().get(0)).isEqualTo(option);

        try {
            ruleVariable.options().clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }
}
