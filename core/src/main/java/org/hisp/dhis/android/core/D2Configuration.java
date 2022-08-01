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

package org.hisp.dhis.android.core;

import android.content.Context;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Interceptor;

@AutoValue
public abstract class D2Configuration {

    @Nullable
    public abstract String appName();

    @Nullable
    public abstract String appVersion();

    @NonNull
    public abstract Integer readTimeoutInSeconds();

    @NonNull
    public abstract Integer connectTimeoutInSeconds();

    @NonNull
    public abstract Integer writeTimeoutInSeconds();

    @NonNull
    public abstract List<Interceptor> interceptors();

    @NonNull
    public abstract List<Interceptor> networkInterceptors();

    @NonNull
    public abstract Context context();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_D2Configuration.Builder()
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(Collections.emptyList())
                .interceptors(Collections.emptyList());
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder appName(String appName);

        public abstract Builder appVersion(String appVersion);

        public abstract Builder context(Context context);

        public abstract Builder readTimeoutInSeconds(Integer readTimeoutInSeconds);

        public abstract Builder connectTimeoutInSeconds(Integer connectTimeoutInSeconds);

        public abstract Builder writeTimeoutInSeconds(Integer writeTimeoutInSeconds);

        public abstract Builder interceptors(List<Interceptor> interceptors);

        public abstract Builder networkInterceptors(List<Interceptor> networkInterceptors);

        public abstract D2Configuration build();
    }
}