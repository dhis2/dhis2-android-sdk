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

package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.implementations.Transaction;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WipeModuleShould {

    @Mock
    private Transaction transaction;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private IdentifiableObjectStore<UserModel> userStore;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private ObjectStore<UserOrganisationUnitLinkModel> userOrganisationUnitLinkStore;

    @Mock
    private ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;

    @Mock
    private IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    @Mock
    private ObjectWithoutUidStore<DataValue> dataValueStore;

    @Mock
    private IdentifiableObjectStore<ProgramTrackedEntityAttributeModel> trackedEntityAttributeStore;

    private WipeModule wipeModule;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
        List<DeletableStore> metadataStoreList = new ArrayList<>();
        metadataStoreList.add(userStore);
        metadataStoreList.add(userCredentialsStore);
        metadataStoreList.add(userOrganisationUnitLinkStore);
        metadataStoreList.add(authenticatedUserStore);
        metadataStoreList.add(organisationUnitStore);

        List<DeletableStore> dataStoreList = new ArrayList<>();
        dataStoreList.add(dataValueStore);
        dataStoreList.add(trackedEntityAttributeStore);
        wipeModule = new WipeModuleImpl(databaseAdapter, metadataStoreList, dataStoreList,
                new ArrayList<WipeableModule>());
    }

    @Test
    public void wipe_all_tables() throws Exception {
        wipeModule.wipeEverything();

        verify(userStore).delete();
        verify(userCredentialsStore).delete();
        verify(userOrganisationUnitLinkStore).delete();
        verify(authenticatedUserStore).delete();
        verify(organisationUnitStore).delete();

        verify(dataValueStore).delete();
        verify(trackedEntityAttributeStore).delete();
    }

    @Test
    public void wipe_metadata_tables() throws Exception {
        wipeModule.wipeMetadata();

        verify(userStore).delete();
        verify(userCredentialsStore).delete();
        verify(userOrganisationUnitLinkStore).delete();
        verify(authenticatedUserStore).delete();
        verify(organisationUnitStore).delete();

        verify(dataValueStore, never()).delete();
        verify(trackedEntityAttributeStore, never()).delete();
    }

    @Test
    public void wipe_data_tables() throws Exception {
        wipeModule.wipeData();

        verify(userStore, never()).delete();
        verify(userCredentialsStore, never()).delete();
        verify(userOrganisationUnitLinkStore, never()).delete();
        verify(authenticatedUserStore, never()).delete();
        verify(organisationUnitStore, never()).delete();

        verify(dataValueStore).delete();
        verify(trackedEntityAttributeStore).delete();
    }
}