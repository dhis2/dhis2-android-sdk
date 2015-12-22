package org.hisp.dhis.client.sdk.android.common;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.api.modules.NetworkModule;
import org.hisp.dhis.client.sdk.android.api.modules.PersistenceModule;
import org.hisp.dhis.client.sdk.android.api.modules.PreferencesModule;
import org.hisp.dhis.client.sdk.android.user.IUserAccountScope;
import org.hisp.dhis.client.sdk.android.user.UserAccountScope;
import org.hisp.dhis.client.sdk.core.common.controllers.ControllersModule;
import org.hisp.dhis.client.sdk.core.common.controllers.IControllersModule;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.common.services.IServicesModule;
import org.hisp.dhis.client.sdk.core.common.services.ServicesModule;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import rx.Observable;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class D2 {
    private static D2 mD2;

    private final IUserPreferences mUserPreferences;
    private final IUserAccountScope mUserAccountScope;

    private D2(Context context) {
        IModelUtils modelUtils = new ModelUtils();

        IPersistenceModule persistenceModule = new PersistenceModule(context);
        IPreferencesModule preferencesModule = new PreferencesModule(context);
        INetworkModule networkModule = new NetworkModule(preferencesModule);
        IServicesModule servicesModule = new ServicesModule(persistenceModule);
        IControllersModule controllersModule = new ControllersModule(networkModule,
                persistenceModule, preferencesModule, modelUtils);

        mUserPreferences = preferencesModule.getUserPreferences();

        mUserAccountScope = new UserAccountScope(controllersModule.getUserAccountController(),
                mUserPreferences, preferencesModule.getConfigurationPreferences());
    }

    public static void init(Context context) {
        isNull(context, "Context object must not be null");

        mD2 = new D2(context);
    }

    private static D2 getInstance() {
        isNull(mD2, "You have to call init first");

        return mD2;
    }

    public static Observable<UserAccount> signIn(Configuration configuration, String username,
                                                 String password) {
        return getInstance().mUserAccountScope.signIn(configuration, username, password);
    }

    public static Observable<Boolean> signOut() {
        return getInstance().mUserAccountScope.signOut();
    }
}
