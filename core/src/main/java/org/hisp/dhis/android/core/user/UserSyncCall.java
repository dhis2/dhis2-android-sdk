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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public final class UserSyncCall implements Call<Response<User>> {
    // retrofit service
    private final UserSyncService userSyncService;

    // databaseAdapter and stores
    private final DatabaseAdapter databaseAdapter;
    private final OrganisationUnitStore organisationUnitStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserRoleStore userRoleStore;
    private final UserStore userStore;
    private final UserRoleProgramLinkStore userRoleProgramLinkStore;
    private final ResourceStore resourceStore;

    private boolean isExecuted;

    public UserSyncCall(UserSyncService userSyncService,
                        DatabaseAdapter databaseAdapter,
                        OrganisationUnitStore organisationUnitStore,
                        UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
                        UserCredentialsStore userCredentialsStore,
                        UserRoleStore userRoleStore,
                        UserStore userStore,
                        UserRoleProgramLinkStore userRoleProgramLinkStore,
                        ResourceStore resourceStore) {
        this.userSyncService = userSyncService;
        this.databaseAdapter = databaseAdapter;
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userRoleStore = userRoleStore;
        this.userStore = userStore;
        this.userRoleProgramLinkStore = userRoleProgramLinkStore;
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

        Response<User> response = getUser();
        if (response.isSuccessful()) {
            deleteOrPersistUserGraph(response);
        }


        return response;
    }

    private Response<User> getUser() throws IOException {
        Fields<User> fields = Fields.<User>builder().fields(
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

        return userSyncService.getUser(fields).execute();
    }

    private void deleteOrPersistUserGraph(Response<User> response) {
        databaseAdapter.beginTransaction();

        try {
            User user = response.body();
            // TODO: check that this is user is authenticated and is persisted in db
            Date serverDateTime = response.headers().getDate(HeaderUtils.DATE);

            deleteOrPersistUser(user, serverDateTime);

            UserCredentials userCredentials = user.userCredentials();

            deleteOrPersistUserCredentials(user, serverDateTime, userCredentials);

            List<UserRole> userRoles = userCredentials.userRoles();

            deleteOrPersistUserRoles(userRoles, serverDateTime);

            deleteOrPersistOrganisationUnits(
                    user.organisationUnits(), OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user, serverDateTime
            );

            deleteOrPersistOrganisationUnits(
                    user.teiSearchOrganisationUnits(),
                    OrganisationUnitModel.Scope.SCOPE_TEI_SEARCH, user, serverDateTime
            );


            databaseAdapter.setTransactionSuccessful();
        } finally {
            databaseAdapter.endTransaction();
        }

    }

    private void deleteOrPersistUser(User user, Date serverDateTime) {
        String userClassName = User.class.getSimpleName();
        if (isDeleted(user)) {
            userStore.delete(user.uid());
            deleteInResourceStore(user.uid());
        } else {
            int updatedRow = userStore.update(user.uid(), user.code(), user.name(), user.displayName(),
                    user.created(), user.lastUpdated(), user.birthday(), user.education(),
                    user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                    user.introduction(), user.employer(), user.interests(), user.languages(),
                    user.email(), user.phoneNumber(), user.nationality(), user.uid());

            // TODO: Does this make sense?
            // if user object was not updated, it means that it wasn't found in databaseAdapter. Insert it.
            if (updatedRow <= 0) {
                userStore.insert(user.uid(), user.code(), user.name(), user.displayName(), user.created(),
                        user.lastUpdated(), user.birthday(), user.education(),
                        user.gender(), user.jobTitle(), user.surname(), user.firstName(),
                        user.introduction(), user.employer(), user.interests(), user.languages(),
                        user.email(), user.phoneNumber(), user.nationality());
            }

            // updateWithSection the resource table
            int updatedResourceRow = updateInResourceStore(
                    userClassName, serverDateTime, userClassName
            );

            if (updatedResourceRow <= 0) {
                insertIntoResourceStore(
                        userClassName, serverDateTime
                );
            }
        }
    }

    private void deleteOrPersistUserCredentials(User user, Date serverDateTime, UserCredentials userCredentials) {
        String userCredentialsClassName = UserCredentials.class.getSimpleName();
        if (isDeleted(userCredentials)) {
            userCredentialsStore.delete(userCredentials.uid());
            deleteInResourceStore(userCredentials.uid());
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
            int updatedResourceRow = updateInResourceStore(
                    userCredentialsClassName,
                    serverDateTime, userCredentialsClassName
            );
            if (updatedResourceRow <= 0) {
                insertIntoResourceStore(
                        userCredentialsClassName, serverDateTime
                );
            }
        }
    }

    private void deleteOrPersistUserRoles(List<UserRole> userRoles, Date serverDate) {
        if (userRoles == null) {
            return;
        }
        String userRoleClassName = UserRole.class.getSimpleName();

        int size = userRoles.size();
        for (int i = 0; i < size; i++) {
            UserRole userRole = userRoles.get(i);

            if (isDeleted(userRole)) {
                userRoleStore.delete(userRole.uid());
                deleteInResourceStore(userRole.uid());
            } else {
                int updatedRow = userRoleStore.update(userRole.uid(), userRole.code(),
                        userRole.name(), userRole.displayName(), userRole.created(),
                        userRole.lastUpdated(), userRole.uid());
                if (updatedRow <= 0) {
                    userRoleStore.insert(userRole.uid(), userRole.code(),
                            userRole.name(), userRole.displayName(), userRole.created(),
                            userRole.lastUpdated());
                }

                int updatedResourceRow = updateInResourceStore(
                        userRoleClassName, serverDate, userRoleClassName
                );

                if (updatedResourceRow <= 0) {
                    insertIntoResourceStore(userRoleClassName, serverDate);
                }

                List<Program> programs = userRole.programs();

                insertOrUpdateUserRoleProgramLink(userRole, programs);


            }
        }
    }

    private void insertOrUpdateUserRoleProgramLink(UserRole userRole, List<Program> programs) {
        if (programs == null) {
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

    private void deleteOrPersistOrganisationUnits(List<OrganisationUnit> organisationUnits,
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

            if (isDeleted(organisationUnit)) {
                deleteOrganisationUnit(organisationUnit);

            } else {
                updateOrInsertOrganisationUnits(
                        organisationUnitScope, user, serverDateTime,
                        organisationUnitSimpleName, organisationUnit);
            }
        }

    }

    private void updateOrInsertOrganisationUnits(OrganisationUnitModel.Scope organisationUnitScope, User user,
                                                 Date serverDateTime, String organisationUnitSimpleName,
                                                 OrganisationUnit organisationUnit) {
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

            int updatedResourceRow = updateInResourceStore(organisationUnitSimpleName,
                    serverDateTime, organisationUnitSimpleName);

            if (updatedResourceRow <= 0) {
                insertIntoResourceStore(
                        organisationUnitSimpleName, serverDateTime
                );
            }

            // maintain link between user and organisation unit
            int updatedUserOrganisationUnitLinkRow = userOrganisationUnitLinkStore.update(
                    user.uid(), organisationUnit.uid(),
                    organisationUnitScope.name(),
                    user.uid(), organisationUnit.uid()
            );

            if (updatedUserOrganisationUnitLinkRow <= 0) {
                userOrganisationUnitLinkStore.insert(
                        user.uid(), organisationUnit.uid(),
                        organisationUnitScope.name()
                );
            }

        }
    }

    private void deleteOrganisationUnit(final OrganisationUnit organisationUnit) {
        organisationUnitStore.delete(organisationUnit.uid());
        deleteInResourceStore(organisationUnit.uid());
    }

    private int updateInResourceStore(final String className,
                                      final Date serverDate,
                                      final String whereClassName) {
        return resourceStore.update(className, serverDate, whereClassName);
    }

    private long insertIntoResourceStore(final String className,
                                         final Date serverDate) {
        return resourceStore.insert(className, serverDate);
    }

    private int deleteInResourceStore(final String resourceType) {
        return resourceStore.delete(resourceType);
    }

    private <T extends BaseIdentifiableObject> boolean isDeleted(final T object) {
        return object.deleted() != null && object.deleted();
    }
}
