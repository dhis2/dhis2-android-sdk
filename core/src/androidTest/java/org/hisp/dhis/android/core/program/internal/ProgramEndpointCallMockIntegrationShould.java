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
import android.database.Cursor;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.data.program.ProgramRuleVariableSamples;
import org.hisp.dhis.android.core.data.program.ProgramSamples;
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo;
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
import static org.hisp.dhis.android.core.common.IdentifiableColumns.UID;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(D2JunitRunner.class)
public class ProgramEndpointCallMockIntegrationShould extends BaseMockIntegrationTestEmptyEnqueable {

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestEmptyEnqueable.setUpClass();

        ContentValues categoryCombo = CreateCategoryComboUtils.create(1L, "nM3u9s5a52V");
        databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo);

        ContentValues categoryCombo2 = CreateCategoryComboUtils.create(2L, "x31y45jvIQL");
        databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo2);

        // inserting tracked entity
        ContentValues trackedEntityType = CreateTrackedEntityUtils.create(1L, "nEenWmSyUEp");
        databaseAdapter.insert(TrackedEntityTypeTableInfo.TABLE_INFO.name(), null, trackedEntityType);

        // inserting tracked entity attributes
        ContentValues trackedEntityAttribute1 = CreateTrackedEntityAttributeUtils.create(1L, "w75KJ2mc4zz", null);
        databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute1);

        ContentValues trackedEntityAttribute2 = CreateTrackedEntityAttributeUtils.create(2L, "zDhUuAYrxNC", null);
        databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute2);

        ContentValues trackedEntityAttribute3 = CreateTrackedEntityAttributeUtils.create(3L, "cejWyOfXge6", null);
        databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute3);

        Single<List<Program>> programCall = objects.d2DIComponent.programCall().download(Sets.newSet("IpHINAT79UW"));

        dhis2MockServer.enqueueMockResponse("program/programs_complete.json");

        programCall.blockingGet();
    }

    @Test
    public void persist_program_when_call() {
        ProgramStoreInterface programStore = ProgramStore.create(databaseAdapter);
        assertThat(programStore.count()).isEqualTo(1);
        assertThat(programStore.selectFirst()).isEqualTo(ProgramSamples.getChildProgram());
    }

    @Test
    public void persist_program_rule_variables_on_call() {
        IdentifiableObjectStore<ProgramRuleVariable> programRuleVariableStore = ProgramRuleVariableStore.create(databaseAdapter);
        assertThat(programRuleVariableStore.count()).isEqualTo(2);
        assertThat(programRuleVariableStore.selectByUid("omrL0gtPpDL")).isEqualTo(ProgramRuleVariableSamples.getHemoglobin());
    }

    @Test
    public void persist_program_tracker_entity_attributes_when_call() {
        String[] projection = {
                UID,
                ProgramTrackedEntityAttributeTableInfo.Columns.CODE,
                ProgramTrackedEntityAttributeTableInfo.Columns.NAME,
                ProgramTrackedEntityAttributeTableInfo.Columns.DISPLAY_NAME,
                ProgramTrackedEntityAttributeTableInfo.Columns.CREATED,
                ProgramTrackedEntityAttributeTableInfo.Columns.LAST_UPDATED,
                ProgramTrackedEntityAttributeTableInfo.Columns.SHORT_NAME,
                ProgramTrackedEntityAttributeTableInfo.Columns.DISPLAY_SHORT_NAME,
                ProgramTrackedEntityAttributeTableInfo.Columns.DESCRIPTION,
                ProgramTrackedEntityAttributeTableInfo.Columns.DISPLAY_DESCRIPTION,
                ProgramTrackedEntityAttributeTableInfo.Columns.MANDATORY,
                ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramTrackedEntityAttributeTableInfo.Columns.ALLOW_FUTURE_DATE,
                ProgramTrackedEntityAttributeTableInfo.Columns.DISPLAY_IN_LIST,
                ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM,
                ProgramTrackedEntityAttributeTableInfo.Columns.SORT_ORDER
        };

        Cursor programTrackedEntityAttributeCursor = databaseAdapter.query(
                ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name(),
                projection,
                UID + "=?",
                new String[]{"l2T72XzBCLd"});

        assertThatCursor(programTrackedEntityAttributeCursor).hasRow(
                "l2T72XzBCLd",
                null,
                "Child Programme First name",
                "Child Programme First name",
                "2017-01-26T19:39:33.347",
                "2017-01-26T19:39:33.347",
                "Child Programme First name",
                "Child Programme First name",
                null,
                null,
                0, // false
                "w75KJ2mc4zz",
                0, // false
                1, // true
                "IpHINAT79UW",
                99
        ).isExhausted();
    }

    @Test
    public void persist_program_indicators_when_call() {
        Cursor programIndicatorCursor = databaseAdapter.query(
                ProgramIndicatorTableInfo.TABLE_INFO.name(),
                ProgramIndicatorTableInfo.TABLE_INFO.columns().all(),
                UID + "=?", new String[]{"rXoaHGAXWy9"});
        assertThatCursor(programIndicatorCursor).hasRow(
                "rXoaHGAXWy9",
                null,
                "Health immunization score",
                "Health immunization score",
                "2015-10-20T11:26:19.631",
                "2015-10-20T11:26:19.631",
                "Health immunization score",
                "Health immunization score",
                "Sum of BCG doses, measles doses and yellow fever doses." +
                        " If Apgar score over or equal to 2, multiply by 2.",
                "Sum of BCG doses, measles doses and yellow fever doses." +
                        " If Apgar score over or equal to 2, multiply by 2.",
                0, // false
                "(#{A03MvHHogjR.bx6fsa0t90x} +  #{A03MvHHogjR.FqlgKAG8HOu} + #{A03MvHHogjR.rxBfISxXS2U}) " +
                        "* d2:condition('#{A03MvHHogjR.a3kGcGDCuk6} >= 2',1,2)",
                "rXoaHGAXWy9",
                null,
                2,
                "SUM",
                "IpHINAT79UW"
        ).isExhausted();
    }

    @Test
    public void persist_legend_sets_when_call() {
        Cursor programIndicatorCursor = databaseAdapter.query(
                LegendSetTableInfo.TABLE_INFO.name(),
                LegendSetTableInfo.TABLE_INFO.columns().all(),
                UID + "=?", new String[]{"TiOkbpGEud4"});
        assertThatCursor(programIndicatorCursor).hasRow(
                "TiOkbpGEud4",
                "AGE15YINT",
                "Age 15y interval",
                "Age 15y interval",
                "2017-06-02T11:40:33.452",
                "2017-06-02T11:41:01.999",
                "color"
        ).isExhausted();
    }

    @Test
    public void persist_legends_when_call() {
        Cursor programIndicatorCursor = databaseAdapter.query(
                LegendTableInfo.TABLE_INFO.name(),
                LegendTableInfo.TABLE_INFO.columns().all(),
                UID + "=?", new String[]{"ZUUGJnvX40X"});
        assertThatCursor(programIndicatorCursor).hasRow(
                "ZUUGJnvX40X",
                null,
                "30 - 40",
                "30 - 40",
                "2017-06-02T11:40:44.279",
                "2017-06-02T11:40:44.279",
                30.5,
                40,
                "#d9f0a3",
                "TiOkbpGEud4"
        ).isExhausted();
    }

    /**
     * Relationship type doesn't exist for the program in the payload. Therefore we'll need to check that it doesn't
     * exist in the database
     *
     */
    @Test
    public void not_persist_relationship_type_when_call() {
        Cursor relationshipTypeCursor = databaseAdapter.query(RelationshipTypeTableInfo.TABLE_INFO.name(), RelationshipTypeTableInfo.TABLE_INFO.columns().all());

        assertThatCursor(relationshipTypeCursor).isExhausted();
    }
}
