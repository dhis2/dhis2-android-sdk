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

package org.hisp.dhis.client.sdk.android.api.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.utils.CollectionUtils;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.IStore;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkUtils {

    private NetworkUtils() {
        // no instances
    }

    public static <T extends IModel> boolean handleApiException(
            ApiException exception, T model, IStore<T> store) throws ApiException {

        switch (exception.getKind()) {
            case HTTP: {
                switch (exception.getResponse().getStatus()) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        // user credentials are not valid
                    case HttpURLConnection.HTTP_FORBIDDEN: {
                        // client does not have access to server
                        // for example, oAuth2 token may expire
                        throw exception;
                    }

                    // given resource was removed, react accordingly
                    case HttpURLConnection.HTTP_NOT_FOUND: {
                        if (store != null && model != null) {
                            store.delete(model);
                            return true;
                        }
                    }

                    // return control to client code,
                    // conflict should be resolved
                    case HttpURLConnection.HTTP_CONFLICT: {
                        return false;
                    }

                    case HttpURLConnection.HTTP_BAD_REQUEST:
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    case HttpURLConnection.HTTP_NOT_IMPLEMENTED: {
                        // log error
                        throw exception;
                    }
                    default: {
                        throw exception;
                    }
                }
            }
            // if it is a network problem (like timeout or something else, do we really
            // want to continue execution? or should we retry request once?
            case NETWORK: {
                throw exception;
            }
            case CONVERSION:
            case UNEXPECTED: {
                // These types of errors are considered to be unrecoverable,
                throw exception;
            }
            default: {
                throw exception;
            }
        }
    }

    @NonNull
    public static <T> List<T> getCollection(
            @NonNull ApiResource<T> apiResource, @NonNull Fields fields,
            @Nullable DateTime lastUpdated, @Nullable String... uids) {

        Map<String, String> queryMap = new HashMap<>();
        List<String> filters = new ArrayList<>();

        /* disable paging */
        queryMap.put("paging", "false");

        /* filter programs by lastUpdated field */
        if (lastUpdated != null) {
            filters.add("lastUpdated:gt:" + lastUpdated.toString());
        }

        switch (fields) {
            case BASIC: {
                queryMap.put("fields", apiResource.getBasicProperties());
                break;
            }
            case ALL: {
                queryMap.put("fields", apiResource.getAllProperties());
                break;
            }
        }

        List<T> allPrograms = new ArrayList<>();
        if (uids != null && uids.length > 0) {

            // splitting up request into chunks
            List<String> idFilters = buildIdFilter(uids);
            for (String idFilter : idFilters) {
                List<String> combinedFilters = new ArrayList<>(filters);
                combinedFilters.add(idFilter);

                // downloading subset of programs
                allPrograms.addAll(unwrap(call(apiResource
                        .getEntities(queryMap, combinedFilters)), apiResource.getResourceName()));
            }
        } else {
            allPrograms.addAll(unwrap(call(

                    apiResource.getEntities(queryMap, filters)), apiResource.getResourceName()));
        }

        return allPrograms;
    }

    private static List<String> buildIdFilter(String[] ids) {
        List<String> idFilters = new ArrayList<>();

        if (ids != null && ids.length > 0) {
            List<List<String>> splittedIds = CollectionUtils.slice(Arrays.asList(ids), 64);
            for (List<String> listOfIds : splittedIds) {
                StringBuilder builder = new StringBuilder();
                idFilters.add(builder.append("id:in:[")
                        .append(CollectionUtils.join(listOfIds, ","))
                        .append("]")
                        .toString());
            }
        }

        return idFilters;
    }

    @Nullable
    public static <T> T call(@NonNull Call<T> call) {
        Response<T> response = null;
        ApiException apiException = null;

        try {
            response = call.execute();
        } catch (IOException ioException) {
            apiException = ApiException.networkError(null, ioException);
        }

        if (apiException != null) {
            throw apiException;
        }

        if (!(response.code() >= 200 && response.code() < 300)) {
            throw ApiException.httpError(response.raw().request().url().toString(),
                    ResponseMapper.fromOkResponse(response.raw()));
        }

        return response.body();
    }

    @NonNull
    public static <T> List<T> unwrap(@Nullable Map<String, List<T>> response, @NonNull String key) {
        if (response != null && response.containsKey(key) && response.get(key) != null) {
            return response.get(key);
        } else {
            return new ArrayList<>();
        }
    }
}
