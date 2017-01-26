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
package org.hisp.dhis.android.core.organisationunit;

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.HeaderUtils;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserStore;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public final class AssignedOrganisationUnitsCall implements Call<Response<User>> {
    // retrofit service
    private final AssignedOrganisationUnitService assignedOrganisationUnitService;

    // database and stores
    private final SQLiteDatabase database;
    private final OrganisationUnitStore organisationUnitStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final UserCredentialsStore userCredentialsStore;
    //    private final UserRoleStore userRoleStore;
    private final UserStore userStore;
    private final ResourceStore resourceStore;

    private boolean isExecuted;

    public AssignedOrganisationUnitsCall(AssignedOrganisationUnitService assignedOrganisationUnitService,
                                         SQLiteDatabase database,
                                         OrganisationUnitStore organisationUnitStore,
                                         UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
                                         UserCredentialsStore userCredentialsStore,
                                         UserStore userStore, ResourceStore resourceStore) {
        this.assignedOrganisationUnitService = assignedOrganisationUnitService;
        this.database = database;
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.userCredentialsStore = userCredentialsStore;
//        this.userRoleStore = userRoleStore;
        this.userStore = userStore;
        this.resourceStore = resourceStore;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<User> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Response<User> response = getAssignedOrganisationUnits();
        if (response.isSuccessful()) {
            saveAssignedOrganisationUnits(response);
        }


        return null;
    }

    private Response<User> getAssignedOrganisationUnits() throws IOException {
        Filter<User> filter = Filter.<User>builder().fields(
                User.uid, User.code, User.name, User.displayName,
                User.created, User.lastUpdated, User.birthday, User.education,
                User.gender, User.jobTitle, User.surname, User.firstName,
                User.introduction, User.employer, User.interests, User.languages,
                User.email, User.phoneNumber, User.nationality,
                User.userCredentials.with(
                        UserCredentials.uid,
                        UserCredentials.code,
                        UserCredentials.name,
                        UserCredentials.displayName,
                        UserCredentials.created,
                        UserCredentials.lastUpdated,
                        UserCredentials.username,
                        UserCredentials.userRoles.with(
                                UserRole.uid, UserRole.programs.with(
                                        Program.uid
                                )
                        )
                ),
                User.organisationUnits.with(
                        OrganisationUnit.uid,
                        OrganisationUnit.programs.with(
                                Program.uid
                        )
                ),
                User.teiSearchOrganisationUnits.with(
                        OrganisationUnit.uid
                )
        ).build();

        return assignedOrganisationUnitService.getAssignedOrganisationUnits(filter).execute();
    }

    private void saveAssignedOrganisationUnits(Response<User> response) {
        database.beginTransaction();

        try {
            User user = response.body();
            // TODO: check that this is user is authenticated and is persisted in db
            Date serverDateTime = response.headers().getDate(HeaderUtils.DATE);

            if (user.deleted() != null && user.deleted()) {
                userStore.delete(user.uid());
                resourceStore.delete(user.uid());
            } else {
                int updatedRow = userStore.update(user.uid(), user.code(), user.name(), user.displayName(),
                        user.created(), user.lastUpdated(), user.birthday(), user.education(),
                        user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                        user.introduction(), user.employer(), user.interests(), user.languages(),
                        user.email(), user.phoneNumber(), user.nationality(), user.uid());

                // TODO: Does this make sense?
                // if user object was not updated, it means that it wasn't found in database. Insert it.
                if (updatedRow <= 0) {
                    userStore.insert(user.uid(), user.code(), user.name(), user.displayName(), user.created(),
                            user.lastUpdated(), user.birthday(), user.education(),
                            user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                            user.introduction(), user.employer(), user.interests(), user.languages(),
                            user.email(), user.phoneNumber(), user.nationality());
                }

                // update the resource table
                int updatedResourceRow = resourceStore.update(
                        User.class.getSimpleName(), user.uid(), serverDateTime, user.uid()
                );

                if (updatedResourceRow <= 0) {
                    resourceStore.insert(
                            User.class.getSimpleName(), user.uid(), serverDateTime
                    );
                }
            }


            UserCredentials userCredentials = user.userCredentials();

            if (userCredentials.deleted() != null && userCredentials.deleted()) {
                userCredentialsStore.delete(userCredentials.uid());
                resourceStore.delete(userCredentials.uid());
            } else {
                int updatedRow = userCredentialsStore.update(userCredentials.uid(), userCredentials.code(),
                        userCredentials.name(), userCredentials.displayName(), userCredentials.created(),
                        userCredentials.lastUpdated(), userCredentials.username(), user.uid(), userCredentials.uid()
                );

                if (updatedRow <= 0) {
                    userCredentialsStore.insert(
                            userCredentials.uid(), userCredentials.code(), userCredentials.name(),
                            userCredentials.displayName(), userCredentials.created(), userCredentials.lastUpdated(),
                            userCredentials.username(), user.uid()
                    );
                }
                int updatedResourceRow = resourceStore.update(
                        UserCredentials.class.getSimpleName(), userCredentials.uid(),
                        serverDateTime, userCredentials.uid()
                );
                if (updatedResourceRow <= 0) {
                    resourceStore.insert(
                            UserCredentials.class.getSimpleName(), userCredentials.uid(), serverDateTime
                    );
                }
            }

            deleteInsertOrUpdateAssignedOrganisationUnits(
                    user.organisationUnits(), OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user, serverDateTime
            );

            deleteInsertOrUpdateAssignedOrganisationUnits(
                    user.teiSearchOrganisationUnits(),
                    OrganisationUnitModel.Scope.SCOPE_TEI_SEARCH, user, serverDateTime
            );


            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void deleteInsertOrUpdateAssignedOrganisationUnits(List<OrganisationUnit> organisationUnits,
                                                               OrganisationUnitModel.Scope organisationUnitScope,
                                                               User user,
                                                               Date serverDateTime) {
        if (organisationUnits == null) {
            return;
        }

        String organisationUnitSimpleName = OrganisationUnit.class.getSimpleName();
        int size = organisationUnits.size();
        for (int i = 0; i < size; i++) {
            OrganisationUnit organisationUnit = organisationUnits.get(i);

            if (organisationUnit.deleted() != null && organisationUnit.deleted()) {
                organisationUnitStore.delete(organisationUnit.uid());
                resourceStore.delete(organisationUnit.uid());
                userOrganisationUnitLinkStore.delete(user.uid(), organisationUnit.uid());

            } else {
                int updatedRow = organisationUnitStore.update(organisationUnit.uid(),
                        organisationUnit.code(),
                        organisationUnit.name(),
                        organisationUnit.displayName(),
                        organisationUnit.created(),
                        organisationUnit.lastUpdated(),
                        organisationUnit.shortName(),
                        organisationUnit.displayShortName(),
                        organisationUnit.description(),
                        organisationUnit.displayDescription(),
                        organisationUnit.path(),
                        organisationUnit.openingDate(),
                        organisationUnit.closedDate(),
                        organisationUnit.parent() == null ? null : organisationUnit.parent().uid(),
                        organisationUnit.level(), organisationUnit.uid());

                if (updatedRow <= 0) {
                    organisationUnitStore.insert(
                            organisationUnit.uid(),
                            organisationUnit.code(),
                            organisationUnit.name(),
                            organisationUnit.displayName(),
                            organisationUnit.created(),
                            organisationUnit.lastUpdated(),
                            organisationUnit.shortName(),
                            organisationUnit.displayShortName(),
                            organisationUnit.description(),
                            organisationUnit.displayDescription(),
                            organisationUnit.path(),
                            organisationUnit.openingDate(),
                            organisationUnit.closedDate(),
                            organisationUnit.parent() == null ? null : organisationUnit.parent().uid(),
                            organisationUnit.level()
                    );

                    int updatedResourceRow = resourceStore.update(organisationUnitSimpleName,
                            organisationUnit.uid(), serverDateTime, organisationUnit.uid());

                    if (updatedResourceRow <= 0) {
                        resourceStore.insert(
                                organisationUnitSimpleName, organisationUnit.uid(), serverDateTime
                        );
                    }

                    // maintain link between user and organisation unit
                    int updatedUserOrgunitLinkRow = userOrganisationUnitLinkStore.update(
                            user.uid(), organisationUnit.uid(),
                            organisationUnitScope.name(),
                            user.uid(), organisationUnit.uid()
                    );

                    if (updatedUserOrgunitLinkRow <= 0) {
                        userOrganisationUnitLinkStore.insert(
                                user.uid(), organisationUnit.uid(),
                                organisationUnitScope.name()
                        );
                    }

                }
            }
        }

    }
}
