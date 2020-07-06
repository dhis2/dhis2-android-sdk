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

package org.hisp.dhis.android.core.program.internal;

import android.content.ContentValues;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.legendset.LegendSamples;
import org.hisp.dhis.android.core.data.legendset.LegendSetSamples;
import org.hisp.dhis.android.core.data.program.ProgramIndicatorSamples;
import org.hisp.dhis.android.core.data.program.ProgramRuleVariableSamples;
import org.hisp.dhis.android.core.data.program.ProgramSamples;
import org.hisp.dhis.android.core.data.program.ProgramTrackedEntityAttributeSamples;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.legendset.Legend;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.internal.LegendSetStore;
import org.hisp.dhis.android.core.legendset.internal.LegendStore;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityAttributeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;

import java.util.List;

import io.reactivex.Single;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramEndpointCallMockIntegrationShould extends BaseMockIntegrationTestEmptyEnqueable {

    private static String programUid = "lxAQ7Zs9VYR";

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestEmptyEnqueable.setUpClass();

        D2CallExecutor executor = D2CallExecutor.create(databaseAdapter);

        executor.executeD2CallTransactionally(() -> {
            String categoryComboUid = "m2jTvAj5kkm";
            ContentValues categoryCombo = CreateCategoryComboUtils.create(1L, categoryComboUid);
            databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo);

            // inserting tracked entity
            ContentValues trackedEntityType = CreateTrackedEntityUtils.create(1L, "nEenWmSyUEp");
            databaseAdapter.insert(TrackedEntityTypeTableInfo.TABLE_INFO.name(), null, trackedEntityType);

            // inserting tracked entity attributes
            ContentValues trackedEntityAttribute1 = CreateTrackedEntityAttributeUtils.create(1L, "aejWyOfXge6", null);
            databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute1);

            ContentValues trackedEntityAttribute2 = CreateTrackedEntityAttributeUtils.create(2L, "cejWyOfXge6", null);
            databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute2);

            ContentValues dataElement1 = CreateDataElementUtils.create(1L, "vANAXwtLwcT", categoryComboUid, null);
            databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement1);

            ContentValues dataElement2 = CreateDataElementUtils.create(2L, "sWoqcoByYmD", categoryComboUid, null);
            databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement2);

            ContentValues programStage = CreateProgramStageUtils.create(1L, "dBwrot7S420", programUid);
            databaseAdapter.insert(ProgramStageTableInfo.TABLE_INFO.name(), null, programStage);

            dhis2MockServer.enqueueMockResponse("program/programs.json");

            Single<List<Program>> programCall = objects.d2DIComponent.programCall().download(Sets.newSet(programUid));

            programCall.blockingGet();

            return new Unit();
        });
    }

    @Test
    public void persist_program_when_call() {
        ProgramStoreInterface store = ProgramStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(2);
        assertThat(store.selectByUid(programUid).toBuilder().id(null).build())
                .isEqualTo(ProgramSamples.getAntenatalProgram());
    }

    @Test
    public void persist_program_rule_variables_on_call() {
        IdentifiableObjectStore<ProgramRuleVariable> store = ProgramRuleVariableStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(2);
        assertThat(store.selectByUid("omrL0gtPpDL")).isEqualTo(ProgramRuleVariableSamples.getHemoglobin());
    }

    @Test
    public void persist_program_tracker_entity_attributes_when_call() {
        IdentifiableObjectStore<ProgramTrackedEntityAttribute> store = ProgramTrackedEntityAttributeStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(2);
        assertThat(store.selectByUid("YhqgQ6Iy4c4")).isEqualTo(ProgramTrackedEntityAttributeSamples.getChildProgrammeGender());
    }

    @Test
    public void persist_program_indicators_when_call() {
        IdentifiableObjectStore<ProgramIndicator> store = ProgramIndicatorStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(2);
        assertThat(store.selectByUid("GSae40Fyppf")).isEqualTo(ProgramIndicatorSamples.getAgeAtVisit());
    }

    @Test
    public void persist_legend_sets_when_call() {
        IdentifiableObjectStore<LegendSet> store = LegendSetStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(1);
        assertThat(store.selectByUid("TiOkbpGEud4")).isEqualTo(LegendSetSamples.getAge15yInterval());
    }

    @Test
    public void persist_legends_when_call() {
        IdentifiableObjectStore<Legend> store = LegendStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(2);
        assertThat(store.selectByUid("BzQkRWHS7lu")).isEqualTo(LegendSamples.get45To60());
    }

    @Test
    public void not_persist_relationship_type_when_call() {
        IdentifiableObjectStore<RelationshipType> store = RelationshipTypeStore.create(databaseAdapter);
        assertThat(store.count()).isEqualTo(0);
    }
}
