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

import org.hisp.dhis.android.core.program.Program;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class UserRoleHandler {
    private final UserRoleStore userRoleStore;
    private final UserRoleProgramLinkStore userRoleProgramLinkStore;


    public UserRoleHandler(UserRoleStore userRoleStore,
                           UserRoleProgramLinkStore userRoleProgramLinkStore) {
        this.userRoleStore = userRoleStore;
        this.userRoleProgramLinkStore = userRoleProgramLinkStore;
    }

    public void handleUserRoles(List<UserRole> userRoles) {
        if (userRoles == null) {
            return;
        }
        deleteOrPersistUserRoles(userRoles);
    }

    private void deleteOrPersistUserRoles(List<UserRole> userRoles) {
        int size = userRoles.size();
        for (int i = 0; i < size; i++) {
            UserRole userRole = userRoles.get(i);

            if (isDeleted(userRole)) {
                userRoleStore.delete(userRole.uid());
            } else {
                int updatedRow = userRoleStore.update(userRole.uid(), userRole.code(),
                        userRole.name(), userRole.displayName(), userRole.created(),
                        userRole.lastUpdated(), userRole.uid());
                if (updatedRow <= 0) {
                    userRoleStore.insert(userRole.uid(), userRole.code(),
                            userRole.name(), userRole.displayName(), userRole.created(),
                            userRole.lastUpdated());
                }

                List<Program> programs = userRole.programs();

                insertOrUpdateUserRoleProgramLink(userRole, programs);


            }
        }
    }

    private void insertOrUpdateUserRoleProgramLink(UserRole userRole, List<Program> programs) {
        if (programs == null || userRole == null) {
            return;
        }

        int programSize = programs.size();
        for (int i = 0; i < programSize; i++) {

            Program program = programs.get(i);
            int updatedLinkRow = userRoleProgramLinkStore.update(
                    userRole.uid(), program.uid(), userRole.uid(), program.uid());

            if (updatedLinkRow <= 0) {
                userRoleProgramLinkStore.insert(userRole.uid(), program.uid());
            }
        }
    }
}
