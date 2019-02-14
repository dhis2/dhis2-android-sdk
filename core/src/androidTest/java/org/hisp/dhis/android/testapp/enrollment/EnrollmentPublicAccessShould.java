package org.hisp.dhis.android.testapp.enrollment;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class EnrollmentPublicAccessShould extends BasePublicAccessShould<Enrollment> {

    @Mock
    private Enrollment object;

    @Override
    public Enrollment object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Enrollment.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Enrollment.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}