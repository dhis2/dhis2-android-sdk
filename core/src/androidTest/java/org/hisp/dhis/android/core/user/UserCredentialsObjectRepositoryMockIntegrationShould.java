package org.hisp.dhis.android.core.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsObjectRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_user() {
        UserCredentials userCredentials = d2.userModule().userCredentials.get();
        assertThat(userCredentials.username(), is("android"));
        assertThat(userCredentials.name(), is("John Barnes"));
    }

    @Test
    public void return_user_roles_as_children() {
        UserCredentials userCredentials = d2.userModule().userCredentials.getWithAllChildren();
        assertThat(userCredentials.userRoles().size(), is(1));
        assertThat(userCredentials.userRoles().get(0).name(), is("Superuser"));
    }
}