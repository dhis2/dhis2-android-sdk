package org.hisp.dhis.android.core.calls;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.Unit;

interface WipeModule {
    public Unit wipeEverything() throws D2CallException;

    public Unit wipeMetadata() throws D2CallException;

    public Unit wipeData() throws D2CallException;
}