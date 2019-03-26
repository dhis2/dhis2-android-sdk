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
package org.hisp.dhis.android.core.trackedentity.search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceQueryOnlineShould {

    private TrackedEntityInstanceQuery.Builder queryBuilder;

    @Before
    public void setUp() {
        queryBuilder = TrackedEntityInstanceQuery.builder()
                .page(1)
                .pageSize(50)
                .paging(true)
                .orgUnits(Collections.singletonList("uid"));
    }

    @Test
    public void parse_query_in_api_format() {
        TrackedEntityInstanceQuery query = queryBuilder
                .query(QueryFilter.create(QueryOperator.LIKE, "filter"))
                .build();

        TrackedEntityInstanceQueryOnline onlineQuery = TrackedEntityInstanceQueryOnline.create(query);

        assertThat(onlineQuery.query()).isEqualTo("LIKE:filter");
    }

    @Test
    public void parse_attributes_in_api_format() {
        TrackedEntityInstanceQuery query = queryBuilder
                .attribute(Arrays.asList(
                        QueryItem.create("attribute1"),
                        QueryItem.create("attribute2",
                                Collections.singletonList(QueryFilter.create("filter21"))),
                        QueryItem.create("attribute3",
                                Arrays.asList(
                                        QueryFilter.create("filter31"),
                                        QueryFilter.create(QueryOperator.EQ, "filter32")))
                )).build();

        TrackedEntityInstanceQueryOnline onlineQuery = TrackedEntityInstanceQueryOnline.create(query);

        assertThat(onlineQuery.attribute().size()).isEqualTo(3);
        assertThat(onlineQuery.attribute()).contains("attribute1");
        assertThat(onlineQuery.attribute()).contains("attribute2:EQ:filter21");
        assertThat(onlineQuery.attribute()).contains("attribute3:EQ:filter31:EQ:filter32");
    }

    @Test
    public void parse_filters_in_api_format() {
        TrackedEntityInstanceQuery query = queryBuilder
                .filter(Arrays.asList(
                        QueryItem.create("filterItem1"),
                        QueryItem.create("filterItem2",
                                Collections.singletonList(QueryFilter.create("filter21"))),
                        QueryItem.create("filterItem3",
                                Arrays.asList(
                                        QueryFilter.create("filter31"),
                                        QueryFilter.create(QueryOperator.EQ, "filter32")))
                )).build();

        TrackedEntityInstanceQueryOnline onlineQuery = TrackedEntityInstanceQueryOnline.create(query);

        assertThat(onlineQuery.filter().size()).isEqualTo(3);
        assertThat(onlineQuery.filter()).contains("filterItem1");
        assertThat(onlineQuery.filter()).contains("filterItem2:EQ:filter21");
        assertThat(onlineQuery.filter()).contains("filterItem3:EQ:filter31:EQ:filter32");
    }
}
