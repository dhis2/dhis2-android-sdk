/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.wipe.internal;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
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
    private ModuleWiper moduleWiperA;
    @Mock
    private ModuleWiper moduleWiperB;

    private WipeModule wipeModule;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        List<ModuleWiper> wipers = Arrays.asList(moduleWiperA, moduleWiperB);

        wipeModule = new WipeModuleImpl(D2CallExecutor.create(databaseAdapter), wipers);
    }

    @Test
    public void wipe_all_modules() throws Exception {
        wipeModule.wipeEverything();

        verify(moduleWiperA).wipeMetadata();
        verify(moduleWiperB).wipeMetadata();

        verify(moduleWiperA).wipeData();
        verify(moduleWiperB).wipeData();
    }

    @Test
    public void wipe_metadata_in_modules() throws Exception {
        wipeModule.wipeMetadata();

        verify(moduleWiperA).wipeMetadata();
        verify(moduleWiperB).wipeMetadata();

        verify(moduleWiperA, never()).wipeData();
        verify(moduleWiperB, never()).wipeData();
    }

    @Test
    public void wipe_data_in_modules() throws Exception {
        wipeModule.wipeData();

        verify(moduleWiperA, never()).wipeMetadata();
        verify(moduleWiperB, never()).wipeMetadata();

        verify(moduleWiperA).wipeData();
        verify(moduleWiperB).wipeData();
    }
}