/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.user.User;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class User$Flow extends BaseIdentifiableObject$Flow {
    // no additional fields

    public User$Flow() {
        // empty constructor
    }

    public static User toModel(User$Flow userFlow) {
        if (userFlow == null) {
            return null;
        }

        User user = new User();
        user.setId(userFlow.getId());
        user.setUId(userFlow.getUId());
        user.setCreated(userFlow.getCreated());
        user.setLastUpdated(userFlow.getLastUpdated());
        user.setName(userFlow.getName());
        user.setDisplayName(userFlow.getDisplayName());
        user.setAccess(userFlow.getAccess());
        return user;
    }

    public static User$Flow fromModel(User user) {
        if (user == null) {
            return null;
        }

        User$Flow userFlow = new User$Flow();
        userFlow.setId(user.getId());
        userFlow.setUId(user.getUId());
        userFlow.setCreated(user.getCreated());
        userFlow.setLastUpdated(user.getLastUpdated());
        userFlow.setName(user.getName());
        userFlow.setDisplayName(user.getDisplayName());
        userFlow.setAccess(user.getAccess());
        return userFlow;
    }

    public static List<User> toModels(List<User$Flow> userFlows) {
        List<User> users = new ArrayList<>();

        if (userFlows != null && !userFlows.isEmpty()) {
            for (User$Flow userFlow : userFlows) {
                users.add(toModel(userFlow));
            }
        }

        return users;
    }

    public static List<User$Flow> fromModels(List<User> users) {
        List<User$Flow> userFlows = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userFlows.add(fromModel(user));
            }
        }

        return userFlows;
    }
}
