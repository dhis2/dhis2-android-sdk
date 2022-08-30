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
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.smscompression.models.AggregateDatasetSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSDataValue;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class DatasetConverter extends Converter<SMSDataValueSet> {

    private final String dataSet;
    private final String orgUnit;
    private final String period;
    private final String attributeOptionComboUid;

    public DatasetConverter(LocalDbRepository localDbRepository,
                            DHISVersionManager dhisVersionManager,
                            String dataSet,
                            String orgUnit,
                            String period,
                            String attributeOptionComboUid) {
        super(localDbRepository, dhisVersionManager);
        this.dataSet = dataSet;
        this.orgUnit = orgUnit;
        this.period = period;
        this.attributeOptionComboUid = attributeOptionComboUid;
    }

    @Override
    Single<? extends SMSSubmission> convert(@NonNull SMSDataValueSet dataValueSet, String user, int submissionId) {
        return Single.fromCallable(() -> {
            AggregateDatasetSMSSubmission subm = new AggregateDatasetSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setOrgUnit(orgUnit);
            subm.setPeriod(period);
            subm.setDataSet(dataSet);
            subm.setAttributeOptionCombo(attributeOptionComboUid);
            subm.setValues(translateValues(dataValueSet.dataValues()));
            subm.setComplete(dataValueSet.completed());
            return subm;
        });
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<SMSDataValue> translateValues(Collection<DataValue> values) {
        ArrayList<SMSDataValue> list = new ArrayList<>();
        for (DataValue value : values) {
            list.add(new SMSDataValue(
                    value.categoryOptionCombo(),
                    value.dataElement(),
                    value.value()));
        }
        return list;
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateDataSetSubmissionState(
                dataSet, orgUnit, period, attributeOptionComboUid, state);
    }

    @Override
    Single<SMSDataValueSet> readItemFromDb() {
        return getLocalDbRepository().getDataValueSet(dataSet, orgUnit, period, attributeOptionComboUid);
    }
}
