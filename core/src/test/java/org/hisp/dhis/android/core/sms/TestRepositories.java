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

package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TestRepositories {

    public static class TestLocalDbRepository implements LocalDbRepository {
        public static String userId = "AIK2aQOJIbj";
        private String gatewayNumber = null;
        private String confirmationSenderNumber = null;
        private Integer resultWaitingTimeout = 120;
        public Metadata metadata;

        public TestLocalDbRepository() {
            this(new Metadata());
        }

        public TestLocalDbRepository(Metadata metadata) {
            this.metadata = metadata;
            metadata.lastSyncDate = new Date();
        }

        @Override
        public Single<String> getUserName() {
            return Single.fromCallable(() -> userId);
        }

        @Override
        public Single<String> getGatewayNumber() {
            return Single.fromCallable(() -> gatewayNumber);
        }

        @Override
        public Completable setGatewayNumber(String number) {
            return Completable.fromAction(() -> gatewayNumber = number);
        }

        @Override
        public Single<Integer> getWaitingResultTimeout() {
            return Single.fromCallable(() -> resultWaitingTimeout);
        }

        @Override
        public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
            return Completable.fromAction(() -> resultWaitingTimeout = timeoutSeconds);
        }

        @Override
        public Single<String> getConfirmationSenderNumber() {
            return Single.fromCallable(() -> confirmationSenderNumber);
        }

        @Override
        public Completable setConfirmationSenderNumber(String number) {
            return Completable.fromAction(() -> confirmationSenderNumber = number);
        }

        @Override
        public Completable updateSubmissionState(BaseDataModel event, State sentViaSms) {
            return Completable.complete();
        }

        @Override
        public Single<Metadata> getIdsLists() {
            return Single.fromCallable(() -> metadata);
        }
    }
}
