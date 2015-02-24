package org.hisp.dhis2.android.sdk.persistence;

import android.app.Activity;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
public abstract interface IDhis2Application  {
    public abstract Class<? extends Activity> getMainActivity();
}
