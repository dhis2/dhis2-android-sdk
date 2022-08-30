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

package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.converter.internal.DatasetConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.DeletionConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.RelationshipConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.SimpleEventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.TrackerEventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;
    private final DHISVersionManager dhisVersionManager;

    public QrCodeCase(LocalDbRepository localDbRepository,
                      DHISVersionManager dhisVersionManager) {
        this.localDbRepository = localDbRepository;
        this.dhisVersionManager = dhisVersionManager;
    }

    /**
     * Get a compressed representation of a simple event.
     * @param eventUid Event uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateSimpleEventCode(String eventUid) {
        return new SimpleEventConverter(localDbRepository, dhisVersionManager, eventUid).readAndConvert();
    }

    /**
     * Get a compressed representation of a tracker event.
     * @param eventUid Event uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateTrackerEventCode(String eventUid) {
        return new TrackerEventConverter(localDbRepository, dhisVersionManager, eventUid).readAndConvert();
    }

    /**
     * Get a compressed representation of an enrollment.
     * @param enrollmentUid Enrollment uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateEnrollmentCode(String enrollmentUid) {
        return new EnrollmentConverter(localDbRepository, dhisVersionManager, enrollmentUid).readAndConvert();
    }

    /**
     * Get a compressed representations of a data value set.
     * @param dataSet DataSet uid.
     * @param orgUnit Organisation unit uid.
     * @param period Period identifier.
     * @param attributeOptionComboUid Attribute option combo uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateDataSetCode(String dataSet,
                                              String orgUnit,
                                              String period,
                                              String attributeOptionComboUid) {
        return new DatasetConverter(
                localDbRepository,
                dhisVersionManager,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid).readAndConvert();
    }

    /**
     * Get a compressed representation of a relationship.
     * @param relationshipUid Relationship uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateRelationshipCode(String relationshipUid) {
        return new RelationshipConverter(localDbRepository, dhisVersionManager, relationshipUid).readAndConvert();
    }

    /**
     * Get a compressed representation of the deletion of an event.
     * @param itemToDeleteUid Event uid.
     * @return {@code Single} with the compressed representation.
     */
    public Single<String> generateDeletionCode(String itemToDeleteUid) {
        return new DeletionConverter(localDbRepository, dhisVersionManager, itemToDeleteUid).readAndConvert();
    }
}
