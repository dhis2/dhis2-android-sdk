package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter.EventData;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.utils.Pair;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;

import java.util.Collection;
import java.util.List;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateTextCode(final EventModel event, final List<TrackedEntityDataValueModel> values) {
        return new EventConverter().format(new EventData(event, values));
    }

    public Single<String> generateTextCode(final EnrollmentModel enrollmentModel,
                                           final Collection<TrackedEntityAttributeValueModel> attributes) {
        return Single.zip(
                localDbRepository.getIdsLists(),
                localDbRepository.getUserName(),
                Pair::create
        ).flatMap(pair ->
                new EnrollmentConverter(pair.first).format(
                        new EnrollmentData(enrollmentModel, attributes, pair.second)
                )
        );
    }
}
