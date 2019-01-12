package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

public interface WipeModule {
    Unit wipeEverything() throws D2Error;

    Unit wipeMetadata() throws D2Error;

    Unit wipeData() throws D2Error;
}