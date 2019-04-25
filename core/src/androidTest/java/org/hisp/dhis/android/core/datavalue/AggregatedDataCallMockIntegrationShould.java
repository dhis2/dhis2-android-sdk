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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.reactivex.observers.TestObserver;

public class AggregatedDataCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        dhis2MockServer.setRequestDispatcher();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }

    @Test
    public void emit_progress() throws Exception {
        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
        d2.syncMetaData().call();
        TestObserver<D2Progress> testObserver = d2.aggregatedModule().data().download().asObservable().test();
        testObserver.assertValueCount(3);

        testObserver.assertValueAt(0, v -> assertDouble(v.percentage(), 33.33) && v.lastCall().equals("SystemInfo"));
        testObserver.assertValueAt(1, v -> assertDouble(v.percentage(), 66.66));
        testObserver.assertValueAt(2, v -> assertDouble(v.percentage(), 100));
        testObserver.dispose();
    }

    private boolean assertDouble(double d1, double d2) {
        return d2 - d1 < 0.01;
    }
}