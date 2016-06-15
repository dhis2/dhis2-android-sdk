/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.user;

import org.hisp.dhis.client.sdk.core.user.UserApiClient;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import java.util.HashMap;
import java.util.Map;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.call;

public class UserAccountApiClientImpl implements UserApiClient {
    private final UserApiClientRetrofit apiClient;

    public UserAccountApiClientImpl(UserApiClientRetrofit apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public UserAccount getUserAccount() {
        Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,created,lastUpdated,name,displayName," +
                "firstName,surname,gender,birthday,introduction," +
                "education,employer,interests,jobTitle,languages,email,phoneNumber," +
                "organisationUnits[id],userCredentials[userRoles[dataSets[id],programs[id,programType]]]");

        return call(apiClient.getCurrentUserAccount(QUERY_PARAMS));
    }

    @Override
    public void postUserAccount(UserAccount userAccount) {
        call(apiClient.postCurrentUserAccount(userAccount));
    }
}
