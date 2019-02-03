package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.enrollment.Enrollment;

import java.util.Collection;

import io.reactivex.Single;

public class EnrollmentConverter extends Converter<Enrollment, Void> {
    @Override
    public String format(@NonNull Enrollment enrollment, Void params) {
        return null;
    }

    @Override
    public Collection<String> getConfirmationRequiredTexts(Enrollment dataObject) {
        return null;
    }

    @Override
    public Single<Void> getParamsTask() {
        return null;
    }
}
