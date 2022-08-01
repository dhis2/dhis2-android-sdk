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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.smscompression.models.RelationshipSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RelationshipConverter extends Converter<Relationship> {
    private final String relationshipUid;

    public RelationshipConverter(LocalDbRepository localDbRepository,
                                 DHISVersionManager dhisVersionManager,
                                 String relationshipUid) {
        super(localDbRepository, dhisVersionManager);
        this.relationshipUid = relationshipUid;
    }

    @Override
    Single<? extends SMSSubmission> convert(@NonNull Relationship relationship, String user, int submissionId) {
        return Single.fromCallable(() -> {
            RelationshipSMSSubmission subm = new RelationshipSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setRelationship(relationship.uid());
            subm.setRelationshipType(relationship.relationshipType());
            subm.setFrom(relationship.from().elementUid());
            subm.setTo(relationship.to().elementUid());
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateRelationshipSubmissionState(relationshipUid, state);
    }

    @Override
    Single<Relationship> readItemFromDb() {
        return getLocalDbRepository().getRelationship(relationshipUid);
    }
}
