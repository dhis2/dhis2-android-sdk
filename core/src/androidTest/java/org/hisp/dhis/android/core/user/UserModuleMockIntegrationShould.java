package org.hisp.dhis.android.core.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class UserModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        login();
    }

    @Test
    public void allow_access_to_authenticated_user() {
        AuthenticatedUserModel authenticatedUser = d2.userModule().authenticatedUser.get();
        assertThat(authenticatedUser.user(), is("DXyJmlo9rge"));
        assertThat(authenticatedUser.credentials(), is("YW5kcm9pZDpBbmRyb2lkMTIz"));
    }

    @Test
    public void allow_access_to_user_credentials() {
        UserCredentials credentials = d2.userModule().userCredentials.get();
        assertThat(credentials.username(), is("android"));
        assertThat(credentials.code(), is("android"));
        assertThat(credentials.name(), is("John Barnes"));
    }

    @Test
    public void allow_access_to_user_role() {
        UserRole userRole = d2.userModule().userRole.get();
        assertThat(userRole.uid(), is("Ufph3mGRmMo"));
        assertThat(userRole.name(), is("Superuser"));
        assertThat(userRole.displayName(), is("Superuser"));
    }

    @Test
    public void allow_access_to_user() {
        User user = d2.userModule().user.get();
        assertThat(user.uid(), is("DXyJmlo9rge"));
        assertThat(user.firstName(), is("John"));
        assertThat(user.email(), is("john@hmail.com"));
    }
}