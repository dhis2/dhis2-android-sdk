package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleAttributeValue;
import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.hisp.dhis.android.rules.models.RuleVariable;
import org.hisp.dhis.android.rules.models.RuleVariableCurrentEvent;
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
public class RuleVariableValueMapFactoryTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Mock
    private RuleEvent ruleEvent;

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        Date eventDate = dateFormat.parse("1994-02-03");
        when(ruleEvent.eventDate()).thenReturn(eventDate);
    }

    @Test
    public void buildShouldReturnImmutableMap() {
        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                new ArrayList<RuleVariable>(),
                new ArrayList<RuleAttributeValue>(),
                new ArrayList<RuleEvent>()
        );

        try {
            ruleVariableValueMapFactory.build(ruleEvent).clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void buildShouldReturnMapWithEnvVariables() throws ParseException {
        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                new ArrayList<RuleVariable>(),
                new ArrayList<RuleAttributeValue>(),
                new ArrayList<RuleEvent>()
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(ruleEvent);

        assertThat(valueMap.get("event_date").value()).isEqualTo("1994-02-03");
        assertThat(valueMap.get("current_date").value()).isEqualTo(dateFormat.format(new Date()));
    }

    @Test
    public void currentEventVariableShouldBeHandledCorrectly() {
        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date eventDate = new Date();

        // values from context events should be ignored
        RuleEvent contextEventOne = RuleEvent.create("test_context_event_one", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_one", "test_context_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_one", "test_context_value_two")
                ));
        RuleEvent contextEventTwo = RuleEvent.create("test_context_event_two", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_one", "test_context_value_three"),
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_one", "test_context_value_four")
                ));
        // values from current event should be propagated to the variable values
        RuleEvent currentEvent = RuleEvent.create("test_event_uid", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage", "test_dataelement_two", "test_value_two")
                ));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo),
                new ArrayList<RuleAttributeValue>(), Arrays.asList(contextEventOne, contextEventTwo)
        );

        // here we will expect correct values to be returned
        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentEvent);

        // 2 variables defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(4);
        assertThat(valueMap.get("event_date").value()).isEqualTo(dateFormat.format(eventDate));
        assertThat(valueMap.get("current_date")).isNotNull();

        // first variable
        assertThat(valueMap.get("test_variable_one").value()).isEqualTo("test_value_one");
        assertThat(valueMap.get("test_variable_one").valueType()).isEqualTo(RuleValueType.TEXT);
        assertThat(valueMap.get("test_variable_one").candidates().size()).isEqualTo(1);
        assertThat(valueMap.get("test_variable_one").candidates().get(0)).isEqualTo("test_value_one");

        // second variable
        assertThat(valueMap.get("test_variable_two").value()).isEqualTo("test_value_two");
        assertThat(valueMap.get("test_variable_two").valueType()).isEqualTo(RuleValueType.TEXT);
        assertThat(valueMap.get("test_variable_two").candidates().size()).isEqualTo(1);
        assertThat(valueMap.get("test_variable_two").candidates().get(0)).isEqualTo("test_value_two");
    }
