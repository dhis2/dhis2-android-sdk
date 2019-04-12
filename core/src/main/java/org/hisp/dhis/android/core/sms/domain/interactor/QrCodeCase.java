package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter.EventData;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.utils.Pair;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.Collection;
import java.util.List;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateTextCode(final Event event, final List<TrackedEntityDataValue> values) {
        return new EventConverter().format(new EventData(event, values));
    }

    public Single<String> generateTextCode(final Enrollment enrollment,
                                           final String trackedEntityType,
                                           final Collection<TrackedEntityAttributeValue> attributes) {
        return Single.zip(
                localDbRepository.getMetadataIds(),
                localDbRepository.getUserName(),
                Pair::create
        ).flatMap(pair ->
                new EnrollmentConverter(pair.first).format(
                        new EnrollmentData(enrollment, trackedEntityType, attributes, pair.second)
                )
        );
    }
}
