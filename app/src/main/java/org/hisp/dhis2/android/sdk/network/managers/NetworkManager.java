package org.hisp.dhis2.android.sdk.network.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import org.hisp.dhis2.android.sdk.controllers.tasks.AuthUserTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.GetOrganisationUnitTask;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.User;

import java.util.concurrent.TimeUnit;

public class NetworkManager {
    private static NetworkManager mNetworkManager;

    private String serverUrl;
    private String credentials;

    private Base64Manager base64Manager;
    private IHttpManager httpManager;

    private NetworkManager() {
        // no instances through
        // constructor for client code
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setFollowSslRedirects(true);
        okHttpClient.setConnectTimeout(HttpManager.TIME_OUT,
                TimeUnit.MILLISECONDS);

        httpManager = new HttpManager(okHttpClient);

        base64Manager = new Base64Manager();
    }

    public static NetworkManager getInstance() {
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager();
        }

        return mNetworkManager;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public Base64Manager getBase64Manager() {
        return base64Manager;
    }

    public IHttpManager getHttpManager() {
        return httpManager;
    }
}
