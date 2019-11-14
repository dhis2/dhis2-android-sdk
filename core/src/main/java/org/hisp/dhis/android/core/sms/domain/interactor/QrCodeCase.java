package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.converter.internal.DatasetConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.DeletionConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.RelationshipConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.SimpleEventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.TrackerEventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateSimpleEventCode(String eventUid) {
        return new SimpleEventConverter(localDbRepository, eventUid).readAndConvert();
    }

    public Single<String> generateTrackerEventCode(String eventUid) {
        return new TrackerEventConverter(localDbRepository, eventUid).readAndConvert();
    }

    public Single<String> generateEnrollmentCode(String enrollmentUid) {
        return new EnrollmentConverter(localDbRepository, enrollmentUid).readAndConvert();
    }

    public Single<String> generateDataSetCode(String dataSet,
                                              String orgUnit,
                                              String period,
                                              String attributeOptionComboUid) {
        return new DatasetConverter(
                localDbRepository,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid).readAndConvert();
    }

    public Single<String> generateRelationshipCode(String relationshipUid) {
        return new RelationshipConverter(localDbRepository, relationshipUid).readAndConvert();
    }

    public Single<String> generateDeletionCode(String itemToDeleteUid) {
        return new DeletionConverter(localDbRepository, itemToDeleteUid).readAndConvert();
    }
}
