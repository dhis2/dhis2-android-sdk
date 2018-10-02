package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.ObjectStyleStore;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingStore;
import org.hisp.dhis.android.core.common.WipeableModule;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementOperandStore;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataset.DataInputPeriodStore;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.dataset.SectionDataElementLinkStore;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLinkStore;
import org.hisp.dhis.android.core.dataset.SectionStore;
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
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.settings.SystemSettingStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserRoleStoreImpl;
import org.hisp.dhis.android.core.user.UserStore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("PMD.ExcessiveImports")
public final class WipeModule {
    private final DatabaseAdapter databaseAdapter;

    @NonNull
    private final List<DeletableStore> metadataStores;
    private final List<DeletableStore> dataStores;
    private final WipeableModule[] wipeableModules;
    private final D2CallExecutor executor = new D2CallExecutor();

    WipeModule(@NonNull DatabaseAdapter databaseAdapter,
               @NonNull List<DeletableStore> metadataStores,
               @NonNull List<DeletableStore> dataStores,
               WipeableModule... wipeableModules) {
        this.databaseAdapter = databaseAdapter;
        this.metadataStores = metadataStores;
        this.dataStores = dataStores;
        this.wipeableModules = wipeableModules;
    }

    public Unit wipeEverything() throws D2CallException {
        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeMetadataInternal();
                wipeDataInternal();

                return new Unit();
            }
        });
    }

    public Unit wipeMetadata() throws D2CallException {
        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeMetadataInternal();

                return new Unit();
            }
        });
    }

    public Unit wipeData() throws D2CallException {
        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeDataInternal();

                return new Unit();
            }
        });
    }

    private void wipeMetadataInternal() {
        for (DeletableStore deletableStore : metadataStores) {
            deletableStore.delete();
        }

        for (WipeableModule wipeableModule : wipeableModules) {
            wipeableModule.wipeMetadata();
        }
    }

    private void wipeDataInternal() {
        for (DeletableStore deletableStore : dataStores) {
            deletableStore.delete();
        }

        for (WipeableModule wipeableModule : wipeableModules) {
            wipeableModule.wipeData();
        }
    }

    public static WipeModule create(DatabaseAdapter databaseAdapter, D2InternalModules internalModules) {

        List<DeletableStore> metadataStores = Arrays.asList(
                UserStore.create(databaseAdapter),
                new UserCredentialsStoreImpl(databaseAdapter),
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                new ResourceStoreImpl(databaseAdapter),
                new UserRoleStoreImpl(databaseAdapter),
                ProgramStore.create(databaseAdapter),

                new ProgramRuleVariableStoreImpl(databaseAdapter),
                ProgramIndicatorStore.create(databaseAdapter),
                ProgramStageSectionProgramIndicatorLinkStore.create(databaseAdapter),
                new ProgramRuleActionStoreImpl(databaseAdapter),
                ProgramRuleStore.create(databaseAdapter),
                OptionStore.create(databaseAdapter),
                OptionSetStore.create(databaseAdapter),
                DataElementStore.create(databaseAdapter),
                new ProgramStageDataElementStoreImpl(databaseAdapter),

                new ProgramStageSectionStoreImpl(databaseAdapter),
                ProgramStageStore.create(databaseAdapter),
                TrackedEntityTypeStore.create(databaseAdapter),
                OrganisationUnitProgramLinkStore.create(databaseAdapter),

                CategoryStore.create(databaseAdapter),
                CategoryOptionStore.create(databaseAdapter),
                CategoryOptionComboStore.create(databaseAdapter),
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
                PeriodStore.create(databaseAdapter),
                ObjectStyleStore.create(databaseAdapter),
                ValueTypeDeviceRenderingStore.create(databaseAdapter),
                LegendStore.create(databaseAdapter),
                LegendSetStore.create(databaseAdapter),

                ProgramIndicatorLegendSetLinkStore.create(databaseAdapter),
                SystemSettingStore.create(databaseAdapter),
                SectionStore.create(databaseAdapter),
                SectionDataElementLinkStore.create(databaseAdapter),
                SectionGreyedFieldsLinkStore.create(databaseAdapter),
                DataSetCompulsoryDataElementOperandLinkStore.create(databaseAdapter),
                DataElementOperandStore.create(databaseAdapter),
                DataSetCompulsoryDataElementOperandLinkStore.create(databaseAdapter),
                DataInputPeriodStore.create(databaseAdapter),
                OrganisationUnitGroupStore.create(databaseAdapter),
                OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter),
                DataSetCompleteRegistrationStore.create(databaseAdapter)
        );

        List<DeletableStore> dataStores = Arrays.asList(
                new TrackedEntityAttributeStoreImpl(databaseAdapter),
                ProgramTrackedEntityAttributeStore.create(databaseAdapter),
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter),
                new EventStoreImpl(databaseAdapter),
                DataValueStore.create(databaseAdapter),
                NoteStore.create(databaseAdapter),
                TrackedEntityAttributeReservedValueStore.create(databaseAdapter)
        );

        return new WipeModule(databaseAdapter, metadataStores, dataStores,
                internalModules.systemInfo, internalModules.relationshipModule);
    }
}