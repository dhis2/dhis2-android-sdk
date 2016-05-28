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

package org.hisp.dhis.client.sdk.core.user;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.util.List;

public final class UserAccountControllerImpl implements UserAccountController {
    private static final String TAG = UserAccountControllerImpl.class.getSimpleName();

    private final UserApiClient userApiClient;
    private final UserAccountStore userAccountStore;
    private final StateStore stateStore;
    private final Logger logger;

    public UserAccountControllerImpl(UserApiClient userApiClient,
                                     UserAccountStore userAccountStore,
                                     StateStore stateStore, Logger logger) {
        this.userApiClient = userApiClient;
        this.userAccountStore = userAccountStore;
        this.stateStore = stateStore;
        this.logger = logger;
    }

    @Override
    public void pull() throws ApiException {
        UserAccount userAccount = userApiClient.getUserAccount();

        // update userAccount in database
        userAccountStore.save(userAccount);
    }

    // it will first check if user was changed, if yes, then try to send
    @Override
    public void push() throws ApiException {
        pushUserAccountConditionally();
    }

    @Override
    public void sync() throws ApiException {
        if (!pushUserAccountConditionally()) {
            pull();
        }
    }

    private boolean pushUserAccountConditionally() {
        UserAccount userAccount = getUserAccount();
        if (userAccount == null) {
            logger.d(TAG, "No UserAccount entries exist in database");
            return false;
        }

        Action action = stateStore.queryActionForModel(userAccount);
        if (action == null) {
            logger.d(TAG, "No State is associated with UserAccount");
            return false;
        }

        switch (action) {
            case TO_UPDATE: {
                userApiClient.postUserAccount(userAccount);
                logger.d(TAG, "Successfully sent UserAccount to server");

                boolean isSuccess = stateStore.saveActionForModel(userAccount, Action.SYNCED);
                logger.d(TAG + " state is saved to database", String.valueOf(isSuccess));

                // we don't want to loose user data,
                // so if request failed, we need to retry
                return true;
            }
            case SYNCED: {
                logger.d(TAG, "State associated with UserAccount is: " + action);
                // User was not changed locally, do nothing
                return false;
            }
            default: {
                logger.d(TAG, "Unsupported State is associated with UserAccount: " + action);
                return false;
            }
        }
    }

    private UserAccount getUserAccount() {
        List<UserAccount> userAccounts = userAccountStore.queryAll();

        if (userAccounts != null && !userAccounts.isEmpty()) {
            return userAccounts.get(0);
        }

        return null;
    }
}
