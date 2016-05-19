/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.job;

import org.hisp.dhis.android.sdk.network.ResponseHolder;
import org.hisp.dhis.android.sdk.network.SessionManager;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

public abstract class NetworkJob<T> extends Job<ResponseHolder<T>> {
    private final ResourceType mResourceType;

    public NetworkJob(int jobId, ResourceType responseType) {
        super(jobId);

        mResourceType = responseType;
    }

    @Override
    public final ResponseHolder<T> inBackground() {
        ResponseHolder<T> holder = new ResponseHolder<>();
        try {
            T item = execute();
            holder.setItem(item);
        } catch (APIException exception) {
            holder.setApiException(exception);
        }
        return holder;
    }

    @Override
    public final void onFinish(ResponseHolder<T> result) {
        SessionManager.getInstance()
                .setResourceTypeSynced(mResourceType);
        Dhis2Application.getEventBus().post(
                new NetworkJobResult<>(mResourceType, result));
    }

    public static class NetworkJobResult<Type> {
        private final ResourceType mResourceType;
        private final ResponseHolder<Type> mResponseHolder;

        public NetworkJobResult(ResourceType resourceType,
                                ResponseHolder<Type> responseHolder) {
            mResourceType = resourceType;
            mResponseHolder = responseHolder;
        }

        public ResourceType getResourceType() {
            return mResourceType;
        }

        public ResponseHolder<Type> getResponseHolder() {
            return mResponseHolder;
        }
    }

    public abstract T execute() throws APIException;

}
