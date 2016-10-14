package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.Application;
import android.content.Context;

import org.hisp.dhis.client.sdk.utils.Logger;

public interface AppComponent {
    Logger logger();

    Context context();

    Application application();
}
