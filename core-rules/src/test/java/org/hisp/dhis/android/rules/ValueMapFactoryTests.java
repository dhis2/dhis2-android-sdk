package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Event;
import org.hisp.dhis.android.rules.models.EventStatus;
import org.hisp.dhis.android.rules.models.Option;
import org.hisp.dhis.android.rules.models.ProgramRuleVariable;
import org.hisp.dhis.android.rules.models.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.rules.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.rules.models.TrackedEntityDataValue;
import org.hisp.dhis.android.rules.models.ValueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ValueMapFactoryTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Mock
    private Event event;

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        Date eventDate = dateFormat.parse("1994-02-03");
        when(event.eventDate()).thenReturn(eventDate);
    }

    @Test
    public void buildShouldReturnImmutableMap() {
        ValueMapFactory valueMapFactory = new ValueMapFactory(
                new ArrayList<ProgramRuleVariable>(),
                new ArrayList<TrackedEntityAttributeValue>(),
                new ArrayList<Event>()
        );

        try {
            valueMapFactory.build(event).clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void buildShouldReturnMapWithEnvVariables() throws ParseException {
        ValueMapFactory valueMapFactory = new ValueMapFactory(
                new ArrayList<ProgramRuleVariable>(),
                new ArrayList<TrackedEntityAttributeValue>(),
                new ArrayList<Event>()
        );

        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(event);

        assertThat(valueMap.get("event_date").hasValue()).isTrue();
        assertThat(valueMap.get("event_date").value()).isEqualTo("1994-02-03");
        assertThat(valueMap.get("current_date").hasValue()).isTrue();
        assertThat(valueMap.get("current_date").value()).isEqualTo(dateFormat.format(new Date()));
    }

    @Test
    public void dataElementCurrentEventSourceTypeShouldBeHandledCorrectly() {
        ProgramRuleVariable ruleVariableOne = ProgramRuleVariable.forDataElement(
                "test_variable_one", "test_program_stage", "test_dataelement_one", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, new ArrayList<Option>());
        ProgramRuleVariable ruleVariableTwo = ProgramRuleVariable.forDataElement(
                "test_variable_two", "test_program_stage", "test_dataelement_two", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, new ArrayList<Option>());

        Event currentEvent = Event.create("test_event_uid", EventStatus.ACTIVE,
                "test_program_stage", new Date(), new Date(), Arrays.asList(
                        TrackedEntityDataValue.create(event, "test_dataelement_one", "test_value_one"),
                        TrackedEntityDataValue.create(event, "test_dataelement_two", "test_value_two")
                ));

        ValueMapFactory valueMapFactory = new ValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo),
                new ArrayList<TrackedEntityAttributeValue>(), new ArrayList<Event>()
        );

        // here we will expect correct values to be returned
        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(currentEvent);

        // 2 values are coming from ruleVariableOne and ruleVariableTwo,
        // while 2 others from environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        // first variable
        assertThat(valueMap.get("test_variable_one").value()).isEqualTo("test_value_one");
        assertThat(valueMap.get("test_variable_one").hasValue()).isEqualTo(true);
        assertThat(valueMap.get("test_variable_one").valueType()).isEqualTo(ValueType.TEXT);

        // second variable
        assertThat(valueMap.get("test_variable_two").value()).isEqualTo("test_value_two");
        assertThat(valueMap.get("test_variable_two").hasValue()).isEqualTo(true);
        assertThat(valueMap.get("test_variable_two").valueType()).isEqualTo(ValueType.TEXT);
    }

    @Test
    public void dataElementNewestEventSourceTypeShouldBeHandledCorrectly() {
        ProgramRuleVariable ruleVariableOne = ProgramRuleVariable.forDataElement(
                "test_variable_one", "test_program_stage", "test_dataelement_one", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
        ProgramRuleVariable ruleVariableTwo = ProgramRuleVariable.forDataElement(
                "test_variable_two", "test_program_stage", "test_dataelement_two", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());

        Event currentEvent = Event.create("test_event_uid", EventStatus.ACTIVE,
                "test_program_stage", new Date(), new Date(), Arrays.asList(
                        TrackedEntityDataValue.create(event, "test_dataelement_one", "test_value_one"),
                        TrackedEntityDataValue.create(event, "test_dataelement_two", "test_value_two")
                ));

        ValueMapFactory valueMapFactory = new ValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo),
                new ArrayList<TrackedEntityAttributeValue>(),
                new ArrayList<Event>());

        //
        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(currentEvent);
    }
}
