package org.hisp.dhis.android.core.enrollment;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EnrollmentModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
        downloadTrackedEntityInstances();
    }

    @Test
    public void allow_access_to_all_enrollments_without_children() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments.get();
        assertThat(enrollments.size(), is(1));
        for (Enrollment enrollment: enrollments) {
            assertThat(enrollment.uid(), is("JILLTkO4LKQ"));
            assertThat(enrollment.program(), is("lxAQ7Zs9VYR"));
            assertThat(enrollment.events() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_one_enrollment_without_children() {
        Enrollment enrollment = d2.enrollmentModule().enrollments.uid("JILLTkO4LKQ").get();
        assertThat(enrollment.uid(), is("JILLTkO4LKQ"));
        assertThat(enrollment.program(), is("lxAQ7Zs9VYR"));
        assertThat(enrollment.events() == null, is(true));
    }
}