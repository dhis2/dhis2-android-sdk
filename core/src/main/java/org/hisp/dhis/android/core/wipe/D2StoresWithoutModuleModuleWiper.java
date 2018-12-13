package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.ObjectStyleTableInfo;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkModel;
import org.hisp.dhis.android.core.period.PeriodModel;

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
        tableWiper.wipeTables(PeriodModel.TABLE,
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
                EnrollmentModel.TABLE,
                EventTableInfo.TABLE_INFO.name(),
                NoteTableInfo.TABLE_INFO.name()
        );
    }
}