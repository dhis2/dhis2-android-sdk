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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboEndpointCall;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.calls.factories.BasicCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.ForeignKeyCleaner;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.calls.factories.GenericCallFactory;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramParentCall;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.settings.SystemSettingCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.common.Unit;

import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public class MetadataCall extends SyncCall<Unit> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private final BasicCallFactory<SystemInfo> systemInfoCallFactory;
    private final GenericCallFactory<SystemSetting> systemSettingCallFactory;
    private final GenericCallFactory<User> userCallFactory;
    private final GenericCallFactory<List<Category>> categoryCallFactory;
    private final GenericCallFactory<List<CategoryCombo>> categoryComboCallFactory;
    private final GenericCallFactory<List<Program>> programParentCallFactory;
    private final OrganisationUnitCall.Factory organisationUnitCallFactory;
    private final DataSetParentCall.Factory dataSetParentCallFactory;
    private final ForeignKeyCleaner foreignKeyCleaner;

    public MetadataCall(@NonNull DatabaseAdapter databaseAdapter,
                        @NonNull Retrofit retrofit,
                        @NonNull BasicCallFactory<SystemInfo> systemInfoCallFactory,
                        @NonNull GenericCallFactory<SystemSetting> systemSettingCallFactory,
                        @NonNull GenericCallFactory<User> userCallFactory,
                        @NonNull GenericCallFactory<List<Category>> categoryCallFactory,
                        @NonNull GenericCallFactory<List<CategoryCombo>> categoryComboCallFactory,
                        @NonNull GenericCallFactory<List<Program>> programParentCallFactory,
                        @NonNull OrganisationUnitCall.Factory organisationUnitCallFactory,
                        @NonNull DataSetParentCall.Factory dataSetParentCallFactory,
                        @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;

        this.systemInfoCallFactory = systemInfoCallFactory;
        this.systemSettingCallFactory = systemSettingCallFactory;
        this.userCallFactory = userCallFactory;
        this.categoryCallFactory = categoryCallFactory;
        this.categoryComboCallFactory = categoryComboCallFactory;
        this.programParentCallFactory = programParentCallFactory;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.dataSetParentCallFactory = dataSetParentCallFactory;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Unit call() throws Exception {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {
            @Override
            public Unit call() throws D2CallException {
                SystemInfo systemInfo = executor.executeD2Call(
                        systemInfoCallFactory.create(databaseAdapter, retrofit));

                GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                        systemInfo.serverDate());

                executor.executeD2Call(systemSettingCallFactory.create(genericCallData));
                User user = executor.executeD2Call(userCallFactory.create(genericCallData));
                executor.executeD2Call(categoryCallFactory.create(genericCallData));
                executor.executeD2Call(categoryComboCallFactory.create(genericCallData));

                List<Program> programs = executor.executeD2Call(
                        programParentCallFactory.create(genericCallData));

                List<OrganisationUnit> organisationUnits =
                        executor.executeD2Call(organisationUnitCallFactory.create(genericCallData, user,
                                UidsHelper.getUids(programs)));

                executor.executeD2Call(dataSetParentCallFactory.create(user, genericCallData, organisationUnits));

                foreignKeyCleaner.cleanForeignKeyErrors();

                return new Unit();
            }
        });
    }

    public static MetadataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
        return new MetadataCall(
                databaseAdapter,
                retrofit,
                SystemInfoCall.FACTORY,
                SystemSettingCall.FACTORY,
                UserCall.FACTORY,
                CategoryEndpointCall.FACTORY,
                CategoryComboEndpointCall.FACTORY,
                ProgramParentCall.FACTORY,
                OrganisationUnitCall.FACTORY,
                DataSetParentCall.FACTORY,
                new ForeignKeyCleaner(databaseAdapter)
        );
    }
}
