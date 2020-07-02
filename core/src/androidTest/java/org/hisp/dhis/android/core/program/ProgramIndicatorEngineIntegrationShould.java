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

package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.internal.ProgramStageStore;
import org.hisp.dhis.android.core.program.internal.ProgramStore;
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorEngineIntegrationShould extends BaseMockIntegrationTestEmptyDispatcher {

    private static String teiUid = "H87GEVeG3JH";
    private static String enrollmentUid = "la16vwCoFM8";
    private static String event1 = "gphKB0UjOrX";
    private static String event2 = "EAZOUgr2Ksv";
    private static String event3 = "BVL4LcEEDdU";

    private static String dataElement1 = "ddaBs9lgZyP";
    private static String dataElement2 = "Kb9hZ428FyH";
    private static String attribute1 = "Kmtdopp5GC1";
    private static String programIndicatorUid = "rg3JkCv0skl";

    // Auxiliary variables
    private static String orgunitUid = "orgunit_uid";
    private static String teiTypeUid = "tei_type_uid";
    private static String programUid = "program_uid";
    private static String programStage1 = "iM4svLr2hlO";
    private static String programStage2 = "RXFTSe1oefv";

    private static ProgramIndicatorEngine programIndicatorEngine;

    @Before
    public void setUp() throws Exception {
        BaseMockIntegrationTestEmptyDispatcher.setUpClass();

        programIndicatorEngine = d2.programModule().programIndicatorEngine();
        
        OrganisationUnit orgunit = OrganisationUnit.builder().uid(orgunitUid).build();
        OrganisationUnitStore.create(databaseAdapter).insert(orgunit);

        TrackedEntityType trackedEntityType = TrackedEntityType.builder().uid(teiTypeUid).build();
        TrackedEntityTypeStore.create(databaseAdapter).insert(trackedEntityType);

        TrackedEntityInstanceStore teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter);
        TrackedEntityInstance trackedEntityInstance = TrackedEntityInstance.builder()
                .uid(teiUid)
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit(orgunitUid)
                .trackedEntityType(teiTypeUid)
                .build();

        teiStore.insert(trackedEntityInstance);

        ContentValues categoryCombo = CreateCategoryComboUtils.create(1L, CategoryCombo.DEFAULT_UID);
        databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo);

        Access access = Access.create(true, null, DataAccess.create(true, true));
        Program program = Program.builder().uid(programUid)
                .access(access)
                .trackedEntityType(TrackedEntityType.builder().uid(teiTypeUid).build())
                .build();
        ProgramStore.create(databaseAdapter).insert(program);

        ProgramStage stage1 = ProgramStage.builder().uid(programStage1).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build();
        ProgramStage stage2 = ProgramStage.builder().uid(programStage2).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build();

        IdentifiableObjectStore<ProgramStage> programStageStore = ProgramStageStore.create(databaseAdapter);
        programStageStore.insert(stage1);
        programStageStore.insert(stage2);

        DataElement de1 = DataElement.builder().uid(dataElement1).valueType(ValueType.NUMBER).build();
        DataElement de2 = DataElement.builder().uid(dataElement2).valueType(ValueType.NUMBER).build();
        IdentifiableObjectStore<DataElement> dataElementStore = DataElementStore.create(databaseAdapter);
        dataElementStore.insert(de1);
        dataElementStore.insert(de2);

        TrackedEntityAttribute tea = TrackedEntityAttribute.builder().uid(attribute1).build();
        TrackedEntityAttributeStore.create(databaseAdapter).insert(tea);
    }

    @After
    public void tearDown() throws D2Error {
        d2.wipeModule().wipeEverything();
    }

    @Test
    public void evaluate_single_dataelement() {
        createEnrollment(null,null);
        createEvent(event1, programStage1, new Date());
        insertTrackedEntityDataValue(event1, dataElement1, "4");

        setProgramIndicatorExpressionAsAverage(de(programStage1,dataElement1));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("4");
    }

    @Test
    public void evaluate_single_text_dataelement() {
        createEnrollment(null,null);
        createEvent(event1, programStage1, new Date());
        insertTrackedEntityDataValue(event1, dataElement1, "text data-value");

        setProgramIndicatorExpressionAsAverage(de(programStage1,dataElement1));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("text data-value");
    }

    @Test
    public void evaluate_addition_two_dataelement() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, null);
        insertTrackedEntityDataValue(event1, dataElement1, "5");
        insertTrackedEntityDataValue(event1, dataElement2, "3");

        setProgramIndicatorExpressionAsAverage(de(programStage1,dataElement1) + " * " + de(programStage1,dataElement2));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("15");
    }

    @Test
    public void evaluate_division_two_dataelement() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, null);
        insertTrackedEntityDataValue(event1, dataElement1, "3");
        insertTrackedEntityDataValue(event1, dataElement2, "5");

        setProgramIndicatorExpressionAsAverage(de(programStage1,dataElement1) + " / " + de(programStage1,dataElement2));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("0.6");
    }

    @Test
    public void evaluate_last_value_indicators_different_dates() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, twoDaysBefore(), today());
        createEvent(event2, programStage1, today(), today());
        createEvent(event3, programStage1, twoDaysBefore(), today());
        insertTrackedEntityDataValue(event1, dataElement1, "1");
        insertTrackedEntityDataValue(event2, dataElement1, "2"); // Expected as last value
        insertTrackedEntityDataValue(event3, dataElement1, "3");

        setProgramIndicatorExpressionAsLast(de(programStage1,dataElement1));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, null, programIndicatorUid);

        assertThat(result).isEqualTo("2");
    }

    @Test
    public void evaluate_last_value_indicators_same_date() {
        createEnrollment(null, null);
        Date eventDate = twoDaysBefore();
        createEvent(event1, programStage1, eventDate, twoDaysBefore());
        createEvent(event2, programStage1, eventDate, today());
        createEvent(event3, programStage1, eventDate, twoDaysBefore());
        insertTrackedEntityDataValue(event1, dataElement1, "1");
        insertTrackedEntityDataValue(event2, dataElement1, "2"); // Expected as last value
        insertTrackedEntityDataValue(event3, dataElement1, "3");

        setProgramIndicatorExpressionAsLast(de(programStage1,dataElement1));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, null, programIndicatorUid);

        assertThat(result).isEqualTo("2");
    }

    @Test
    public void evaluate_operation_several_stages() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, null);
        createEvent(event2, programStage2, null);

        insertTrackedEntityDataValue(event1, dataElement1, "5");
        insertTrackedEntityDataValue(event2, dataElement2, "1.5");
        insertTrackedEntityAttributeValue(attribute1, "2");

        setProgramIndicatorExpressionAsAverage("(" + de(programStage1,dataElement1) + " + " + de(programStage2,dataElement2) +
                ") / " + att(attribute1));

        String resultWithoutEvent = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, null,
                programIndicatorUid);
        String resultWithEvent = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1,
                programIndicatorUid);

        assertThat(resultWithoutEvent).isEqualTo("3.25");
        assertThat(resultWithEvent).isEqualTo("3.25");
    }

    @Test
    public void evaluate_event_count_variable() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, null);
        createEvent(event2, programStage2, null);
        createDeletedEvent(event3, programStage2);

        setProgramIndicatorExpressionAsAverage(var("event_count"));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, null,
                programIndicatorUid);

        assertThat(result).isEqualTo("2");
    }

    @Test
    public void evaluate_expression_with_d2_functions() {
        createEnrollment(null, null);
        createEvent(event1, programStage1, null);
        insertTrackedEntityDataValue(event1, dataElement1, "4.8");
        insertTrackedEntityDataValue(event1, dataElement2, "3");

        setProgramIndicatorExpressionAsAverage("d2:round(" + de(programStage1,dataElement1) + ") * " +
                de(programStage1, dataElement2));

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("15");
    }

    @Test
    public void evaluate_d2_functions_with_dates() throws ParseException {
        Date enrollmentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2018-05-05T00:00:00.000");
        Date incidentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2018-05-21T00:00:00.000");

        createEnrollment(enrollmentDate, incidentDate);

        setProgramIndicatorExpressionAsAverage("d2:daysBetween(V{enrollment_date}, V{incident_date})");

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, null, programIndicatorUid);

        assertThat(result).isEqualTo("16");
    }

    private void createEnrollment(Date enrollmentDate, Date incidentDate) {
        Enrollment enrollment = Enrollment.builder().uid(enrollmentUid).organisationUnit(orgunitUid).program(programUid)
                .enrollmentDate(enrollmentDate).incidentDate(incidentDate).trackedEntityInstance(teiUid).build();
        EnrollmentStoreImpl.create(databaseAdapter).insert(enrollment);
    }

    private void createEvent(String eventUid, String programStageUid, Date eventDate, Date lastUpdated) {
        Event event = Event.builder().uid(eventUid).enrollment(enrollmentUid).lastUpdated(lastUpdated)
                .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
                .eventDate(eventDate).deleted(false).build();

        EventStoreImpl.create(databaseAdapter).insert(event);
    }

    private void createEvent(String eventUid, String programStageUid, Date eventDate) {
        createEvent(eventUid, programStageUid, eventDate, null);
    }

    private void createDeletedEvent(String eventUid, String programStageUid) {
        Event event = Event.builder().uid(eventUid).enrollment(enrollmentUid).lastUpdated(null)
                .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
                .eventDate(null).deleted(true).build();

        EventStoreImpl.create(databaseAdapter).insert(event);
    }

    private void setProgramIndicatorExpressionAsAverage(String expression) {
        insertProgramIndicator(expression, AggregationType.AVERAGE);
    }

    private void setProgramIndicatorExpressionAsLast(String expression) {
        insertProgramIndicator(expression, AggregationType.LAST);
    }

    private void insertProgramIndicator(String expression, AggregationType aggregationType) {
        ProgramIndicator programIndicator = ProgramIndicator.builder().uid(programIndicatorUid)
                .program(ObjectWithUid.create(programUid)).expression(expression).aggregationType(aggregationType).build();
        ProgramIndicatorStore.create(databaseAdapter).insert(programIndicator);
    }

    private void insertTrackedEntityDataValue(String eventUid, String dataElementUid, String value) {
        TrackedEntityDataValue trackedEntityDataValue = TrackedEntityDataValue.builder()
                .event(eventUid)
                .dataElement(dataElementUid)
                .value(value).build();

        TrackedEntityDataValueStoreImpl.create(databaseAdapter).insert(trackedEntityDataValue);
    }

    private void insertTrackedEntityAttributeValue(String attributeUid, String value) {
        TrackedEntityAttributeValue trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
                .value(value).trackedEntityAttribute(attributeUid).trackedEntityInstance(teiUid).build();
        TrackedEntityAttributeValueStoreImpl.create(databaseAdapter).insert(trackedEntityAttributeValue);
    }

    private String de(String programStageUid, String dataElementUid) {
        return "#{" + programStageUid + "." + dataElementUid + "}";
    }

    private String att(String attributeUid) {
        return "A{" + attributeUid + "}";
    }

    private String var(String variable) {
        return "V{" + variable + "}";
    }

    private Date today() {
        return new Date();
    }

    private Date twoDaysBefore() {
        Long newTime = (new Date()).getTime() - (2 * 24 * 60 * 60 * 1000);
        return new Date(newTime);
    }
}