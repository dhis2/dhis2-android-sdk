package org.hisp.dhis.android.sdk.common;

import android.content.Context;

import org.hisp.dhis.android.sdk.api.modules.PersistenceModule;
import org.hisp.dhis.android.sdk.api.modules.PreferencesModule;
import org.hisp.dhis.android.sdk.dashboard.DashboardScope;
import org.hisp.dhis.android.sdk.modules.NetworkModule;
import org.hisp.dhis.java.sdk.common.controllers.ControllersModule;
import org.hisp.dhis.java.sdk.common.controllers.IControllersModule;
import org.hisp.dhis.java.sdk.common.network.Configuration;
import org.hisp.dhis.java.sdk.common.network.INetworkModule;
import org.hisp.dhis.java.sdk.common.persistence.IPersistenceModule;
import org.hisp.dhis.java.sdk.common.preferences.IPreferencesModule;
import org.hisp.dhis.java.sdk.common.services.IServicesModule;
import org.hisp.dhis.java.sdk.common.services.ServicesModule;
import org.hisp.dhis.java.sdk.models.utils.IModelUtils;

import static org.hisp.dhis.java.sdk.models.utils.Preconditions.isNull;

public class Dhis2 {
    private static Dhis2 mDhis2;
    private final DashboardScope mDashboardScope;

    private Dhis2(Context context, Configuration configuration) {
        INetworkModule networkModule = new NetworkModule(null);
        IPersistenceModule persistenceModule = new PersistenceModule(context);
        IPreferencesModule preferencesModule = new PreferencesModule(context);
        IServicesModule servicesModule = new ServicesModule(persistenceModule, null);
        IModelUtils modelUtils = null;
        IControllersModule controllersModule = new ControllersModule(networkModule,
                persistenceModule, preferencesModule, modelUtils);

        mDashboardScope = new DashboardScope(servicesModule.getDashboardService(),
                controllersModule.getDashboardController());
    }

    public static void init(Context context, Configuration configuration) {
        isNull(context, "Context object must not be null");
        isNull(configuration, "Configuration object must not be null");
        mDhis2 = new Dhis2(context, configuration);
    }

    private static Dhis2 getInstance() {
        isNull(mDhis2, "You have to call init first");
        return mDhis2;
    }

    public static DashboardScope dashboards() {
        return getInstance().mDashboardScope;
    }
}
