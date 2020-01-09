/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.user.internal;


import android.util.Log;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.user.User;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class UserCall implements Callable<User> {
    private final GenericCallData genericCallData;
    private final APICallExecutor apiCallExecutor;
    private final UserService userService;
    private final Handler<User> userHandler;

    @Inject
    UserCall(GenericCallData genericCallData,
             APICallExecutor apiCallExecutor,
             UserService userService,
             Handler<User> userHandler) {
        this.genericCallData = genericCallData;
        this.apiCallExecutor = apiCallExecutor;
        this.userService = userService;
        this.userHandler = userHandler;
    }

    @Override
    public User call() throws D2Error {

        User user = apiCallExecutor.executeObjectCall(userService.getUser(UserFields.allFieldsWithOrgUnit));
        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
        try {
            userHandler.handle(user);
            genericCallData.resourceHandler().handleResource(Resource.Type.USER);

            transaction.setSuccessful();
        } catch (Exception constraintException) {
            // TODO review
            //constraintException.printStackTrace();
            Log.d("CAll", "call: constraintException");
        } finally {
            transaction.end();
        }
        return user;
    }
}
