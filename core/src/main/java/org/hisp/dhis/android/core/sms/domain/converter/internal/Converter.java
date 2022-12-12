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

package org.hisp.dhis.android.core.sms.domain.converter.internal;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SMSVersion;
import org.hisp.dhis.smscompression.SMSSubmissionWriter;
import org.hisp.dhis.smscompression.models.SMSMetadata;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public abstract class Converter<P> {
    private final LocalDbRepository localDbRepository;
    private final DHISVersionManager dhisVersionManager;

    Converter(LocalDbRepository localDbRepository,
              DHISVersionManager dhisVersionManager) {
        this.localDbRepository = localDbRepository;
        this.dhisVersionManager = dhisVersionManager;
    }

    public Single<String> readAndConvert() {
        return readAndConvert(0);
    }

    public Single<String> readAndConvert(int submissionId) {
        return Single.zip(
                localDbRepository.getMetadataIds(),
                localDbRepository.getUserName(),
                readItemFromDb(),
                CompressionData::new
        ).flatMap(
                d -> convert(d.item, d.metadata, d.user, submissionId)
        );
    }

    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    private Single<String> convert(@NonNull P dataItem, SMSMetadata metadata, String user, Integer submissionId) {
        return convert(dataItem, user, submissionId).map(submission -> {
            SMSSubmissionWriter writer = new SMSSubmissionWriter(metadata);
            SMSVersion smsVersion = dhisVersionManager.getSmsVersion();
            if (smsVersion == null) {
                throw D2Error.builder()
                        .errorCode(D2ErrorCode.SMS_NOT_SUPPORTED)
                        .errorDescription("SMS is not supported in version " + dhisVersionManager.getPatchVersion())
                        .errorComponent(D2ErrorComponent.SDK)
                        .build();
            }
            return base64(writer.compress(submission, smsVersion.getIntValue()));
        });
    }

    @SuppressLint("NewApi")
    private String base64(byte[] bytes) {
        String encoded;
        try {
            encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Throwable t) {
            encoded = null;
            // not android, so will try with pure java
        }
        if (encoded == null) {
            encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return encoded;
    }

    LocalDbRepository getLocalDbRepository() {
        return localDbRepository;
    }

    abstract Single<? extends SMSSubmission> convert(@NonNull P dataItem, String user, int submissionId);

    public abstract Completable updateSubmissionState(State state);

    abstract Single<P> readItemFromDb();

    private class CompressionData {
        final String user;
        final SMSMetadata metadata;
        final P item;

        CompressionData(SMSMetadata metadata, String user, P item) {
            this.user = user;
            this.metadata = metadata;
            this.item = item;
        }
    }
}
