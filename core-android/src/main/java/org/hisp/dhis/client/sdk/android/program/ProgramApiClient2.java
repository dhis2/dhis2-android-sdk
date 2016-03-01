/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.IProgramApiClient;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.android.api.utils.NetworkUtils.call;
import static org.hisp.dhis.client.sdk.android.api.utils.NetworkUtils.unwrap;

public class ProgramApiClient2 implements IProgramApiClient {
    /* amount of programs which we should get by each request */
    private static final int PROGRAMS_PER_REQUEST = 64;

    /* Retrofit implementation of the client */
    private final IProgramApiClientRetrofit programApiClientRetrofit;

    public ProgramApiClient2(IProgramApiClientRetrofit programApiClientRetrofit) {
        this.programApiClientRetrofit = programApiClientRetrofit;
    }

    @Override
    public List<Program> getPrograms(Fields fields, DateTime lastUpdated,
                                     String... ids) throws ApiException {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("paging", "false");

        /* filter programs by lastUpdated field */
        if (lastUpdated != null) {
            queryMap.put("lastUpdated", lastUpdated.toString());
        }

        switch (fields) {
            case BASIC: {
                queryMap.put("fields", "id,displayName");
                break;
            }
            case ALL: {
                queryMap.put("fields", "id,displayName");
                break;
            }
        }

        List<Program> allPrograms = new ArrayList<>();
        if (ids != null && ids.length > 0) {

            List<String> filters = buildIdFilter(ids);
            for (String filter : filters) {
                // we need to avoid mutating existing map
                Map<String, String> queryMapWithIds = new HashMap<>(queryMap);
                queryMapWithIds.put("filter", "id:in:" + filter);

                // downloading subset of programs
                allPrograms.addAll(unwrap(call(programApiClientRetrofit
                        .getPrograms(queryMapWithIds)), "programs"));
            }
        } else {
            allPrograms.addAll(unwrap(call(programApiClientRetrofit
                    .getPrograms(queryMap)), "programs"));
        }

        return allPrograms;
    }

    private static List<String> buildIdFilter(String[] ids) {
        List<String> idFilters = new ArrayList<>();

        if (ids != null && ids.length > 0) {
            List<List<String>> splittedIds = sliceList(Arrays.asList(ids), PROGRAMS_PER_REQUEST);

            for (List<String> listOfIds : splittedIds) {
                StringBuilder builder = new StringBuilder();
                idFilters.add(builder.append("[")
                        .append(join(listOfIds))
                        .append("]")
                        .toString());
            }
        }

        return idFilters;
    }

    private static List<List<String>> sliceList(List<String> stringList, int subListSize) {
        List<List<String>> listOfSubLists = new ArrayList<>();

        int leftBoundary = 0;
        int rightBoundary = subListSize < stringList.size() ? subListSize : stringList.size();

        do {
            listOfSubLists.add(stringList.subList(leftBoundary, rightBoundary));

            leftBoundary = rightBoundary;
            rightBoundary = rightBoundary + subListSize < stringList.size() ?
                    rightBoundary + subListSize : stringList.size();
        } while (leftBoundary != rightBoundary);

        return listOfSubLists;
    }

    private static String join(List<String> strings) {
        if (strings == null) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();
        Iterator<? extends String> iterator = strings.iterator();

        if (iterator.hasNext()) {
            buffer.append(iterator.next());

            while (iterator.hasNext()) {
                buffer.append(",").append(iterator.next());
            }
        }


        return buffer.toString();
    }
}
