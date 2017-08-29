package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class EnrollmentSynchronizer {
    //coordinate one type of item

    IEnrollmentRepository mEnrollmentRepository;
    IFailedItemRepository mFailedItemRepository;

    public EnrollmentSynchronizer(IEnrollmentRepository enrollmentRepository,
            IFailedItemRepository failedItemRepository) {
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
                mFailedItemRepository.clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
                return true;
            } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                mFailedItemRepository.handleImportSummaryError(importSummary, FailedItem.ENROLLMENT, 200, enrollment.getLocalId());
            }
        } catch (APIException api) {
            mFailedItemRepository.handleSerializableItemException(api, FailedItem.ENROLLMENT,
                    enrollment.getLocalId());
        }
        return false;
    }
}
