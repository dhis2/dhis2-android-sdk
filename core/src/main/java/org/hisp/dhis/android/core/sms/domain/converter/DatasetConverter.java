package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.smscompression.models.AggregateDatasetSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class DatasetConverter extends Converter<List<DataValue>> {

    private final String orgUnit;
    private final String period;
    private final String attributeOptionComboUid;

    public DatasetConverter(LocalDbRepository localDbRepository,
                            String orgUnit,
                            String period,
                            String attributeOptionComboUid) {
        super(localDbRepository);
        this.orgUnit = orgUnit;
        this.period = period;
        this.attributeOptionComboUid = attributeOptionComboUid;
    }

    @Override
    Single<? extends SMSSubmission> convert(@NonNull List<DataValue> values, String user, int submissionId) {
        return Single.fromCallable(() -> {
            AggregateDatasetSMSSubmission subm = new AggregateDatasetSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setOrgUnit(orgUnit);
            subm.setPeriod(period);
            subm.setAttributeOptionCombo(attributeOptionComboUid);
            subm.setValues(translateValues(values));
            return subm;
        });
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<org.hisp.dhis.smscompression.models.DataValue> translateValues(List<DataValue> values) {
        ArrayList<org.hisp.dhis.smscompression.models.DataValue> list = new ArrayList<>();
        for (DataValue value : values) {
            list.add(new org.hisp.dhis.smscompression.models.DataValue(
                    value.categoryOptionCombo(),
                    value.dataElement(),
                    value.value()));
        }
        return list;
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateDataSetSubmissionState(orgUnit, period, attributeOptionComboUid, state);
    }

    @Override
    Single<List<DataValue>> readItemFromDb() {
        return getLocalDbRepository().getDataValues(orgUnit, period, attributeOptionComboUid);
    }
}
