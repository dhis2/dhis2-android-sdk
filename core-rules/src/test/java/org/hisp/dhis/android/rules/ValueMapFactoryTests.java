package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Event;
import org.hisp.dhis.android.rules.models.EventStatus;
import org.hisp.dhis.android.rules.models.Option;
import org.hisp.dhis.android.rules.models.variables.RuleVariable;
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
                new ArrayList<RuleVariable>(),
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
                new ArrayList<RuleVariable>(),
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
    public void deCurrentEventSourceTypeShouldBeHandledCorrectly() {
        RuleVariable ruleVariableOne = RuleVariable.forDataElement(
                "test_variable_one", "test_program_stage", "test_dataelement_one", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, new ArrayList<Option>());
        RuleVariable ruleVariableTwo = RuleVariable.forDataElement(
                "test_variable_two", "test_program_stage", "test_dataelement_two", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT, new ArrayList<Option>());

        Event currentEvent = Event.create("test_event_uid", EventStatus.ACTIVE,
                "test_program_stage", new Date(), new Date(), Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage", "test_dataelement_one", "test_value_one"),
                        TrackedEntityDataValue.create("test_program_stage", "test_dataelement_two", "test_value_two")
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
    public void deNewestEventProgramShouldRespectDatesOfExistingEvents() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariable.forDataElement(
                "test_variable_one", "test_program_stage", "test_dataelement_one", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
        RuleVariable ruleVariableTwo = RuleVariable.forDataElement(
                "test_variable_two", "test_program_stage", "test_dataelement_two", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());

        Event oldestEvent = Event.create("test_event_uid_oldest", EventStatus.COMPLETED,
                "test_program_stage", dateFormat.parse("2013-01-01"), dateFormat.parse("2013-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_oldest"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_oldest")
                ));

        Event newestEvent = Event.create("test_event_uid_newest", EventStatus.ACTIVE,
                "test_program_stage", dateFormat.parse("2017-01-01"), dateFormat.parse("2017-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_newest"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_newest")
                ));

        Event currentEvent = Event.create("test_event_uid_current", EventStatus.ACTIVE,
                "test_program_stage", dateFormat.parse("2015-01-01"), dateFormat.parse("2015-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_current"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_current")
                ));

        ValueMapFactory valueMapFactory = new ValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo),
                new ArrayList<TrackedEntityAttributeValue>(),
                Arrays.asList(oldestEvent, newestEvent)
        );

        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(currentEvent);

        // 2 values are coming from ruleVariableOne and ruleVariableTwo,
        // while 2 others from environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        ProgramRuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        ProgramRuleVariableValue variableValueTwo = valueMap.get("test_variable_two");

        // variable one
        assertThat(variableValueOne.value()).isEqualTo("test_value_one_newest");
        assertThat(variableValueOne.hasValue()).isEqualTo(true);
        assertThat(variableValueOne.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(variableValueOne.valueCandidates().size()).isEqualTo(3);
        assertThat(variableValueOne.valueCandidates().get(0)).isEqualTo("test_value_one_newest");
        assertThat(variableValueOne.valueCandidates().get(1)).isEqualTo("test_value_one_current");
        assertThat(variableValueOne.valueCandidates().get(2)).isEqualTo("test_value_one_oldest");

        // variable two
        assertThat(variableValueTwo.value()).isEqualTo("test_value_two_newest");
        assertThat(variableValueTwo.hasValue()).isEqualTo(true);
        assertThat(variableValueTwo.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(variableValueTwo.valueCandidates().size()).isEqualTo(3);
        assertThat(variableValueTwo.valueCandidates().get(0)).isEqualTo("test_value_two_newest");
        assertThat(variableValueTwo.valueCandidates().get(1)).isEqualTo("test_value_two_current");
        assertThat(variableValueTwo.valueCandidates().get(2)).isEqualTo("test_value_two_oldest");
    }

    @Test
    public void deNewestEventProgramShouldReturnValuesFromCurrentEvent() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariable.forDataElement(
                "test_variable_one", "test_program_stage", "test_dataelement_one", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
        RuleVariable ruleVariableTwo = RuleVariable.forDataElement(
                "test_variable_two", "test_program_stage", "test_dataelement_two", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());

        Event firstEvent = Event.create("test_event_uid_oldest", EventStatus.COMPLETED,
                "test_program_stage", dateFormat.parse("2013-01-01"), dateFormat.parse("2013-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_first"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_first")
                ));

        Event secondEvent = Event.create("test_event_uid_newest", EventStatus.ACTIVE,
                "test_program_stage", dateFormat.parse("2014-01-01"), dateFormat.parse("2014-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_second"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_second")
                ));

        Event currentEvent = Event.create("test_event_uid_current", EventStatus.ACTIVE,
                "test_program_stage", dateFormat.parse("2016-01-01"), dateFormat.parse("2016-01-01"),
                Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_one", "test_value_one_current"),
                        TrackedEntityDataValue.create("test_program_stage",
                                "test_dataelement_two", "test_value_two_current")
                ));

        ValueMapFactory valueMapFactory = new ValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo),
                new ArrayList<TrackedEntityAttributeValue>(),
                Arrays.asList(firstEvent, secondEvent)
        );

        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(currentEvent);

        // 2 values are coming from ruleVariableOne and ruleVariableTwo,
        // while 2 others from environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        ProgramRuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        ProgramRuleVariableValue variableValueTwo = valueMap.get("test_variable_two");

        // variable one
        assertThat(variableValueOne.value()).isEqualTo("test_value_one_current");
        assertThat(variableValueOne.hasValue()).isEqualTo(true);
        assertThat(variableValueOne.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(variableValueOne.valueCandidates().size()).isEqualTo(3);
        assertThat(variableValueOne.valueCandidates().get(0)).isEqualTo("test_value_one_current");
        assertThat(variableValueOne.valueCandidates().get(1)).isEqualTo("test_value_one_second");
        assertThat(variableValueOne.valueCandidates().get(2)).isEqualTo("test_value_one_first");

        // variable two
        assertThat(variableValueTwo.value()).isEqualTo("test_value_two_current");
        assertThat(variableValueTwo.hasValue()).isEqualTo(true);
        assertThat(variableValueTwo.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(variableValueTwo.valueCandidates().size()).isEqualTo(3);
        assertThat(variableValueTwo.valueCandidates().get(0)).isEqualTo("test_value_two_current");
        assertThat(variableValueTwo.valueCandidates().get(1)).isEqualTo("test_value_two_second");
        assertThat(variableValueTwo.valueCandidates().get(2)).isEqualTo("test_value_two_first");
    }

    // ToDo: implement different models for different program rule variable types

    @Test
    public void deNewestEventStageShouldRespectDatesOfExistingEvents() throws ParseException {
        RuleVariable ruleVariable = RuleVariable.forDataElement(
                "test_variable", "test_program_stage_one", "test_dataelement", ValueType.TEXT,
                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE,
                new ArrayList<Option>());

        Event eventOne = Event.create("test_event_uid_one", EventStatus.ACTIVE,
                "test_program_stage_one", dateFormat.parse("2014-02-03"),
                dateFormat.parse("2014-02-03"), Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage_one",
                                "test_dataelement", "test_value_one")
                ));
        Event eventTwo = Event.create("test_event_uid_two", EventStatus.ACTIVE,
                "test_program_stage_two", dateFormat.parse("2014-03-03"),
                dateFormat.parse("2014-03-03"), Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage_two",
                                "test_dataelement", "test_value_two")
                ));
        Event eventThree = Event.create("test_event_uid_three", EventStatus.ACTIVE,
                "test_program_stage_two", dateFormat.parse("2015-02-03"),
                dateFormat.parse("2015-02-03"), Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage_two",
                                "test_dataelement", "test_value_three")
                ));
        Event currentEvent = Event.create("test_event_uid_current", EventStatus.ACTIVE,
                "test_program_stage_one", dateFormat.parse("2011-02-03"),
                dateFormat.parse("2011-02-03"), Arrays.asList(
                        TrackedEntityDataValue.create("test_program_stage_one",
                                "test_dataelement", "test_value_current")
                ));

        ValueMapFactory valueMapFactory = new ValueMapFactory(Arrays.asList(ruleVariable),
                new ArrayList<TrackedEntityAttributeValue>(), Arrays.asList(
                        eventOne, eventTwo, eventThree
        ));

        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(currentEvent);

        // 1 value is coming from ruleVariable, while 2 others from environment variables
        assertThat(valueMap.size()).isEqualTo(3);

        ProgramRuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isEqualTo("test_value_one");
        assertThat(variableValue.hasValue()).isEqualTo(true);
        assertThat(variableValue.valueType()).isEqualTo(ValueType.TEXT);

        // check candidates as well
        // ToDo: candidates currently are not filtered out
//        assertThat(variableValue.valueCandidates().size()).isEqualTo(2);
//        assertThat(variableValue.valueCandidates().get(0)).isEqualTo("test_value_one");
//        assertThat(variableValue.valueCandidates().get(1)).isEqualTo("test_value_current");
    }
}
