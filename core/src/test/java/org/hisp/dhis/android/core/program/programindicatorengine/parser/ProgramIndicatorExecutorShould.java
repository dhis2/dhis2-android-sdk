/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program.programindicatorengine.parser;


import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramIndicatorExecutorShould {

    private String enrollmentUid = "enrollment-uid";
    private String programStage1 = "p2adVnEmIei";
    private String programStage2 = "trjLe2gx6lI";
    private String eventUid1 = "qCUMGmMZAhz";
    private String eventUid2_1 = "QApcv9Je3bp";
    private String eventUid2_2 = "fvTdau868YO";
    private String attribute1 = "JiuwgfybPrE";
    private String attribute2 = "U4w2S7vUxV7";
    private String dataElement1 = "UUqzccMujME";
    private String dataElement2 = "JHpWWC1bISq";
    private String programIndicatorUid = "program-indicator-uid";
    private String trackedEntityInstanceUid = "tei-uid";

    @Mock
    private IdentifiableObjectStore<ProgramIndicator> programIndicatorStore;
    @Mock
    private IdentifiableObjectStore<Constant> constantStore;

    @Mock
    private ProgramIndicator programIndicator;
    @Mock
    private ProgramIndicatorContext programIndicatorContext;

    @Mock
    private Constant constant;

    private Map<String, Constant> constantMap = new HashMap<>();

    @Mock
    private Enrollment enrollment;

    @Mock
    private TrackedEntityAttributeValue attributeValue1, attributeValue2;

    private Map<String, TrackedEntityAttributeValue> attributeValueMap = new HashMap<>();

    @Mock
    private Event event1, event2_1, event2_2;

    private Map<String, List<Event>> eventsMap = new HashMap<>();

    @Mock
    private TrackedEntityDataValue dataValue1, dataValue2_1, dataValue2_2;


    @Mock
    private TrackedEntityAttributeValue attributeValue;

    private String constantUid1 = "gzlRs2HEGAf";

    private ProgramIndicatorExecutor programIndicatorExecutor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        constantMap.put(constantUid1, constant);
        when(constant.uid()).thenReturn(constantUid1);

        attributeValueMap.put(attribute1, attributeValue1);
        attributeValueMap.put(attribute2, attributeValue2);
        when(attributeValue1.trackedEntityAttribute()).thenReturn(attribute1);
        when(attributeValue2.trackedEntityAttribute()).thenReturn(attribute2);

        eventsMap.put(programStage1, Collections.singletonList(event1));
        eventsMap.put(programStage2, Arrays.asList(event2_1, event2_2));
        when(event1.uid()).thenReturn(eventUid1);
        when(event2_1.uid()).thenReturn(eventUid2_1);
        when(event2_2.uid()).thenReturn(eventUid2_2);

        // Data values
        when(event1.trackedEntityDataValues()).thenReturn(Collections.singletonList(dataValue1));
        when(event2_1.trackedEntityDataValues()).thenReturn(Collections.singletonList(dataValue2_1));
        when(event2_2.trackedEntityDataValues()).thenReturn(Collections.singletonList(dataValue2_2));

        when(dataValue1.dataElement()).thenReturn(dataElement1);
        when(dataValue2_1.dataElement()).thenReturn(dataElement2);
        when(dataValue2_2.dataElement()).thenReturn(dataElement2);


        when(programIndicatorContext.programIndicator()).thenReturn(programIndicator);
        when(programIndicatorContext.enrollment()).thenReturn(enrollment);
        when(programIndicatorContext.attributeValues()).thenReturn(attributeValueMap);
        when(programIndicatorContext.events()).thenReturn(eventsMap);

        programIndicatorExecutor = new ProgramIndicatorExecutor(
                constantMap,
                programIndicatorContext
        );

    }

    @Test
    public void evaluate_constants() {
        when(constant.value()).thenReturn(5.3);

        String result = programIndicatorExecutor.getProgramIndicatorValue(cons(constantUid1));

        assertThat(result).isEqualTo("5.3");
    }

    @Test
    public void evaluate_tracked_entity_attribute_value() {
        String expression = att(attribute1) + " - " + att(attribute2);
        when(attributeValue1.value()).thenReturn("7.8");
        when(attributeValue2.value()).thenReturn("2.5");

        String result = programIndicatorExecutor.getProgramIndicatorValue(expression);

        assertThat(result).isEqualTo("5.3");
    }

    @Test
    public void evaluate_data_elements_in_stage() {
        String expression = de(programStage1, dataElement1) + " + " + de(programStage2, dataElement2);

        when(dataValue1.value()).thenReturn("4.5");
        when(dataValue2_1.value()).thenReturn("0.8");
        when(dataValue2_2.value()).thenReturn("20.5");

        when(programIndicator.aggregationType()).thenReturn(AggregationType.NONE);
        String resultNone = programIndicatorExecutor.getProgramIndicatorValue(expression);
        assertThat(resultNone).isEqualTo("5.3");

        when(programIndicator.aggregationType()).thenReturn(AggregationType.LAST);
        String resultLast = programIndicatorExecutor.getProgramIndicatorValue(expression);
        assertThat(resultLast).isEqualTo("25.0");
    }

    @Test
    public void evaluate_data_elements_with_value_count() {
        setExpression("(" + de(programStage1, dataElement1) + " + " + de(programStage2, dataElement2)  + ") / " +
                var("value_count"));

        when(dataValue1.value()).thenReturn("4.5");
        when(dataValue2_1.value()).thenReturn("1.5");
        when(dataValue2_2.value()).thenReturn("20.5");

        String resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNone).isEqualTo("3.0");
    }

    @Test
    public void evaluate_data_elements_with_zero_pos_value_count() {
        setExpression("(" + de(programStage1, dataElement1) + " + " + de(programStage2, dataElement2)  + ") / " +
                var("zero_pos_value_count"));

        when(dataValue1.value()).thenReturn("7.5");
        when(dataValue2_1.value()).thenReturn("-1.5");

        String resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNone).isEqualTo("6.0");
    }

    @Test
    public void evaluate_event_count() {
        setExpression(var("event_count"));

        String resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNone).isEqualTo("3");
    }

    @Test
    public void evaluate_enrollment_dates() throws ParseException {
        setExpression("d2:daysBetween(" + var("enrollment_date") + "," + var("incident_date") + ")");

        when(enrollment.enrollmentDate()).thenReturn(BaseIdentifiableObject.parseDate("2020-05-01T00:00:00.000"));
        when(enrollment.incidentDate()).thenReturn(BaseIdentifiableObject.parseDate("2020-05-05T00:00:00.000"));

        String resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNone).isEqualTo("4");
    }

    @Test
    public void evaluate_d2_count() {
        setExpression("d2:count(" + de(programStage2, dataElement2) + ")");

        when(dataValue2_1.value()).thenReturn("1.5");
        when(dataValue2_2.value()).thenReturn("20.5");

        String result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(result).isEqualTo("2");
    }

    @Test
    public void evaluate_d2_count_if_condition() {
        setExpression("d2:countIfCondition(" + de(programStage2, dataElement2) + ", '< 10')");

        when(dataValue2_1.value()).thenReturn("1.5");
        when(dataValue2_2.value()).thenReturn("20.5");

        String result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void evaluate_d2_count_if_value_numeric() {
        setExpression("d2:countIfValue(" + de(programStage2, dataElement2) + ", 1.5)");

        when(dataValue2_1.value()).thenReturn("1.5");
        when(dataValue2_2.value()).thenReturn("20.5");

        String result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void evaluate_d2_count_if_value_string() {
        setExpression("d2:countIfValue(" + de(programStage2, dataElement2) + ", 'positive')");

        when(dataValue2_1.value()).thenReturn("positive");
        when(dataValue2_2.value()).thenReturn("negative");

        String result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void evaluate_d2_has_attribute_value() {
        setExpression("d2:hasValue(" + att(attribute1) + ")");

        when(attributeValue1.value()).thenReturn(null);
        String resultNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNull).isEqualTo("false");

        when(attributeValue1.value()).thenReturn("3.4");
        String resultNonNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNonNull).isEqualTo("true");
    }

    @Test
    public void evaluate_d2_has_data_value() {
        setExpression("d2:hasValue(" + de(programStage1, dataElement2) + ")");
        String resultNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNull).isEqualTo("false");

        setExpression("d2:hasValue(" + de(programStage1, dataElement1) + ")");
        when(dataValue1.value()).thenReturn("value");
        String resultNonNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultNonNull).isEqualTo("true");
    }

    @Test
    public void evaluate_d2_condition() {
        setExpression("d2:condition('" + de(programStage1, dataElement1) + " < 10', 150, 50)");

        when(dataValue1.value()).thenReturn("8");
        String resultTrue = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultTrue).isEqualTo("150.0");

        when(dataValue1.value()).thenReturn("15");
        String resultFalse = programIndicatorExecutor.getProgramIndicatorValue(programIndicator.expression());
        assertThat(resultFalse).isEqualTo("50.0");

    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void setExpression(String expression) {
        when(programIndicator.expression()).thenReturn(expression);
    }

    private String de(String programStageUid, String dataElementUid) {
        return "#{" + programStageUid + "." + dataElementUid + "}";
    }

    private String cons(String constantUid) {
        return "C{" + constantUid + "}";
    }

    private String var(String variable) {
        return "V{" + variable + "}";
    }

    private String att(String attributeUid) {
        return "A{" + attributeUid + "}";
    }
}