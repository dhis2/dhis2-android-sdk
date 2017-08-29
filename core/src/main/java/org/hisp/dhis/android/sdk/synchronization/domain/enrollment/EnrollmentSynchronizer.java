package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class EnrollmentSynchronizer extends Synchronizer {
    //coordinate one type of item

    IEnrollmentRepository mEnrollmentRepository;
    IFailedItemRepository mFailedItemRepository;

    public EnrollmentSynchronizer(IEnrollmentRepository enrollmentRepository,
            IFailedItemRepository failedItemRepository) {
        super(failedItemRepository);

        mEnrollmentRepository = enrollmentRepository;
        mFailedItemRepository = failedItemRepository;
    }

    public boolean sync(Enrollment enrollment) {
        try {
            ImportSummary importSummary = mEnrollmentRepository.sync(enrollment);

            if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.OK.equals(importSummary.getStatus())) {

                enrollment.setFromServer(true);
                mEnrollmentRepository.save(enrollment);
                super.clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
                return true;
            } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                super.handleImportSummaryError(importSummary, FailedItem.ENROLLMENT, 200, enrollment.getLocalId());
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.ENROLLMENT,
                    enrollment.getLocalId());
        }
        return false;
    }
}
