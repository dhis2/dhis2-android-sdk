/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.utils;

import org.hisp.dhis.java.sdk.common.network.ApiException;
import org.hisp.dhis.android.sdk.retrofit.ResponseMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Response;

public class NetworkUtils {
    private NetworkUtils() {
        // no instances
    }

    public static <T> T call(Call<T> call) {
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

        if (!response.isSuccess()) {
            throw ApiException.httpError(response.raw().request().urlString(),
                    ResponseMapper.fromOkResponse(response.raw()));
        }

        return response.body();
    }

    public static <T> List<T> unwrap(Map<String, List<T>> response, String key) {
        if (response != null && response.containsKey(key) && response.get(key) != null) {
            return response.get(key);
        } else {
            return new ArrayList<>();
        }
    }
}
