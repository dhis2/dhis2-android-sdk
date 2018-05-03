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
package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceQueryCallShould extends BaseCallShould {
    @Mock
    private TrackedEntityInstanceService service;

    @Mock
    private SearchGridMapper mapper;

    @Mock
    private SearchGrid searchGrid;

    @Mock
    private Call<SearchGrid> searchGridCall;

    @Mock
    private List<TrackedEntityInstance> teis;

    @Mock
    private List<String> attribute;

    @Mock
    private List<String> filter;

    private List<String> orgUnits;

    private final String program = "program";
    private final String queryStr = "queryStr";
    private final Boolean paging = true;
    private final Integer page = 2;
    private final Integer pageSize = 33;

    // object to test
    private TrackedEntityInstanceQueryCall call;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        orgUnits = new ArrayList<>(2);
        orgUnits.add("ou1");
        orgUnits.add("ou2");

        TrackedEntityInstanceQuery query = TrackedEntityInstanceQuery.builder().
                orgUnits(orgUnits).orgUnitMode(OuMode.ACCESSIBLE).program(program)
                .query(queryStr).attribute(attribute).filter(filter)
                .paging(paging).page(page).pageSize(pageSize).build();

        when(service.query(anyString(), anyString(), anyString(), anyString(),
                anyListOf(String.class), anyListOf(String.class), anyBoolean(), anyInt(), anyInt()))
        .thenReturn(searchGridCall);
        when(searchGridCall.execute()).thenReturn(Response.success(searchGrid));
        when(mapper.transform(any(SearchGrid.class))).thenReturn(teis);

        // Metadata call
        call = new TrackedEntityInstanceQueryCall(service, query, mapper);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() throws Exception {
        List<TrackedEntityInstance> teisResponse = call.call();
        assertThat(teisResponse).isEqualTo(teis);
    }
}
