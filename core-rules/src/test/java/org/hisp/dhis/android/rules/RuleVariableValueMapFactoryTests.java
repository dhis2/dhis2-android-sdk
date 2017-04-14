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

        try {
            RuleVariableValueMapBuilder.target(ruleEvent)
                    .ruleVariables(new ArrayList<RuleVariable>())
                    .build().clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void buildShouldReturnMapWithEnvVariables() throws ParseException {
        RuleEvent ruleEvent = mock(RuleEvent.class);
        when(ruleEvent.eventDate()).thenReturn(dateFormat.parse("1994-02-03"));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(ruleEvent)
                .ruleVariables(new ArrayList<RuleVariable>())
                .build();

        RuleVariableValue eventDate = valueMap.get("event_date");
        assertThat(eventDate.value()).isEqualTo("1994-02-03");
        assertThat(eventDate.candidates().size()).isEqualTo(1);
        assertThat(eventDate.candidates().get(0)).isEqualTo("1994-02-03");

        String today = dateFormat.format(new Date());
        RuleVariableValue currentDate = valueMap.get("current_date");
        assertThat(currentDate.value()).isEqualTo(today);
        assertThat(currentDate.candidates().size()).isEqualTo(1);
        assertThat(currentDate.candidates().get(0)).isEqualTo(today);
    }

    @Test
    public void ruleEnrollmentShouldThrowIfTargetEnrollmentIsSet() {
        try {
            RuleEnrollment ruleEnrollment = mock(RuleEnrollment.class);
            RuleVariableValueMapBuilder.target(ruleEnrollment)
                    .ruleEnrollment(ruleEnrollment)
                    .build();
        } catch (IllegalStateException illegalStateException) {
            // noop
        }
    }

    @Test
    public void currentEventVariableShouldContainValuesFromCurrentEvent() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableCurrentEvent.create(
                "test_variable_one", "test_dataelement_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableCurrentEvent.create(
                "test_variable_two", "test_dataelement_two", RuleValueType.TEXT);

        Date eventDate = dateFormat.parse("2015-01-01");

        // values from context ruleEvents should be ignored
        RuleEvent contextEventOne = RuleEvent.create("test_context_event_one", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_two")));
        RuleEvent contextEventTwo = RuleEvent.create("test_context_event_two", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_three"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_four")));
        // values from current ruleEvent should be propagated to the variable values
        RuleEvent currentEvent = RuleEvent.create("test_event_uid", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .ruleEvents(Arrays.asList(contextEventOne, contextEventTwo))
                .build();

        assertThat(valueMap.size()).isEqualTo(5);
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

        RuleEvent oldestRuleEvent = RuleEvent.create("test_event_uid_oldest", "test_program_stage",
                RuleEvent.Status.ACTIVE, oldestEventDate, oldestEventDate, Arrays.asList(
                        RuleDataValue.create(oldestEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_oldest"),
                        RuleDataValue.create(oldestEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_oldest")));
        RuleEvent newestRuleEvent = RuleEvent.create("test_event_uid_newest", "test_program_stage",
                RuleEvent.Status.ACTIVE, newestEventDate, newestEventDate, Arrays.asList(
                        RuleDataValue.create(newestEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_newest"),
                        RuleDataValue.create(newestEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_newest")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid_current", "test_program_stage",
                RuleEvent.Status.ACTIVE, currentEventDate, currentEventDate, Arrays.asList(
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one_current"),
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two_current")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .ruleEvents(Arrays.asList(oldestRuleEvent, newestRuleEvent))
                .build();

        assertThat(valueMap.size()).isEqualTo(5);

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

        RuleEvent firstRuleEvent = RuleEvent.create("test_event_uid_one", "test_program_stage",
                RuleEvent.Status.ACTIVE, firstEventDate, firstEventDate, Arrays.asList(
                        RuleDataValue.create(firstEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_first"),
                        RuleDataValue.create(firstEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_first")));
        RuleEvent secondRuleEvent = RuleEvent.create("test_event_uid_two", "test_program_stage",
                RuleEvent.Status.ACTIVE, secondEventDate, secondEventDate, Arrays.asList(
                        RuleDataValue.create(secondEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_second"),
                        RuleDataValue.create(secondEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_second")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid_current", "test_program_stage",
                RuleEvent.Status.ACTIVE, currentEventDate, currentEventDate, Arrays.asList(
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_dataelement_one_current"),
                        RuleDataValue.create(currentEventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_dataelement_two_current")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .ruleEvents(Arrays.asList(firstRuleEvent, secondRuleEvent))
                .build();

        assertThat(valueMap.size()).isEqualTo(5);

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

        RuleEvent eventOne = RuleEvent.create("test_event_uid_one", "test_program_stage_one",
                RuleEvent.Status.ACTIVE, dateEventOne, dateEventOne, Arrays.asList(
                        RuleDataValue.create(dateEventOne, "test_program_stage_one",
                                "test_dataelement", "test_value_one")));
        RuleEvent eventTwo = RuleEvent.create("test_event_uid_two", "test_program_stage_two",
                RuleEvent.Status.ACTIVE, dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));
        RuleEvent eventThree = RuleEvent.create("test_event_uid_three", "test_program_stage_two",
                RuleEvent.Status.ACTIVE, dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_three")));
        RuleEvent eventCurrent = RuleEvent.create("test_event_uid_current", "test_program_stage_one",
                RuleEvent.Status.ACTIVE, dateEventCurrent, dateEventCurrent, Arrays.asList(
                        RuleDataValue.create(dateEventCurrent, "test_program_stage_one",
                                "test_dataelement", "test_value_current")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(eventCurrent)
                .ruleVariables(Arrays.asList(ruleVariable))
                .ruleEvents(Arrays.asList(eventOne, eventTwo, eventThree))
                .build();

        assertThat(valueMap.size()).isEqualTo(4);

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

        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_two", "test_program_stage_two",
                RuleEvent.Status.ACTIVE, dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_one")));
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_three", "test_program_stage_two",
                RuleEvent.Status.ACTIVE, dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(ruleEventTwo)
                .ruleVariables(Arrays.asList(ruleVariable))
                .ruleEvents(Arrays.asList(ruleEventOne, ruleEventTwo))
                .build();

        assertThat(valueMap.size()).isEqualTo(4);

        RuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isNull();
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(0);
    }

    @Test
    public void previousEventVariableShouldContainValuesFromPreviousEvent() throws ParseException {
        RuleVariable ruleVariable = RuleVariablePreviousEvent.create("test_variable",
                "test_dataelement", RuleValueType.TEXT);

        Date dateEventOne = dateFormat.parse("2014-02-03");
        Date dateEventTwo = dateFormat.parse("2014-03-03");
        Date dateEventThree = dateFormat.parse("2015-02-03");
        Date dateEventCurrent = dateFormat.parse("2014-05-03");

        RuleEvent ruleEventOne = RuleEvent.create("test_event_uid_one", "test_program_stage",
                RuleEvent.Status.ACTIVE, dateEventOne, dateEventOne, Arrays.asList(
                        RuleDataValue.create(dateEventOne, "test_program_stage_one",
                                "test_dataelement", "test_value_one")));
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_uid_two", "test_program_stage",
                RuleEvent.Status.ACTIVE, dateEventTwo, dateEventTwo, Arrays.asList(
                        RuleDataValue.create(dateEventTwo, "test_program_stage_two",
                                "test_dataelement", "test_value_two")));
        RuleEvent ruleEventThree = RuleEvent.create("test_event_uid_three", "test_program_stage",
                RuleEvent.Status.ACTIVE, dateEventThree, dateEventThree, Arrays.asList(
                        RuleDataValue.create(dateEventThree, "test_program_stage_two",
                                "test_dataelement", "test_value_three")));
        RuleEvent ruleEventCurrent = RuleEvent.create("test_event_uid_current", "test_program_stage",
                RuleEvent.Status.ACTIVE, dateEventCurrent, dateEventCurrent, Arrays.asList(
                        RuleDataValue.create(dateEventCurrent, "test_program_stage_one",
                                "test_dataelement", "test_value_current")));

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(ruleEventCurrent)
                .ruleVariables(Arrays.asList(ruleVariable))
                .ruleEvents(Arrays.asList(ruleEventOne, ruleEventTwo, ruleEventThree))
                .build();

        assertThat(valueMap.size()).isEqualTo(4);

        RuleVariableValue variableValue = valueMap.get("test_variable");
        assertThat(variableValue.value()).isEqualTo("test_value_two");
        assertThat(variableValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(variableValue.candidates().size()).isEqualTo(4);
        assertThat(variableValue.candidates().get(0)).isEqualTo("test_value_three");
        assertThat(variableValue.candidates().get(1)).isEqualTo("test_value_current");
        assertThat(variableValue.candidates().get(2)).isEqualTo("test_value_two");
        assertThat(variableValue.candidates().get(3)).isEqualTo("test_value_one");
    }

    @Test
    public void attributeVariableShouldContainValuesFromContextEnrollment() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableAttribute.create("test_variable_one",
                "test_attribute_one", RuleValueType.TEXT);
        RuleVariable ruleVariableTwo = RuleVariableAttribute.create("test_variable_two",
                "test_attribute_two", RuleValueType.TEXT);

        Date eventDate = dateFormat.parse("2015-01-01");
        Date enrollmentDate = dateFormat.parse("2014-03-01");

        // values from ruleEnrollment should end up in ruleVariables
        RuleEnrollment ruleEnrollment = RuleEnrollment.create("test_enrollment",
                enrollmentDate, enrollmentDate, RuleEnrollment.Status.ACTIVE, Arrays.asList(
                        RuleAttributeValue.create("test_attribute_one", "test_attribute_value_one"),
                        RuleAttributeValue.create("test_attribute_two", "test_attribute_value_two")));

        // values from context ruleEvents should be ignored
        RuleEvent contextEvent = RuleEvent.create("test_context_event_one", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_context_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_context_value_two")));
        RuleEvent currentEvent = RuleEvent.create("test_event_uid", "test_program_stage",
                RuleEvent.Status.ACTIVE, eventDate, new Date(), Arrays.asList(
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_one", "test_value_one"),
                        RuleDataValue.create(eventDate, "test_program_stage",
                                "test_dataelement_two", "test_value_two")));

        // here we will expect correct values to be returned
        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(currentEvent)
                .ruleEnrollment(ruleEnrollment)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo))
                .ruleEvents(Arrays.asList(contextEvent))
                .build();

        assertThat(valueMap.size()).isEqualTo(10);

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
    public void ruleEnrollmentValuesShouldBePropagatedToMapCorrectly() throws ParseException {
        RuleVariable ruleVariableOne = RuleVariableAttribute.create("test_variable_one",
                "test_attribute_one", RuleValueType.NUMERIC);
        RuleVariable ruleVariableTwo = RuleVariableAttribute.create("test_variable_two",
                "test_attribute_two", RuleValueType.TEXT);
        RuleVariable ruleVariableThree = RuleVariableCurrentEvent.create("test_variable_three",
                "test_dataelement_one", RuleValueType.BOOLEAN);

        String currentDate = dateFormat.format(new Date());
        Date enrollmentDate = dateFormat.parse("2017-02-02");
        Date incidentDate = dateFormat.parse("2017-04-02");
        RuleEnrollment ruleEnrollment = RuleEnrollment.create("test_enrollment", incidentDate,
                enrollmentDate, RuleEnrollment.Status.ACTIVE, Arrays.asList(
                        RuleAttributeValue.create("test_attribute_one", "test_attribute_value_one"),
                        RuleAttributeValue.create("test_attribute_two", "test_attribute_value_two"),
                        RuleAttributeValue.create("test_attribute_three", "test_attribute_value_three")));

        RuleEvent ruleEventOne = RuleEvent.create("test_event_one", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());
        RuleEvent ruleEventTwo = RuleEvent.create("test_event_two", "test_program_stage",
                RuleEvent.Status.ACTIVE, new Date(), new Date(), new ArrayList<RuleDataValue>());

        Map<String, RuleVariableValue> valueMap = RuleVariableValueMapBuilder.target(ruleEnrollment)
                .ruleVariables(Arrays.asList(ruleVariableOne, ruleVariableTwo, ruleVariableThree))
                .ruleEvents(Arrays.asList(ruleEventOne, ruleEventTwo))
                .build();

        assertThat(valueMap.size()).isEqualTo(9);

        // Environment variables
        RuleVariableValue currentDateValue = valueMap.get("current_date");
        assertThat(currentDateValue.value()).isEqualTo(currentDate);
        assertThat(currentDateValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(currentDateValue.candidates().size()).isEqualTo(1);
        assertThat(currentDateValue.candidates().get(0)).isEqualTo(currentDate);

        RuleVariableValue enrollmentDateValue = valueMap.get("enrollment_date");
        assertThat(enrollmentDateValue.value()).isEqualTo(dateFormat.format(enrollmentDate));
        assertThat(enrollmentDateValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(enrollmentDateValue.candidates().size()).isEqualTo(1);
        assertThat(enrollmentDateValue.candidates().get(0)).isEqualTo(dateFormat.format(enrollmentDate));

        RuleVariableValue enrollmentIdValue = valueMap.get("enrollment_id");
        assertThat(enrollmentIdValue.value()).isEqualTo("test_enrollment");
        assertThat(enrollmentIdValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(enrollmentIdValue.candidates().size()).isEqualTo(1);
        assertThat(enrollmentIdValue.candidates().get(0)).isEqualTo("test_enrollment");

        RuleVariableValue enrollmentCountValue = valueMap.get("enrollment_count");
        assertThat(enrollmentCountValue.value()).isEqualTo("1");
        assertThat(enrollmentCountValue.type()).isEqualTo(RuleValueType.NUMERIC);
        assertThat(enrollmentCountValue.candidates().size()).isEqualTo(1);
        assertThat(enrollmentCountValue.candidates().get(0)).isEqualTo("1");

        RuleVariableValue incidentDateValue = valueMap.get("incident_date");
        assertThat(incidentDateValue.value()).isEqualTo(dateFormat.format(incidentDate));
        assertThat(incidentDateValue.type()).isEqualTo(RuleValueType.TEXT);
        assertThat(incidentDateValue.candidates().size()).isEqualTo(1);
        assertThat(incidentDateValue.candidates().get(0)).isEqualTo(dateFormat.format(incidentDate));

        RuleVariableValue teiCount = valueMap.get("tei_count");
        assertThat(teiCount.value()).isEqualTo("1");
        assertThat(teiCount.type()).isEqualTo(RuleValueType.NUMERIC);
        assertThat(teiCount.candidates().size()).isEqualTo(1);
        assertThat(teiCount.candidates().get(0)).isEqualTo("1");

        RuleVariableValue eventCount = valueMap.get("event_count");
        assertThat(eventCount.value()).isEqualTo("2");
        assertThat(eventCount.type()).isEqualTo(RuleValueType.NUMERIC);
        assertThat(eventCount.candidates().size()).isEqualTo(1);
        assertThat(eventCount.candidates().get(0)).isEqualTo("2");

        RuleVariableValue variableValueOne = valueMap.get("test_variable_one");
        assertThat(variableValueOne.value()).isEqualTo("test_attribute_value_one");
        assertThat(variableValueOne.candidates().size()).isEqualTo(1);
        assertThat(variableValueOne.candidates().get(0)).isEqualTo("test_attribute_value_one");

        RuleVariableValue variableValueTwo = valueMap.get("test_variable_two");
        assertThat(variableValueTwo.value()).isEqualTo("test_attribute_value_two");
        assertThat(variableValueTwo.candidates().size()).isEqualTo(1);
        assertThat(variableValueTwo.candidates().get(0)).isEqualTo("test_attribute_value_two");
    }

    // ToDo: add test case when current event is set in the context of events: event_count should reflect this
}
