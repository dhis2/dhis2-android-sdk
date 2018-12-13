package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;
import org.hisp.dhis.android.core.event.EventTableInfo;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class D2StoresWithoutModuleModuleWiper implements ModuleWiper {
    private final TableWiper tableWiper;

    @Inject
    D2StoresWithoutModuleModuleWiper(TableWiper tableWiper) {
        this.tableWiper = tableWiper;
    }

    @Override
    public void wipeMetadata() {
        // No metadata to wipe
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