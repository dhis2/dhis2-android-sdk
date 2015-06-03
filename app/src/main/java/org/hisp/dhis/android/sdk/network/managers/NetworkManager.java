/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.network.managers;

import com.squareup.okhttp.OkHttpClient;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class NetworkManager {

    private static final String CLASS_TAG = "NetworkManager";

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

    /**
     * Returns true if able to connect to the internet.
     * Will return false if run on the main ui thread
     * @return
     */
    public static boolean hasInternetConnection() {
        try {
            String url = getInstance().getServerUrl();
            if(url == null) {
                return false;
            }
            InetAddress ipAddr = InetAddress.getByName(url); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
