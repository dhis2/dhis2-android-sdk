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
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.utils.Preconditions;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountStore userAccountStore;
    private final StateStore stateStore;

    public UserAccountServiceImpl(UserAccountStore userAccountStore,
                                  StateStore stateStore) {
        this.userAccountStore = userAccountStore;
        this.stateStore = stateStore;
    }

    @Override
    public boolean save(UserAccount userAccount) {
        // return userAccountStore.save(object);
        isNull(userAccount, "UserAccount must not be null");

        Action action = stateStore.queryActionForModel(userAccount);
        if (action == null) {
            throw new UnsupportedOperationException("It seems that UserAccount " +
                    "was saved incorrectly or does not exist at all");
        }

        switch (action) {
            case TO_POST:
                throw new UnsupportedOperationException("Unsupported state flag: TO_POST. " +
                        "It seems that UserAccount was saved incorrectly or does not exist at all");
            case TO_UPDATE: {
                return userAccountStore.save(userAccount);
            }
            case SYNCED: {
                return userAccountStore.save(userAccount) &&
                        stateStore.saveActionForModel(userAccount, Action.TO_UPDATE);
            }
            // we cannot save what should be removed
            case TO_DELETE: {
                throw new UnsupportedOperationException("Unsupported state flag: TO_DELETE. " +
                        "It seems that UserAccount was saved incorrectly or does not exist at all");
            }
            default: {
                throw new IllegalArgumentException("Unsupported state action");
            }
        }
    }

    @Override
    public User toUser(UserAccount userAccount) {
        Preconditions.isNull(userAccount, "userAccount must not be null");

        User user = new User();
        user.setUId(userAccount.getUId());
        user.setAccess(userAccount.getAccess());
        user.setCreated(userAccount.getCreated());
        user.setLastUpdated(userAccount.getLastUpdated());
        user.setName(userAccount.getName());
        user.setDisplayName(userAccount.getDisplayName());
        return user;
    }

    @Override
    public UserAccount get() {
        List<UserAccount> userAccounts = userAccountStore.queryAll();

        if (userAccounts != null && !userAccounts.isEmpty()) {
            return userAccounts.get(0);
        }

        return null;
    }
}
