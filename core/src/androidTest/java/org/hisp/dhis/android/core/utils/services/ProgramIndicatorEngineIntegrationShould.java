/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.utils.services;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorEngineIntegrationShould extends AbsStoreTestCase {

    private String teiUid = "H87GEVeG3JH";
    private String enrollmentUid = "la16vwCoFM8";
    private String event1 = "gphKB0UjOrX";
    private String event2 = "EAZOUgr2Ksv";
    private String event3 = "BVL4LcEEDdU";

    private String dataElement1 = "ddaBs9lgZyP";
    private String dataElement2 = "Kb9hZ428FyH";
    private String attribute1 = "Kmtdopp5GC1";
    private String programIndicatorUid = "rg3JkCv0skl";

    // Auxiliary variables
    private String orgunitUid = "orgunit_uid";
    private String teiTypeUid = "tei_type_uid";
    private String programUid = "program_uid";
    private String programStage1 = "iM4svLr2hlO";
    private String programStage2 = "RXFTSe1oefv";

    private ProgramIndicatorEngine programIndicatorEngine;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        D2 d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        programIndicatorEngine = d2.programModule().programIndicatorEngine;
        
        OrganisationUnit orgunit = OrganisationUnit.builder().uid(orgunitUid).build();
        OrganisationUnitStore.create(databaseAdapter()).insert(orgunit);

        TrackedEntityType trackedEntityType = TrackedEntityType.builder().uid(teiTypeUid).build();
        TrackedEntityTypeStore.create(databaseAdapter()).insert(trackedEntityType);

        TrackedEntityInstanceStore teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter());
        TrackedEntityInstance trackedEntityInstance = TrackedEntityInstance.builder()
                .uid(teiUid)
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit(orgunitUid)
                .trackedEntityType(teiTypeUid)
                .build();

        teiStore.insert(trackedEntityInstance);

        ContentValues categoryCombo = CreateCategoryComboUtils.create(1L, CategoryComboModel.DEFAULT_UID);
        database().insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo);

        Access access = Access.create(true, null, null, null, null, null,
                DataAccess.create(true, true));
        Program program = Program.builder().uid(programUid)
                .access(access)
                .trackedEntityType(TrackedEntityType.builder().uid(teiTypeUid).build())
                .build();
        ProgramStore.create(databaseAdapter()).insert(program);

        ProgramStage stage1 = ProgramStage.builder().uid(programStage1).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build();
        ProgramStage stage2 = ProgramStage.builder().uid(programStage2).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build();

        IdentifiableObjectStore<ProgramStage> programStageStore = ProgramStageStore.create(databaseAdapter());
        programStageStore.insert(stage1);
        programStageStore.insert(stage2);

        DataElement de1 = DataElement.builder().uid(dataElement1).valueType(ValueType.NUMBER).build();
        DataElement de2 = DataElement.builder().uid(dataElement2).valueType(ValueType.NUMBER).build();
        IdentifiableObjectStore<DataElement> dataElementStore = DataElementStore.create(databaseAdapter());
        dataElementStore.insert(de1);
        dataElementStore.insert(de2);

        TrackedEntityAttribute tea = TrackedEntityAttribute.builder().uid(attribute1).build();
        TrackedEntityAttributeStore.create(databaseAdapter()).insert(tea);
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
        Date eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2018-05-21T00:00:00.000");

        createEnrollment(enrollmentDate, null);
        createEvent(event1, programStage1, eventDate);

        setProgramIndicatorExpressionAsAverage("d2:daysBetween(V{enrollment_date}, V{event_date})");

        String result = programIndicatorEngine.getProgramIndicatorValue(enrollmentUid, event1, programIndicatorUid);

        assertThat(result).isEqualTo("16");
    }

    private void createEnrollment(Date enrollmentDate, Date incidentDate) {
        Enrollment enrollment = Enrollment.builder().uid(enrollmentUid).organisationUnit(orgunitUid).program(programUid)
                .enrollmentDate(enrollmentDate).incidentDate(incidentDate).trackedEntityInstance(teiUid).build();
        EnrollmentStoreImpl.create(databaseAdapter()).insert(enrollment);
    }

    private void createEvent(String eventUid, String programStageUid, Date eventDate, Date lastUpdated) {
        Event event = Event.builder().uid(eventUid).enrollment(enrollmentUid).lastUpdated(lastUpdated)
                .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
                .eventDate(eventDate).build();

        EventStoreImpl.create(databaseAdapter()).insert(event);
    }

    private void createEvent(String eventUid, String programStageUid, Date eventDate) {
        createEvent(eventUid, programStageUid, eventDate, null);
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
        ProgramIndicatorStore.create(databaseAdapter()).insert(programIndicator);
    }

    private void insertTrackedEntityDataValue(String eventUid, String dataElementUid, String value) {
        TrackedEntityDataValue trackedEntityDataValue = TrackedEntityDataValue.builder()
                .event(eventUid)
                .dataElement(dataElementUid)
                .value(value).build();

        TrackedEntityDataValueStoreImpl.create(databaseAdapter()).insert(trackedEntityDataValue);
    }

    private void insertTrackedEntityAttributeValue(String attributeUid, String value) {
        TrackedEntityAttributeValue trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
                .value(value).trackedEntityAttribute(attributeUid).trackedEntityInstance(teiUid).build();
        TrackedEntityAttributeValueStoreImpl.create(databaseAdapter()).insert(trackedEntityAttributeValue);
    }

    private String de(String programStageUid, String dataElementUid) {
        return "#{" + programStageUid + "." + dataElementUid + "}";
    }

    private String att(String attributeUid) {
        return "A{" + attributeUid + "}";
    }

    private Date today() {
        return new Date();
    }

    private Date twoDaysBefore() {
        Long newTime = (new Date()).getTime() - (2 * 24 * 60 * 60 * 1000);
        return new Date(newTime);
    }
}