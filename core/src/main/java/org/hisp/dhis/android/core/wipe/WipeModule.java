package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.Unit;

public interface WipeModule {
    Unit wipeEverything() throws D2CallException;

    Unit wipeMetadata() throws D2CallException;

    Unit wipeData() throws D2CallException;
}