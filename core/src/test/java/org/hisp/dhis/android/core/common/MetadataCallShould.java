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
package org.hisp.dhis.android.core.common;

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.arch.modules.Downloader;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.CategoryModuleDownloader;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitDownloadModule;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.settings.SystemSettingModuleDownloader;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserModuleDownloader;
import org.hisp.dhis.android.core.user.UserRole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould extends BaseCallShould {

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private Date serverDateTime;

    @Mock
    private User user;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private UserRole userRole;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private Program program;

    @Mock
    private DataSet dataSet;

    @Mock
    private Callable<Unit> systemInfoDownloadCall;

    @Mock
    private Callable<Unit> systemSettingDownloadCall;

    @Mock
    private Call<User> userCall;

    @Mock
    private Callable<Unit> categoryDownloadCall;

    @Mock
    private Call<List<Program>> programParentCall;

    @Mock
    private Call<List<DataSet>> dataSetParentCall;

    @Mock
    private Callable<Unit> organisationUnitDownloadCall;

    @Mock
    private SystemInfoModuleDownloader systemInfoModuleDownloader;

    @Mock
    private SystemSettingModuleDownloader systemSettingDownloader;

    @Mock
    private UserModuleDownloader userDownloadModule;

    @Mock
    private CategoryModuleDownloader categoryDownloader;

    @Mock
    private Downloader<List<Program>> programParentCallFactory;

    @Mock
    private OrganisationUnitDownloadModule organisationUnitDownloadModule;

    @Mock
    private Downloader<List<DataSet>> dataSetDownloader;

    @Mock
    private ForeignKeyCleaner foreignKeyCleaner;

    // object to test
    private MetadataCall metadataCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        // Payload data
        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(userCredentials.userRoles()).thenReturn(Collections.singletonList(userRole));
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));

        // Call factories
        when(systemInfoModuleDownloader.downloadMetadata()).thenReturn(systemInfoDownloadCall);
        when(systemSettingDownloader.downloadMetadata()).thenReturn(systemSettingDownloadCall);
        when(userDownloadModule.downloadMetadata()).thenReturn(userCall);
        when(programParentCallFactory.download()).thenReturn(programParentCall);
        when(categoryDownloader.downloadMetadata()).thenReturn(categoryDownloadCall);
        when(organisationUnitDownloadModule.download(same(user), anySetOf(Program.class),
                anySetOf(DataSet.class))).thenReturn(organisationUnitDownloadCall);
        when(dataSetDownloader.download()).thenReturn(dataSetParentCall);

        // Calls
        when(systemInfoDownloadCall.call()).thenReturn(new Unit());
        when(systemSettingDownloadCall.call()).thenReturn(new Unit());
        when(userCall.call()).thenReturn(user);
        when(categoryDownloadCall.call()).thenReturn(new Unit());
        when(programParentCall.call()).thenReturn(Lists.newArrayList(program));
        when(dataSetParentCall.call()).thenReturn(Lists.newArrayList(dataSet));

        // Metadata call
        metadataCall = new MetadataCall(
                new D2CallExecutor(databaseAdapter),
                systemInfoModuleDownloader,
                systemSettingDownloader,
                userDownloadModule,
                categoryDownloader,
                programParentCallFactory,
                organisationUnitDownloadModule,
                dataSetDownloader,
                foreignKeyCleaner);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() throws Exception {
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_system_info_call_fail() throws Exception {
        when(systemInfoDownloadCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_system_setting_call_fail() throws Exception {
        when(systemSettingDownloadCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_user_call_fail() throws Exception {
        when(userCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_category_download_call_fail() throws Exception {
        when(categoryDownloadCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_program_call_fail() throws Exception {
        when(programParentCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_organisation_unit_call_fail() throws Exception {
        when(organisationUnitDownloadCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_dataset_parent_call_fail() throws Exception {
        when(dataSetParentCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test
    public void call_foreign_key_cleaner() throws Exception {
        metadataCall.call();
        verify(foreignKeyCleaner).cleanForeignKeyErrors();
    }
}