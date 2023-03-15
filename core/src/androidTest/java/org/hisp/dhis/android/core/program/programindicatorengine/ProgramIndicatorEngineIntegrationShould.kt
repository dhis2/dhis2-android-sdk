/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.program.programindicatorengine

import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryComboTableInfo
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.att
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.today
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.twoDaysBefore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgramIndicatorEngineIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    private lateinit var programIndicatorEngine: ProgramIndicatorEngine

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    companion object Factory {

        private const val teiUid = "H87GEVeG3JH"
        private const val enrollmentUid = "la16vwCoFM8"
        private const val event1 = "gphKB0UjOrX"
        private const val event2 = "EAZOUgr2Ksv"
        private const val event3 = "BVL4LcEEDdU"
        private const val dataElement1 = "ddaBs9lgZyP"
        private const val dataElement2 = "Kb9hZ428FyH"
        private const val attribute1 = "Kmtdopp5GC1"
        private const val programIndicatorUid = "rg3JkCv0skl"

        // Auxiliary variables
        private const val orgunitUid = "orgunit_uid"
        private const val teiTypeUid = "tei_type_uid"
        private const val programUid = "program_uid"
        private const val programStage1 = "iM4svLr2hlO"
        private const val programStage2 = "RXFTSe1oefv"

        @BeforeClass
        @JvmStatic
        @Throws(Exception::class)
        fun setUp() {
            setUpClass()

            val orgunit = OrganisationUnit.builder().uid(orgunitUid).build()
            OrganisationUnitStore.create(databaseAdapter).insert(orgunit)

            val trackedEntityType = TrackedEntityType.builder().uid(teiTypeUid).build()
            TrackedEntityTypeStore.create(databaseAdapter).insert(trackedEntityType)

            val categoryCombo = CreateCategoryComboUtils.create(1L, CategoryCombo.DEFAULT_UID)
            databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo)

            val access = Access.create(true, false, DataAccess.create(true, true))
            val program = Program.builder().uid(programUid)
                .access(access)
                .trackedEntityType(TrackedEntityType.builder().uid(teiTypeUid).build())
                .build()
            ProgramStore.create(databaseAdapter).insert(program)

            val stage1 = ProgramStage.builder().uid(programStage1).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build()
            val stage2 = ProgramStage.builder().uid(programStage2).program(ObjectWithUid.create(programUid))
                .formType(FormType.CUSTOM).build()
            val programStageStore = ProgramStageStore.create(databaseAdapter)
            programStageStore.insert(stage1)
            programStageStore.insert(stage2)

            val de1 = DataElement.builder().uid(dataElement1).valueType(ValueType.NUMBER).build()
            val de2 = DataElement.builder().uid(dataElement2).valueType(ValueType.NUMBER).build()
            val dataElementStore = DataElementStore.create(databaseAdapter)
            dataElementStore.insert(de1)
            dataElementStore.insert(de2)

            val tea = TrackedEntityAttribute.builder().uid(attribute1).build()
            TrackedEntityAttributeStore.create(databaseAdapter).insert(tea)
        }

        @AfterClass
        @JvmStatic
        @Throws(D2Error::class)
        fun tearDownClass() {
            d2.wipeModule().wipeEverything()
        }
    }

    @Before
    @Throws(Exception::class)
    fun setUpTest() {
        programIndicatorEngine = d2.programModule().programIndicatorEngine()

        helper.createTrackedEntity(teiUid, orgunitUid, teiTypeUid)
    }

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun evaluate_single_dataelement() {
        createEnrollment()
        createTrackerEvent(eventUid = event1, programStageUid = programStage1, eventDate = Date())
        insertTrackedEntityDataValue(event1, dataElement1, "4")
        setProgramIndicatorExpression(de(programStage1, dataElement1))
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("4")
    }

    @Test
    fun evaluate_single_text_dataelement() {
        createEnrollment()
        createTrackerEvent(eventUid = event1, programStageUid = programStage1, eventDate = Date())
        insertTrackedEntityDataValue(event1, dataElement1, "text data-value")
        setProgramIndicatorExpression(de(programStage1, dataElement1))
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("text data-value")
    }

    @Test
    fun evaluate_addition_two_dataelement() {
        createEnrollment()
        createTrackerEvent(eventUid = event1, programStageUid = programStage1)
        insertTrackedEntityDataValue(event1, dataElement1, "5")
        insertTrackedEntityDataValue(event1, dataElement2, "3")
        setProgramIndicatorExpression("${de(programStage1, dataElement1)} * ${de(programStage1, dataElement2)}")
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("15")
    }

    @Test
    fun evaluate_division_two_dataelement() {
        createEnrollment()
        createTrackerEvent(eventUid = event1, programStageUid = programStage1)
        insertTrackedEntityDataValue(event1, dataElement1, "3")
        insertTrackedEntityDataValue(event1, dataElement2, "5")
        setProgramIndicatorExpression("${de(programStage1, dataElement1)} / ${de(programStage1, dataElement2)}")
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("0.6")
    }

    @Test
    fun evaluate_last_value_in_repeatable_stages() {
        createEnrollment()
        createTrackerEvent(
            eventUid = event1, programStageUid = programStage1,
            eventDate = twoDaysBefore(), lastUpdated = today()
        )
        createTrackerEvent(
            eventUid = event2, programStageUid = programStage1,
            eventDate = today(), lastUpdated = today()
        )
        createTrackerEvent(
            eventUid = event3, programStageUid = programStage1,
            eventDate = twoDaysBefore(), lastUpdated = today()
        )
        insertTrackedEntityDataValue(event1, dataElement1, "1")
        insertTrackedEntityDataValue(event2, dataElement1, "2") // Expected as last value
        insertTrackedEntityDataValue(event3, dataElement1, "3")
        setProgramIndicatorExpression(de(programStage1, dataElement1))
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun evaluate_last_value_indicators_same_date() {
        createEnrollment()
        val eventDate = twoDaysBefore()
        createTrackerEvent(
            eventUid = event1, programStageUid = programStage1,
            eventDate = eventDate, lastUpdated = twoDaysBefore()
        )
        createTrackerEvent(
            eventUid = event2, programStageUid = programStage1,
            eventDate = eventDate, lastUpdated = today()
        )
        createTrackerEvent(
            eventUid = event3, programStageUid = programStage1,
            eventDate = eventDate, lastUpdated = twoDaysBefore()
        )
        insertTrackedEntityDataValue(event1, dataElement1, "1")
        insertTrackedEntityDataValue(event2, dataElement1, "2") // Expected as last value
        insertTrackedEntityDataValue(event3, dataElement1, "3")
        setProgramIndicatorExpression(de(programStage1, dataElement1))
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun evaluate_operation_several_stages() {
        createEnrollment()
        createTrackerEvent(event1, programStage1)
        createTrackerEvent(event2, programStage2)
        insertTrackedEntityDataValue(event1, dataElement1, "5")
        insertTrackedEntityDataValue(event2, dataElement2, "1.5")
        insertTrackedEntityAttributeValue(attribute1, "2")
        setProgramIndicatorExpression(
            "(${de(programStage1, dataElement1)} + ${de(programStage2, dataElement2)})" +
                " / ${att(attribute1)}"
        )
        val enrollmentValue = programIndicatorEngine.getEnrollmentProgramIndicatorValue(
            enrollmentUid,
            programIndicatorUid
        )
        val event1Value = programIndicatorEngine.getEventProgramIndicatorValue(event1, programIndicatorUid)
        val event2Value = programIndicatorEngine.getEventProgramIndicatorValue(event2, programIndicatorUid)
        assertThat(enrollmentValue).isEqualTo("3.25")
        assertThat(event1Value).isEqualTo("2.5")
        assertThat(event2Value).isEqualTo("0.75")
    }

    @Test
    fun evaluate_event_count_variable() {
        createEnrollment()
        createTrackerEvent(event1, programStage1)
        createTrackerEvent(event2, programStage2)
        createTrackerEvent(event3, programStage2, deleted = true)
        setProgramIndicatorExpression(`var`("event_count"))
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(
            enrollmentUid,
            programIndicatorUid
        )
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun evaluate_expression_with_d2_functions() {
        createEnrollment()
        createTrackerEvent(event1, programStage1)
        insertTrackedEntityDataValue(event1, dataElement1, "4.8")
        insertTrackedEntityDataValue(event1, dataElement2, "3")
        setProgramIndicatorExpression(
            "d2:round(${de(programStage1, dataElement1)}) * ${de(programStage1, dataElement2)}"
        )
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("15")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_d2_functions_with_dates() {
        val enrollmentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2018-05-05T00:00:00.000")
        val incidentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2018-05-21T00:00:00.000")
        createEnrollment(enrollmentDate, incidentDate)
        setProgramIndicatorExpression("d2:daysBetween(V{enrollment_date}, V{incident_date})")
        val result = programIndicatorEngine.getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
        assertThat(result).isEqualTo("16")
    }

    @Test
    fun evaluate_single_event() {
        createSingleEvent(eventUid = event1, programStageUid = programStage1)
        insertTrackedEntityDataValue(event1, dataElement1, "3.0")
        insertTrackedEntityDataValue(event1, dataElement2, "4.0")
        setProgramIndicatorExpression("${de(programStage1, dataElement1)} + ${de(programStage1, dataElement2)}")
        val result = programIndicatorEngine.getEventProgramIndicatorValue(event1, programIndicatorUid)
        assertThat(result).isEqualTo("7")
    }

    private fun createEnrollment(enrollmentDate: Date? = null, incidentDate: Date? = null) {
        helper.createEnrollment(teiUid, enrollmentUid, programUid, orgunitUid, enrollmentDate, incidentDate)
    }

    private fun createTrackerEvent(
        eventUid: String,
        programStageUid: String,
        deleted: Boolean = false,
        eventDate: Date? = null,
        lastUpdated: Date? = null
    ) {
        helper.createEvent(
            eventUid = eventUid,
            programUid = programUid,
            programStageUid = programStageUid,
            enrollmentUid = enrollmentUid,
            orgunitUid = orgunitUid,
            deleted = deleted,
            eventDate = eventDate,
            lastUpdated = lastUpdated
        )
    }

    private fun createSingleEvent(
        eventUid: String,
        programStageUid: String,
        deleted: Boolean = false,
        eventDate: Date? = null,
        lastUpdated: Date? = null
    ) {
        helper.createEvent(
            eventUid = eventUid,
            programUid = programUid,
            programStageUid = programStageUid,
            enrollmentUid = null,
            orgunitUid = orgunitUid,
            deleted = deleted,
            eventDate = eventDate,
            lastUpdated = lastUpdated
        )
    }

    private fun setProgramIndicatorExpression(expression: String) {
        insertProgramIndicator(expression, AggregationType.AVERAGE)
    }

    private fun insertProgramIndicator(expression: String, aggregationType: AggregationType) {
        helper.insertProgramIndicator(programIndicatorUid, programUid, expression, aggregationType)
    }

    private fun insertTrackedEntityDataValue(eventUid: String, dataElementUid: String, value: String) {
        helper.insertTrackedEntityDataValue(eventUid, dataElementUid, value)
    }

    private fun insertTrackedEntityAttributeValue(attributeUid: String, value: String) {
        helper.insertTrackedEntityAttributeValue(teiUid, attributeUid, value)
    }
}
