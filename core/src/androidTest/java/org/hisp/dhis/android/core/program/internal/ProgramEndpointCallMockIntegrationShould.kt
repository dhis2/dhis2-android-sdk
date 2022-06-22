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
package org.hisp.dhis.android.core.program.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.category.CategoryComboTableInfo
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.data.program.ProgramRuleVariableSamples
import org.hisp.dhis.android.core.data.program.ProgramSamples
import org.hisp.dhis.android.core.data.program.ProgramTrackedEntityAttributeSamples
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo
import org.hisp.dhis.android.core.program.CreateProgramStageUtils
import org.hisp.dhis.android.core.program.ProgramStageTableInfo
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityAttributeUtils
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.util.collections.Sets

@RunWith(D2JunitRunner::class)
class ProgramEndpointCallMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun persist_program_when_call() {
        val store = ProgramStore.create(databaseAdapter)

        assertThat(store.count()).isEqualTo(2)
        assertThat(store.selectByUid(programUid)!!.toBuilder().id(null).build())
            .isEqualTo(ProgramSamples.getAntenatalProgram())
    }

    @Test
    fun persist_program_rule_variables_on_call() {
        val store = ProgramRuleVariableStore.create(databaseAdapter)

        assertThat(store.count()).isEqualTo(2)
        assertThat(store.selectByUid("omrL0gtPpDL")).isEqualTo(ProgramRuleVariableSamples.getHemoglobin())
    }

    @Test
    fun persist_program_tracker_entity_attributes_when_call() {
        val store = ProgramTrackedEntityAttributeStore.create(databaseAdapter)

        assertThat(store.count()).isEqualTo(2)
        assertThat(store.selectByUid("YhqgQ6Iy4c4"))
            .isEqualTo(ProgramTrackedEntityAttributeSamples.getChildProgrammeGender())
    }

    @Test
    fun not_persist_relationship_type_when_call() {
        val store = RelationshipTypeStore.create(databaseAdapter)
        assertThat(store.count()).isEqualTo(0)
    }

    companion object {
        private const val programUid = "lxAQ7Zs9VYR"

        @BeforeClass
        @JvmStatic
        fun setUpTestClass() {
            setUpClass()

            val executor = D2CallExecutor.create(databaseAdapter)
            executor.executeD2CallTransactionally {
                val categoryComboUid = "m2jTvAj5kkm"
                val categoryCombo = CreateCategoryComboUtils.create(1L, categoryComboUid)
                databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo)

                // inserting tracked entity
                val trackedEntityType = CreateTrackedEntityUtils.create(1L, "nEenWmSyUEp")
                databaseAdapter.insert(TrackedEntityTypeTableInfo.TABLE_INFO.name(), null, trackedEntityType)

                // inserting tracked entity attributes
                val trackedEntityAttribute1 = CreateTrackedEntityAttributeUtils.create(1L, "aejWyOfXge6", null)
                databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute1)
                val trackedEntityAttribute2 = CreateTrackedEntityAttributeUtils.create(2L, "cejWyOfXge6", null)
                databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute2)
                val dataElement1 = CreateDataElementUtils.create(1L, "vANAXwtLwcT", categoryComboUid, null)
                databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement1)
                val dataElement2 = CreateDataElementUtils.create(2L, "sWoqcoByYmD", categoryComboUid, null)
                databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement2)
                val programStage = CreateProgramStageUtils.create(1L, "dBwrot7S420", programUid)
                databaseAdapter.insert(ProgramStageTableInfo.TABLE_INFO.name(), null, programStage)
                dhis2MockServer.enqueueMockResponse("program/programs.json")
                val programCall = objects.d2DIComponent.programCall().download(Sets.newSet(programUid))

                programCall.blockingGet()

                Unit()
            }
        }
    }
}
