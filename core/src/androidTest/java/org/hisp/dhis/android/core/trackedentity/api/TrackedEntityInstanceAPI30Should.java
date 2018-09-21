package org.hisp.dhis.android.core.trackedentity.api;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceAPI30Should extends TrackedEntityInstanceAPIShould {

    public TrackedEntityInstanceAPI30Should() {
        super(RealServerMother.android_current, "SYNC");
    }

}
