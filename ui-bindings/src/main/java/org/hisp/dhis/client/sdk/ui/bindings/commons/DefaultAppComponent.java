package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.Application;
import android.content.Context;

import org.hisp.dhis.client.sdk.core.commons.LoggerImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class DefaultAppComponent implements AppComponent {
    private final Logger logger;
    private final Application application;

    public DefaultAppComponent(Application application) {
        this.application = isNull(application, "application must not be null");

        // constructing dependencies by hand
        this.logger = new LoggerImpl();
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public Context context() {
        return application;
    }

    @Override
    public Application application() {
        return application;
    }
}
