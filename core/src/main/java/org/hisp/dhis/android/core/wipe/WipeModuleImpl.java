package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.List;
import java.util.concurrent.Callable;

final class WipeModuleImpl implements WipeModule {

    private final D2CallExecutor d2CallExecutor;
    private final List<ModuleWiper> moduleWipers;

    WipeModuleImpl(D2CallExecutor d2CallExecutor,
                   List<ModuleWiper> moduleWipers) {
        this.d2CallExecutor = d2CallExecutor;
        this.moduleWipers = moduleWipers;
    }

    @Override
    public Unit wipeEverything() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeMetadataInternal();
                wipeDataInternal();

                return new Unit();
            }
        });
    }

    @Override
    public Unit wipeMetadata() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeMetadataInternal();

                return new Unit();
            }
        });
    }

    @Override
    public Unit wipeData() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() {
                wipeDataInternal();

                return new Unit();
            }
        });
    }

    private void wipeMetadataInternal() {
        for (ModuleWiper moduleWiper : moduleWipers) {
            moduleWiper.wipeMetadata();
        }
    }

    private void wipeDataInternal() {
        for (ModuleWiper moduleWiper : moduleWipers) {
            moduleWiper.wipeData();
        }
    }
}