//
//    @Test
//    public void deNewestEventProgramShouldRespectDatesOfExistingEvents() throws ParseException {
//        RuleVariable ruleVariableOne = RuleVariable.forDataElement(
//                "test_variable_one", "test_program_stage", "test_dataelement_one", RuleValueType.TEXT,
//                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
//        RuleVariable ruleVariableTwo = RuleVariable.forDataElement(
//                "test_variable_two", "test_program_stage", "test_dataelement_two", RuleValueType.TEXT,
//                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
//
//        RuleEvent oldestRuleEvent = RuleEvent.create("test_event_uid_oldest", EventStatus.COMPLETED,
//                "test_program_stage", dateFormat.parse("2013-01-01"), dateFormat.parse("2013-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_oldest"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_oldest")
//                ));
//
//        RuleEvent newestRuleEvent = RuleEvent.create("test_event_uid_newest", EventStatus.ACTIVE,
//                "test_program_stage", dateFormat.parse("2017-01-01"), dateFormat.parse("2017-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_newest"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_newest")
//                ));
//
//        RuleEvent currentRuleEvent = RuleEvent.create("test_event_uid_current", EventStatus.ACTIVE,
//                "test_program_stage", dateFormat.parse("2015-01-01"), dateFormat.parse("2015-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_current"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_current")
//                ));
//
//        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
//                Arrays.asList(ruleVariableOne, ruleVariableTwo),
//                new ArrayList<RuleAttributeValue>(),
//                Arrays.asList(oldestRuleEvent, newestRuleEvent)
//        );
//
//        Map<String, ProgramRuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentRuleEvent);
//
//        // 2 values are coming from ruleVariableOne and ruleVariableTwo,
//        // while 2 others from environment variables
//        assertThat(valueMap.size()).isEqualTo(4);
//
//        ProgramRuleVariableValue variableValueOne = valueMap.get("test_variable_one");
//        ProgramRuleVariableValue variableValueTwo = valueMap.get("test_variable_two");
//
//        // variable one
//        assertThat(variableValueOne.value()).isEqualTo("test_value_one_newest");
//        assertThat(variableValueOne.hasValue()).isEqualTo(true);
//        assertThat(variableValueOne.valueType()).isEqualTo(RuleValueType.TEXT);
//        assertThat(variableValueOne.valueCandidates().size()).isEqualTo(3);
//        assertThat(variableValueOne.valueCandidates().get(0)).isEqualTo("test_value_one_newest");
//        assertThat(variableValueOne.valueCandidates().get(1)).isEqualTo("test_value_one_current");
//        assertThat(variableValueOne.valueCandidates().get(2)).isEqualTo("test_value_one_oldest");
//
//        // variable two
//        assertThat(variableValueTwo.value()).isEqualTo("test_value_two_newest");
//        assertThat(variableValueTwo.hasValue()).isEqualTo(true);
//        assertThat(variableValueTwo.valueType()).isEqualTo(RuleValueType.TEXT);
//        assertThat(variableValueTwo.valueCandidates().size()).isEqualTo(3);
//        assertThat(variableValueTwo.valueCandidates().get(0)).isEqualTo("test_value_two_newest");
//        assertThat(variableValueTwo.valueCandidates().get(1)).isEqualTo("test_value_two_current");
//        assertThat(variableValueTwo.valueCandidates().get(2)).isEqualTo("test_value_two_oldest");
//    }
//
//    @Test
//    public void deNewestEventProgramShouldReturnValuesFromCurrentEvent() throws ParseException {
//        RuleVariable ruleVariableOne = RuleVariable.forDataElement(
//                "test_variable_one", "test_program_stage", "test_dataelement_one", RuleValueType.TEXT,
//                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
//        RuleVariable ruleVariableTwo = RuleVariable.forDataElement(
//                "test_variable_two", "test_program_stage", "test_dataelement_two", RuleValueType.TEXT,
//                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM, new ArrayList<Option>());
//
//        RuleEvent firstRuleEvent = RuleEvent.create("test_event_uid_oldest", EventStatus.COMPLETED,
//                "test_program_stage", dateFormat.parse("2013-01-01"), dateFormat.parse("2013-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_first"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_first")
//                ));
//
//        RuleEvent secondRuleEvent = RuleEvent.create("test_event_uid_newest", EventStatus.ACTIVE,
//                "test_program_stage", dateFormat.parse("2014-01-01"), dateFormat.parse("2014-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_second"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_second")
//                ));
//
//        RuleEvent currentRuleEvent = RuleEvent.create("test_event_uid_current", EventStatus.ACTIVE,
//                "test_program_stage", dateFormat.parse("2016-01-01"), dateFormat.parse("2016-01-01"),
//                Arrays.asList(
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_one", "test_value_one_current"),
//                        RuleDataValue.create("test_program_stage",
//                                "test_dataelement_two", "test_value_two_current")
//                ));
//
//        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
//                Arrays.asList(ruleVariableOne, ruleVariableTwo),
//                new ArrayList<RuleAttributeValue>(),
//                Arrays.asList(firstRuleEvent, secondRuleEvent)
//        );
//
//        Map<String, ProgramRuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentRuleEvent);
//
//        // 2 values are coming from ruleVariableOne and ruleVariableTwo,
//        // while 2 others from environment variables
//        assertThat(valueMap.size()).isEqualTo(4);
//
//        ProgramRuleVariableValue variableValueOne = valueMap.get("test_variable_one");
//        ProgramRuleVariableValue variableValueTwo = valueMap.get("test_variable_two");
//
//        // variable one
//        assertThat(variableValueOne.value()).isEqualTo("test_value_one_current");
//        assertThat(variableValueOne.hasValue()).isEqualTo(true);
//        assertThat(variableValueOne.valueType()).isEqualTo(RuleValueType.TEXT);
//        assertThat(variableValueOne.valueCandidates().size()).isEqualTo(3);
//        assertThat(variableValueOne.valueCandidates().get(0)).isEqualTo("test_value_one_current");
//        assertThat(variableValueOne.valueCandidates().get(1)).isEqualTo("test_value_one_second");
//        assertThat(variableValueOne.valueCandidates().get(2)).isEqualTo("test_value_one_first");
//
//        // variable two
//        assertThat(variableValueTwo.value()).isEqualTo("test_value_two_current");
//        assertThat(variableValueTwo.hasValue()).isEqualTo(true);
//        assertThat(variableValueTwo.valueType()).isEqualTo(RuleValueType.TEXT);
//        assertThat(variableValueTwo.valueCandidates().size()).isEqualTo(3);
//        assertThat(variableValueTwo.valueCandidates().get(0)).isEqualTo("test_value_two_current");
//        assertThat(variableValueTwo.valueCandidates().get(1)).isEqualTo("test_value_two_second");
//        assertThat(variableValueTwo.valueCandidates().get(2)).isEqualTo("test_value_two_first");
//    }
//
//    @Test
//    public void deNewestEventStageShouldRespectDatesOfExistingEvents() throws ParseException {
//        RuleVariable ruleVariable = RuleVariable.forDataElement(
//                "test_variable", "test_program_stage_one", "test_dataelement", RuleValueType.TEXT,
//                false, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE,
//                new ArrayList<Option>());
//
//        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_one", EventStatus.ACTIVE,
//                "test_program_stage_one", dateFormat.parse("2014-02-03"),
//                dateFormat.parse("2014-02-03"), Arrays.asList(
//                        RuleDataValue.create("test_program_stage_one",
//                                "test_dataelement", "test_value_one")
//                ));
//        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_two", EventStatus.ACTIVE,
//                "test_program_stage_two", dateFormat.parse("2014-03-03"),
//                dateFormat.parse("2014-03-03"), Arrays.asList(
//                        RuleDataValue.create("test_program_stage_two",
//                                "test_dataelement", "test_value_two")
//                ));
//        RuleEvent ruleEventThree = RuleEvent.create("test_event_uid_three", EventStatus.ACTIVE,
//                "test_program_stage_two", dateFormat.parse("2015-02-03"),
//                dateFormat.parse("2015-02-03"), Arrays.asList(
//                        RuleDataValue.create("test_program_stage_two",
//                                "test_dataelement", "test_value_three")
//                ));
//        RuleEvent currentRuleEvent = RuleEvent.create("test_event_uid_current", EventStatus.ACTIVE,
//                "test_program_stage_one", dateFormat.parse("2011-02-03"),
//                dateFormat.parse("2011-02-03"), Arrays.asList(
//                        RuleDataValue.create("test_program_stage_one",
//                                "test_dataelement", "test_value_current")
//                ));
//
//        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(Arrays.asList(ruleVariable),
//                new ArrayList<RuleAttributeValue>(), Arrays.asList(
//                ruleEventOne, ruleEventTwo, ruleEventThree
//        ));
//
//        Map<String, ProgramRuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentRuleEvent);
//
//        // 1 value is coming from ruleVariable, while 2 others from environment variables
//        assertThat(valueMap.size()).isEqualTo(3);
//
//        ProgramRuleVariableValue variableValue = valueMap.get("test_variable");
//        assertThat(variableValue.value()).isEqualTo("test_value_one");
//        assertThat(variableValue.hasValue()).isEqualTo(true);
//        assertThat(variableValue.valueType()).isEqualTo(RuleValueType.TEXT);
//
//        // check candidates as well
//        // ToDo: candidates currently are not filtered out
////        assertThat(variableValue.valueCandidates().size()).isEqualTo(2);
////        assertThat(variableValue.valueCandidates().get(0)).isEqualTo("test_value_one");
////        assertThat(variableValue.valueCandidates().get(1)).isEqualTo("test_value_current");
//    }
}
