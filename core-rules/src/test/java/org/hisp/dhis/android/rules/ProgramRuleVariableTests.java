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
public class ProgramRuleVariableTests {

    @Mock
    private Option option;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void forDataElementShouldPropagatePropertiesCorrectly() {
        ProgramRuleVariable programRuleVariable = ProgramRuleVariable.forDataElement(
                "test_variable_name", "test_program_stage", "test_data_element", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, Arrays.asList(option)
        );

        assertThat(programRuleVariable.name()).isEqualTo("test_variable_name");
        assertThat(programRuleVariable.programStage()).isEqualTo("test_program_stage");
        assertThat(programRuleVariable.dataElement()).isEqualTo("test_data_element");
        assertThat(programRuleVariable.dataElementValueType()).isEqualTo(ValueType.TEXT);
        assertThat(programRuleVariable.trackedEntityAttribute()).isNull();
        assertThat(programRuleVariable.trackedEntityAttributeType()).isNull();
        assertThat(programRuleVariable.useCodeForOptionSet()).isEqualTo(false);
        assertThat(programRuleVariable.sourceType())
                .isEqualTo(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        assertThat(programRuleVariable.options().get(0)).isEqualTo(option);
    }

    @Test
    public void optionsShouldBeImmutableWhenCallingForDataElement() {
        List<Option> options = new ArrayList<>();
        options.add(option);

        ProgramRuleVariable programRuleVariable = ProgramRuleVariable.forDataElement(
                "test_variable_name", "test_program_stage", "test_data_element", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, options
        );

        options.clear();

        assertThat(programRuleVariable.options().size()).isEqualTo(1);
        assertThat(programRuleVariable.options().get(0)).isEqualTo(option);

        try {
            programRuleVariable.options().clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void forAttributeShouldPropagatePropertiesCorrectly() {
        ProgramRuleVariable programRuleVariable = ProgramRuleVariable.forAttribute(
                "test_variable_name", "test_program_stage", "test_attribute", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, Arrays.asList(option)
        );

        assertThat(programRuleVariable.name()).isEqualTo("test_variable_name");
        assertThat(programRuleVariable.programStage()).isEqualTo("test_program_stage");
        assertThat(programRuleVariable.dataElement()).isNull();
        assertThat(programRuleVariable.dataElementValueType()).isNull();
        assertThat(programRuleVariable.trackedEntityAttribute()).isEqualTo("test_attribute");
        assertThat(programRuleVariable.trackedEntityAttributeType()).isEqualTo(ValueType.TEXT);
        assertThat(programRuleVariable.useCodeForOptionSet()).isEqualTo(false);
        assertThat(programRuleVariable.sourceType())
                .isEqualTo(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        assertThat(programRuleVariable.options().get(0)).isEqualTo(option);
    }

    @Test
    public void optionsShouldBeImmutableWhenCallingForAttribute() {
        List<Option> options = new ArrayList<>();
        options.add(option);

        ProgramRuleVariable programRuleVariable = ProgramRuleVariable.forAttribute(
                "test_variable_name", "test_program_stage", "test_attribute", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, options
        );

        options.clear();

        assertThat(programRuleVariable.options().size()).isEqualTo(1);
        assertThat(programRuleVariable.options().get(0)).isEqualTo(option);

        try {
            programRuleVariable.options().clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }
}
