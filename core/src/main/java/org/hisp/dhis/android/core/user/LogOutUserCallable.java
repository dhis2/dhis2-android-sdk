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

package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.ObjectStyleStore;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.datavalue.DataValueStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.enrollment.note.NoteStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkStore;
import org.hisp.dhis.android.core.indicator.IndicatorStore;
import org.hisp.dhis.android.core.indicator.IndicatorTypeStore;
import org.hisp.dhis.android.core.legendset.LegendSetStore;
import org.hisp.dhis.android.core.legendset.LegendStore;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkStore;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.settings.SystemSettingStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeStoreImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("PMD.ExcessiveImports")
public class LogOutUserCallable implements Callable<Void> {

    @NonNull
    private final List<DeletableStore> deletableStores;

    LogOutUserCallable(@NonNull List<DeletableStore> deletableStores) {
        this.deletableStores = deletableStores;
    }

    @Override
    public Void call() throws Exception {
        // clear out all tables
        for (DeletableStore deletableStore : deletableStores) {
            deletableStore.delete();
        }
        return null;
    }



    public static LogOutUserCallable createToWipe(DatabaseAdapter databaseAdapter) {

        List<DeletableStore> deletableStores = Arrays.asList(
                UserStore.create(databaseAdapter),
                new UserCredentialsStoreImpl(databaseAdapter),
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                new AuthenticatedUserStoreImpl(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                new ResourceStoreImpl(databaseAdapter),
                SystemInfoStore.create(databaseAdapter),
                new UserRoleStoreImpl(databaseAdapter),
                ProgramStore.create(databaseAdapter),
                new TrackedEntityAttributeStoreImpl(databaseAdapter),

                ProgramTrackedEntityAttributeStore.create(databaseAdapter),
                new ProgramRuleVariableStoreImpl(databaseAdapter),
                ProgramIndicatorStore.create(databaseAdapter),
                ProgramStageSectionProgramIndicatorLinkStore.create(databaseAdapter),
                new ProgramRuleActionStoreImpl(databaseAdapter),
                new ProgramRuleStoreImpl(databaseAdapter),
                new OptionStoreImpl(databaseAdapter),
                OptionSetStore.create(databaseAdapter),
                DataElementStore.create(databaseAdapter),
                new ProgramStageDataElementStoreImpl(databaseAdapter),

                new ProgramStageSectionStoreImpl(databaseAdapter),
                ProgramStageStore.create(databaseAdapter),
                RelationshipStore.create(databaseAdapter),
                new TrackedEntityTypeStoreImpl(databaseAdapter),
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter),
                OrganisationUnitProgramLinkStore.create(databaseAdapter),
                new EventStoreImpl(databaseAdapter),

                new CategoryStoreImpl(databaseAdapter),
                CategoryOptionStore.create(databaseAdapter),
                CategoryCategoryOptionLinkStore.create(databaseAdapter),
                CategoryOptionComboCategoryOptionLinkStore.create(databaseAdapter),
                new CategoryComboStoreImpl(databaseAdapter),
                CategoryCategoryComboLinkStore.create(databaseAdapter),
                DataSetStore.create(databaseAdapter),
                DataSetDataElementLinkStore.create(databaseAdapter),
                DataSetOrganisationUnitLinkStore.create(databaseAdapter),
                IndicatorStore.create(databaseAdapter),

                IndicatorTypeStore.create(databaseAdapter),
                DataSetIndicatorLinkStore.create(databaseAdapter),
                DataValueStore.create(databaseAdapter),
                PeriodStore.create(databaseAdapter),
                ObjectStyleStore.create(databaseAdapter),
                ValueTypeDeviceRenderingStore.create(databaseAdapter),
                RelationshipTypeStore.create(databaseAdapter),
                NoteStore.create(databaseAdapter),
                LegendStore.create(databaseAdapter),
                LegendSetStore.create(databaseAdapter),

                ProgramIndicatorLegendSetLinkStore.create(databaseAdapter),
                SystemSettingStore.create(databaseAdapter),
                TrackedEntityAttributeReservedValueStore.create(databaseAdapter)
        );

        return new LogOutUserCallable(
                deletableStores
        );
    }

    public static LogOutUserCallable createToLogOut(DatabaseAdapter databaseAdapter) {
        List<DeletableStore> deletableStores = new ArrayList<>();
        deletableStores.add(new AuthenticatedUserStoreImpl(databaseAdapter));
        return new LogOutUserCallable(deletableStores);
    }
}
