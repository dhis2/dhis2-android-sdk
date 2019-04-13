package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter.EventData;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.utils.Common;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Collection;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateTextCode(final Event event) {
        return Common.getCompressionData(localDbRepository).flatMap(data ->
                new EventConverter(data.metadata).format(
                        new EventData(event, data.user)
                )
        );
    }

    public Single<String> generateTextCode(final Enrollment enrollment,
                                           final String trackedEntityType,
                                           final Collection<TrackedEntityAttributeValue> attributes) {
        return Common.getCompressionData(localDbRepository).flatMap(data ->
                new EnrollmentConverter(data.metadata).format(
                        new EnrollmentData(enrollment, trackedEntityType, attributes, data.user)
                )
        );
    }
}
