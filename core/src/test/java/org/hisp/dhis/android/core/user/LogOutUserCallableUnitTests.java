package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class LogOutUserCallableUnitTests {

    @Mock
    private UserStore userStore;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    @Mock
    private OrganisationUnitStore organisationUnitStore;

    private Callable<Void> logOutUserCallable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        logOutUserCallable = new LogOutUserCallable(
                userStore, userCredentialsStore, userOrganisationUnitLinkStore,
                authenticatedUserStore, organisationUnitStore
        );
    }

    @Test
    public void logOut_shouldClearTables() throws Exception {
        logOutUserCallable.call();

        verify(userStore).delete();
        verify(userCredentialsStore).delete();
        verify(userOrganisationUnitLinkStore).delete();
        verify(authenticatedUserStore).delete();
        verify(organisationUnitStore).delete();
    }
}
