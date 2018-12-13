package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.wipe.ModuleWiper;
import org.hisp.dhis.android.core.wipe.TableWiper;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EventModuleWiper implements ModuleWiper {
    private final TableWiper tableWiper;

    @Inject
    EventModuleWiper(TableWiper tableWiper) {
        this.tableWiper = tableWiper;
    }

    @Override
    public void wipeMetadata() {
        // No metadata to wipe
    }

    @Override
    public void wipeData() {
        tableWiper.wipeTable(EventTableInfo.TABLE_INFO);
    }
}