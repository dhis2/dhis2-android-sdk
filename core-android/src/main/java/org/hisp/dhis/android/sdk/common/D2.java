package org.hisp.dhis.android.sdk.common;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.sdk.api.modules.NetworkModule;
import org.hisp.dhis.android.sdk.api.modules.PersistenceModule;
import org.hisp.dhis.android.sdk.api.modules.PreferencesModule;
import org.hisp.dhis.android.sdk.dashboard.DashboardScope;
import org.hisp.dhis.android.sdk.dashboard.IDashboardScope;
import org.hisp.dhis.android.sdk.user.IUserAccountScope;
import org.hisp.dhis.android.sdk.user.UserAccountScope;
import org.hisp.dhis.java.sdk.common.controllers.ControllersModule;
import org.hisp.dhis.java.sdk.common.controllers.IControllersModule;
import org.hisp.dhis.java.sdk.common.network.Configuration;
import org.hisp.dhis.java.sdk.common.network.INetworkModule;
import org.hisp.dhis.java.sdk.common.persistence.IPersistenceModule;
import org.hisp.dhis.java.sdk.common.preferences.IPreferencesModule;
import org.hisp.dhis.java.sdk.common.preferences.IUserPreferences;
import org.hisp.dhis.java.sdk.common.services.IServicesModule;
import org.hisp.dhis.java.sdk.common.services.ServicesModule;
import org.hisp.dhis.java.sdk.models.user.UserAccount;
import org.hisp.dhis.java.sdk.models.utils.IModelUtils;

import rx.Observable;

import static org.hisp.dhis.java.sdk.models.utils.Preconditions.isNull;

public class D2 {
    private static D2 mD2;

    private final IUserPreferences mUserPreferences;
    private final IDashboardScope mDashboardScope;
    private final IUserAccountScope mUserAccountScope;

    private D2(Context context, Configuration configuration) {
        IModelUtils modelUtils = null;
        INetworkModule networkModule = new NetworkModule(null);
        IPersistenceModule persistenceModule = new PersistenceModule(context);
        IPreferencesModule preferencesModule = new PreferencesModule(context);
        IServicesModule servicesModule = new ServicesModule(persistenceModule, null);
        IControllersModule controllersModule = new ControllersModule(networkModule,
                persistenceModule, preferencesModule, modelUtils);

        mDashboardScope = new DashboardScope(servicesModule.getDashboardService(),
                controllersModule.getDashboardController());
        mUserAccountScope = new UserAccountScope(null, null);
        mUserPreferences = preferencesModule.getUserPreferences();
    }

    public static void init(Context context, Configuration configuration) {
        isNull(context, "Context object must not be null");
        isNull(configuration, "Configuration object must not be null");

        mD2 = new D2(context, configuration);
    }

    private static D2 getInstance() {
        isNull(mD2, "You have to call init first");
        return mD2;
    }

    public static Observable<UserAccount> signIn(String username, String password) {
        return getInstance().mUserAccountScope.signIn(username, password);
    }

    public static Observable<Boolean> signOut() {
        return getInstance().mUserAccountScope.signOut();
    }

    @NonNull
    public static IDashboardScope dashboards() {
        return getInstance().mDashboardScope;
    }
}
