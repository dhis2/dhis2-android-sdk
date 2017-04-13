package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleAttributeValue;
import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEnrollment;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.hisp.dhis.android.rules.models.RuleVariable;
import org.hisp.dhis.android.rules.models.RuleVariableAttribute;
import org.hisp.dhis.android.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.android.rules.models.RuleVariablePreviousEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RuleVariableValueMapFactoryTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws Exception {
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
    }

    @Test
    public void buildShouldReturnImmutableMap() throws ParseException {
        RuleEvent ruleEvent = mock(RuleEvent.class);
        when(ruleEvent.eventDate()).thenReturn(dateFormat.parse("1994-02-03"));

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
        RuleEvent ruleEvent = mock(RuleEvent.class);
        when(ruleEvent.eventDate()).thenReturn(dateFormat.parse("1994-02-03"));

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
    public void currentEventVariableShouldContainValuesFromCurrentEvent() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date eventDate = dateFormat.parse("2015-01-01");

        // values from context events should be ignored
        RuleEvent contextEventOne = RuleEvent.create("test_context_event_one", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_two")));
        RuleEvent contextEventTwo = RuleEvent.create("test_context_event_two", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_three"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_four")));
        // values from current event should be propagated to the variable values
        RuleEvent currentEvent = RuleEvent.create("test_event_uid", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two")));

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
        assertThat(valueMap.get("test_variable_one").type()).isEqualTo(RuleValueType.TEXT);
        assertThat(valueMap.get("test_variable_one").candidates().size()).isEqualTo(1);
        assertThat(valueMap.get("test_variable_one").candidates().get(0)).isEqualTo("test_value_one");

        // second variable
        assertThat(valueMap.get("test_variable_two").value()).isEqualTo("test_value_two");
        assertThat(valueMap.get("test_variable_two").type()).isEqualTo(RuleValueType.TEXT);
        assertThat(valueMap.get("test_variable_two").candidates().size()).isEqualTo(1);
        assertThat(valueMap.get("test_variable_two").candidates().get(0)).isEqualTo("test_value_two");
    }

    @Test
    public void newestEventProgramVariableShouldContainValueFromNewestContextEvent() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableNewestEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableNewestEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date oldestEventDate = dateFormat.parse("2013-01-01");
        Date newestEventDate = dateFormat.parse("2017-01-01");
        Date currentEventDate = dateFormat.parse("2015-01-01");

        RuleEvent oldestRuleEvent = RuleEvent.create("test_event_uid_oldest", RuleEvent.Status.ACTIVE,
                "test_program_stage", oldestEventDate, oldestEventDate, Arrays.asList(
                        RuleDataValue.create(oldestEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_oldest"),
                        RuleDataValue.create(oldestEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_oldest")));
        RuleEvent newestRuleEvent = RuleEvent.create("test_event_uid_newest", RuleEvent.Status.ACTIVE,
                "test_program_stage", newestEventDate, newestEventDate, Arrays.asList(
                        RuleDataValue.create(newestEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_newest"),
                        RuleDataValue.create(newestEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_newest")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid_current", RuleEvent.Status.ACTIVE,
                "test_program_stage", currentEventDate, currentEventDate, Arrays.asList(
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_current"),
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_current")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo), new ArrayList<RuleAttributeValue>(),
                Arrays.asList(oldestRuleEvent, newestRuleEvent)
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentEvent);

        // 2 variables defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        RuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        RuleVariableValue variableValueTwo = valueMap.get("test_variable_two");

        // variable one
        assertThat(variableValueOne.value()).isEqualTo("test_value_one_newest");
        assertThat(variableValueOne.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueOne.candidates().size()).isEqualTo(3);
        assertThat(variableValueOne.candidates().get(0)).isEqualTo("test_value_one_newest");
        assertThat(variableValueOne.candidates().get(1)).isEqualTo("test_value_one_current");
        assertThat(variableValueOne.candidates().get(2)).isEqualTo("test_value_one_oldest");

        // variable two
        assertThat(variableValueTwo.value()).isEqualTo("test_value_two_newest");
        assertThat(variableValueTwo.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueTwo.candidates().size()).isEqualTo(3);
        assertThat(variableValueTwo.candidates().get(0)).isEqualTo("test_value_two_newest");
        assertThat(variableValueTwo.candidates().get(1)).isEqualTo("test_value_two_current");
        assertThat(variableValueTwo.candidates().get(2)).isEqualTo("test_value_two_oldest");
    }

    @Test
    public void newestEventProgramVariableShouldReturnValuesFromCurrentEventWhenIfNewest() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableNewestEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableNewestEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date firstEventDate = dateFormat.parse("2013-01-01");
        Date secondEventDate = dateFormat.parse("2014-01-01");
        Date currentEventDate = dateFormat.parse("2015-01-01");

        RuleEvent firstRuleEvent = RuleEvent.create("test_event_uid_one", RuleEvent.Status.ACTIVE,
                "test_program_stage", firstEventDate, firstEventDate, Arrays.asList(
                        RuleDataValue.create(firstEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_first"),
                        RuleDataValue.create(firstEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_first")));
        RuleEvent secondRuleEvent = RuleEvent.create("test_event_uid_two", RuleEvent.Status.ACTIVE,
                "test_program_stage", secondEventDate, secondEventDate, Arrays.asList(
                        RuleDataValue.create(secondEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_second"),
                        RuleDataValue.create(secondEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_second")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid_current", RuleEvent.Status.ACTIVE,
                "test_program_stage", currentEventDate, currentEventDate, Arrays.asList(
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_current"),
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_current")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo), new ArrayList<RuleAttributeValue>(),
                Arrays.asList(firstRuleEvent, secondRuleEvent)
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentEvent);

        // 2 variables defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        RuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        RuleVariableValue variableValueTwo = valueMap.get("test_variable_two");

        // variable one
        assertThat(variableValueOne.value()).isEqualTo("test_value_dataelement_one_current");
        assertThat(variableValueOne.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueOne.candidates().size()).isEqualTo(3);
        assertThat(variableValueOne.candidates().get(0)).isEqualTo("test_value_dataelement_one_current");
        assertThat(variableValueOne.candidates().get(1)).isEqualTo("test_value_dataelement_one_second");
        assertThat(variableValueOne.candidates().get(2)).isEqualTo("test_value_dataelement_one_first");

        // variable two
        assertThat(variableValueTwo.value()).isEqualTo("test_value_dataelement_two_current");
        assertThat(variableValueTwo.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueTwo.candidates().size()).isEqualTo(3);
        assertThat(variableValueTwo.candidates().get(0)).isEqualTo("test_value_dataelement_two_current");
        assertThat(variableValueTwo.candidates().get(1)).isEqualTo("test_value_dataelement_two_second");
        assertThat(variableValueTwo.candidates().get(2)).isEqualTo("test_value_dataelement_two_first");
    }

    @Test
    public void newestEventProgramStageVariableShouldContainValueFromNewestContextEvent() throws ParseException {
        RuleVariable ruleVariable = RuleVariableNewestStageEvent.create("test_variable",
                "test_dataelement", "test_program_stage_one", RuleValueType.TEXT);

        Date dateEventOne = dateFormat.parse("2014-02-03");
        Date dateEventTwo = dateFormat.parse("2014-03-03");
        Date dateEventThree = dateFormat.parse("2015-02-03");
        Date dateEventCurrent = dateFormat.parse("2011-02-03");

        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_one", RuleEvent.Status.ACTIVE,
                "test_program_stage_one", dateEventOne, dateEventOne, Arrays.asList(
                        RuleDataValue.create(dateEventOne, "test_program_stage_one",
                                "test_dataelement", "test_value_one")));
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_two", RuleEvent.Status.ACTIVE,
                "test_program_stage_two", dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));
        RuleEvent ruleEventThree = RuleEvent.create("test_event_uid_three", RuleEvent.Status.ACTIVE,
                "test_program_stage_two", dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_three")));
        RuleEvent ruleEventCurrent = RuleEvent.create("test_event_uid_current", RuleEvent.Status.ACTIVE,
                "test_program_stage_one", dateEventCurrent, dateEventCurrent, Arrays.asList(
                        RuleDataValue.create(dateEventCurrent, "test_program_stage_one",
                                "test_dataelement", "test_value_current")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariable), new ArrayList<RuleAttributeValue>(),
                Arrays.asList(ruleEventOne, ruleEventTwo, ruleEventThree)
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(ruleEventCurrent);

        // 1 variable defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(3);

        RuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isEqualTo("test_value_one");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(2);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_one");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_current");
    }

    @Test
    public void newestEventProgramStageVariableShouldNotContainAnyValues() throws ParseException {
        RuleVariable ruleVariable = RuleVariableNewestStageEvent.create("test_variable",
                "test_dataelement", "test_program_stage_one", RuleValueType.TEXT);

        Date dateEventTwo = dateFormat.parse("2014-03-03");
        Date dateEventThree = dateFormat.parse("2015-02-03");

        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_two", RuleEvent.Status.ACTIVE,
                "test_program_stage_two", dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_one")));
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_three", RuleEvent.Status.ACTIVE,
                "test_program_stage_two", dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariable), new ArrayList<RuleAttributeValue>(),
                Arrays.asList(ruleEventOne, ruleEventTwo)
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(ruleEventTwo);

        // 1 variable defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(3);

        RuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isNull();
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(0);
    }

    @Test
    public void attributeVariableShouldContainValuesFromContextEnrollment() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableAttribute.create("test_variable_one",
                "test_attribute_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableAttribute.create("test_variable_two",
                "test_attribute_two", RuleValueType.TEXT);

        Date eventDate = dateFormat.parse("2015-01-01");
        Date enrollmentDate = dateFormat.parse("2014-03-01");

        // values from ruleEnrollment should end up in variables
        RuleEnrollment ruleEnrollment = RuleEnrollment.create("test_enrollment",
                enrollmentDate, enrollmentDate, RuleEnrollment.Status.ACTIVE, Arrays.asList(
                        RuleAttributeValue.create("test_attribute_one", "test_attribute_value_one"),
                        RuleAttributeValue.create("test_attribute_two", "test_attribute_value_two")));

        // values from context events should be ignored
        RuleEvent contextEvent = RuleEvent.create("test_context_event_one", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_two")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid", RuleEvent.Status.ACTIVE,
                "test_program_stage", eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariableOne, ruleVariableTwo), ruleEnrollment.attributeValues(),
                Arrays.asList(contextEvent)
        );

        // here we will expect correct values to be returned
        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(currentEvent);

        // 2 variables defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(4);

        RuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        RuleVariableValue variableValueTwo = valueMap.get("test_variable_two");

        // first variable
        assertThat(variableValueOne.value()).isEqualTo("test_attribute_value_one");
        assertThat(variableValueOne.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueOne.candidates().size()).isEqualTo(1);
        assertThat(variableValueOne.candidates().get(0)).isEqualTo("test_attribute_value_one");

        // second variable
        assertThat(variableValueTwo.value()).isEqualTo("test_attribute_value_two");
        assertThat(variableValueTwo.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValueTwo.candidates().size()).isEqualTo(1);
        assertThat(variableValueTwo.candidates().get(0)).isEqualTo("test_attribute_value_two");
    }

    @Test
    public void previousEventVariableShouldContainValuesFromPreviousEvent() throws ParseException {
        RuleVariable ruleVariable = RuleVariablePreviousEvent.create("test_variable",
                "test_dataelement", RuleValueType.TEXT);

        Date dateEventOne = dateFormat.parse("2014-02-03");
        Date dateEventTwo = dateFormat.parse("2014-03-03");
        Date dateEventThree = dateFormat.parse("2015-02-03");
        Date dateEventCurrent = dateFormat.parse("2014-05-03");

        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_one", RuleEvent.Status.ACTIVE,
                "test_program_stage", dateEventOne, dateEventOne, Arrays.asList(
                        RuleDataValue.create(dateEventOne, "test_program_stage_one",
                                "test_dataelement", "test_value_one")));
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_two", RuleEvent.Status.ACTIVE,
                "test_program_stage", dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));
        RuleEvent ruleEventThree = RuleEvent.create("test_event_uid_three", RuleEvent.Status.ACTIVE,
                "test_program_stage", dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_three")));
        RuleEvent ruleEventCurrent = RuleEvent.create("test_event_uid_current", RuleEvent.Status.ACTIVE,
                "test_program_stage", dateEventCurrent, dateEventCurrent, Arrays.asList(
                        RuleDataValue.create(dateEventCurrent, "test_program_stage_one",
                                "test_dataelement", "test_value_current")));

        RuleVariableValueMapFactory ruleVariableValueMapFactory = new RuleVariableValueMapFactory(
                Arrays.asList(ruleVariable), new ArrayList<RuleAttributeValue>(),
                Arrays.asList(ruleEventOne, ruleEventTwo, ruleEventThree)
        );

        Map<String, RuleVariableValue> valueMap = ruleVariableValueMapFactory.build(ruleEventCurrent);

        // 1 variable defined within test + 2 environment variables
        assertThat(valueMap.size()).isEqualTo(3);

        RuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isEqualTo("test_value_two");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(4);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_three");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_current");
        assertThat(variableValue.candidates().get(2)).isEqualTo("test_value_two");
        assertThat(variableValue.candidates().get(3)).isEqualTo("test_value_one");
    }
}
