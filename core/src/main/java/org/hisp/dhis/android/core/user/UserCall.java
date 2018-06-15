/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.user;


import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.calls.factories.GenericCallFactory;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

public final class UserCall extends SyncCall<User> {
    private final GenericCallData genericCallData;
    private final UserService userService;
    private final GenericHandler<User, UserModel> userHandler;

    UserCall(GenericCallData genericCallData,
             UserService userService,
             GenericHandler<User, UserModel> userHandler) {
        this.genericCallData = genericCallData;
        this.userService = userService;
        this.userHandler = userHandler;
    }

    @Override
    public User call() throws D2CallException {
        setExecuted();

        User user = new APICallExecutor().executeObjectCall(userService.getUser(User.allFieldsWithOrgUnit));
        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
        try {
            userHandler.handle(user, new UserModelBuilder());
            genericCallData.resourceHandler().handleResource(ResourceModel.Type.USER, genericCallData.serverDate());

            transaction.setSuccessful();
        } catch (SQLiteConstraintException constraintException) {
            // TODO review
            //constraintException.printStackTrace();
            Log.d("CAll", "call: constraintException");
        } finally {
            transaction.end();
        }
        return user;
    }

    public static final GenericCallFactory<User> FACTORY = new GenericCallFactory<User>() {

        @Override
        public Call<User> create(GenericCallData genericCallData) {
            return new UserCall(
                    genericCallData,
                    genericCallData.retrofit().create(UserService.class),
                    UserHandler.create(genericCallData.databaseAdapter())
            );
        }
    };
}
