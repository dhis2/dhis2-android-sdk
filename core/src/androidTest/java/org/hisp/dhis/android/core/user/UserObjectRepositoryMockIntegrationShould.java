package org.hisp.dhis.android.core.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class UserObjectRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_user() {
        User user = d2.userModule().user.get();
        assertThat(user.uid(), is("DXyJmlo9rge"));
        assertThat(user.firstName(), is("John"));
    }

    @Test
    public void return_user_credentials_as_children() {
        User user = d2.userModule().user.getWithAllChildren();
        assertThat(user.userCredentials().user().uid(), is(user.uid()));
        assertThat(user.userCredentials().username(), is("android"));
        assertThat(user.userCredentials().name(), is("John Barnes"));
    }

    @Test
    public void return_organisation_units_as_children() {
        User user = d2.userModule().user.getWithAllChildren();
        List<OrganisationUnit> organisationUnits = user.organisationUnits();
        assertThat(organisationUnits.size(), is(1));
        assertThat(organisationUnits.get(0).name(), is("Ngelehun CHC"));
    }
}