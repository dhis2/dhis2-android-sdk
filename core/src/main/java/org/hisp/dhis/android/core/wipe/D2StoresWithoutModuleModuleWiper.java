package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.ObjectStyleTableInfo;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo;
import org.hisp.dhis.android.core.indicator.IndicatorTypeTableInfo;
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkModel;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.OptionTableInfo;
import org.hisp.dhis.android.core.period.PeriodModel;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleActionModel;
import org.hisp.dhis.android.core.program.ProgramRuleModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo;

import javax.inject.Inject;

import dagger.Reusable;

@SuppressWarnings("PMD.ExcessiveImports")
@Reusable
final class D2StoresWithoutModuleModuleWiper implements ModuleWiper {
    private final TableWiper tableWiper;

    @Inject
    D2StoresWithoutModuleModuleWiper(TableWiper tableWiper) {
        this.tableWiper = tableWiper;
    }

    @Override
    public void wipeMetadata() {
        tableWiper.wipeTables(
                ResourceModel.TABLE,
                ProgramTableInfo.TABLE_INFO.name(),
                TrackedEntityAttributeModel.TABLE,
                ProgramTrackedEntityAttributeModel.TABLE,

                ProgramRuleVariableModel.TABLE,
                ProgramIndicatorTableInfo.TABLE_INFO.name(),
                ProgramStageSectionProgramIndicatorLinkModel.TABLE,
                ProgramRuleActionModel.TABLE,
                ProgramRuleModel.TABLE,

                OptionTableInfo.TABLE_INFO.name(),
                OptionSetTableInfo.TABLE_INFO.name(),
                ProgramStageDataElementModel.TABLE,
                ProgramStageSectionTableInfo.TABLE_INFO.name(),
                ProgramStageModel.TABLE,

                TrackedEntityTypeTableInfo.TABLE_INFO.name(),
                IndicatorTableInfo.TABLE_INFO.name(),
                IndicatorTypeTableInfo.TABLE_INFO.name(),
                DataSetIndicatorLinkModel.TABLE,

                PeriodModel.TABLE,
                ObjectStyleTableInfo.TABLE_INFO.name(),
                ValueTypeDeviceRenderingModel.TABLE,
                LegendTableInfo.TABLE_INFO.name(),
                LegendSetTableInfo.TABLE_INFO.name(),

                ProgramIndicatorLegendSetLinkModel.TABLE
        );
    }

    @Override
    public void wipeData() {
        tableWiper.wipeTables(
                TrackedEntityInstanceModel.TABLE,
                EnrollmentModel.TABLE,
                TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                TrackedEntityAttributeValueModel.TABLE,
                EventTableInfo.TABLE_INFO.name(),

                NoteTableInfo.TABLE_INFO.name(),
                TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name()
        );
    }
}