package org.hisp.dhis.android.core.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IsUserLoggedInCallableUnitTests {

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    @Mock
    private AuthenticatedUserModel authenticatedUser;

    private Callable<Boolean> isUserLoggedInCallable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        isUserLoggedInCallable = new IsUserLoggedInCallable(authenticatedUserStore);
    }

    @Test
    public void call_shouldReturnTrueIfAnyUsersArePersisted() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUser));

        Boolean isUserLoggedIn = isUserLoggedInCallable.call();

        assertThat(isUserLoggedIn).isTrue();
    }

    @Test
    public void call_shouldReturnFalseIfNoUsersArePersisted() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(new ArrayList<AuthenticatedUserModel>());

        Boolean isUserLoggedIn = isUserLoggedInCallable.call();

        assertThat(isUserLoggedIn).isFalse();
    }
}
