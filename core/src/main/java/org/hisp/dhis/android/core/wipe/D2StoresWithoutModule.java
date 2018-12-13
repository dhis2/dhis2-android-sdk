package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.ObjectStyleStoreImpl;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
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
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroupStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeStore;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD.ExcessiveImports")
final class D2StoresWithoutModule {
    final List<DeletableStore> metadataStores;
    final List<DeletableStore> dataStores;

    D2StoresWithoutModule(DatabaseAdapter databaseAdapter) {
        this.metadataStores = Arrays.asList(
                OrganisationUnitStore.create(databaseAdapter),
                new ResourceStoreImpl(databaseAdapter),
                ProgramStore.create(databaseAdapter),

                new TrackedEntityAttributeStoreImpl(databaseAdapter),
                ProgramTrackedEntityAttributeStore.create(databaseAdapter),
                new ProgramRuleVariableStoreImpl(databaseAdapter),
                ProgramIndicatorStore.create(databaseAdapter),
                ProgramStageSectionProgramIndicatorLinkStore.create(databaseAdapter),
                new ProgramRuleActionStoreImpl(databaseAdapter),
                ProgramRuleStore.create(databaseAdapter),
                OptionStore.create(databaseAdapter),
                OptionSetStore.create(databaseAdapter),
                new ProgramStageDataElementStoreImpl(databaseAdapter),

                ProgramStageSectionStore.create(databaseAdapter),
                ProgramStageStore.create(databaseAdapter),
                TrackedEntityTypeStore.create(databaseAdapter),
                OrganisationUnitProgramLinkStore.create(databaseAdapter),

                IndicatorStore.create(databaseAdapter),

                IndicatorTypeStore.create(databaseAdapter),
                DataSetIndicatorLinkStore.create(databaseAdapter),
                PeriodStore.create(databaseAdapter),
                ObjectStyleStoreImpl.create(databaseAdapter),
                ValueTypeDeviceRenderingStore.create(databaseAdapter),
                LegendStore.create(databaseAdapter),
                LegendSetStore.create(databaseAdapter),

                ProgramIndicatorLegendSetLinkStore.create(databaseAdapter),
                OrganisationUnitGroupStore.create(databaseAdapter),
                OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter)
        );

        this.dataStores = Arrays.asList(
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                TrackedEntityDataValueStoreImpl.create(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter),
                EventStoreImpl.create(databaseAdapter),
                DataValueStore.create(databaseAdapter),
                NoteStore.create(databaseAdapter),
                TrackedEntityAttributeReservedValueStore.create(databaseAdapter)
        );
    }
}