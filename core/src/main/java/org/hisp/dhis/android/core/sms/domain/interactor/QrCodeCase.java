package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;

import java.util.Collection;
import java.util.List;

import io.reactivex.Single;

public class QrCodeCase {

    public Single<String> generateTextCode(final EventModel event, final List<TrackedEntityDataValueModel> values) {
        return Single.fromCallable(() -> new EventConverter().format(
                new EventConverter.EventData(event, values)
        ));
    }

    public Single<String> generateTextCode(final EnrollmentModel enrollmentModel,
                                           final Collection<TrackedEntityAttributeValueModel> attributes) {
        return Single.fromCallable(() -> new EnrollmentConverter().format(
                new EnrollmentConverter.EnrollmentData(enrollmentModel, attributes)
        ));
    }
}
