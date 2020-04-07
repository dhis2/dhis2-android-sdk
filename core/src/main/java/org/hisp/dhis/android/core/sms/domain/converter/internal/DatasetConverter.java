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
