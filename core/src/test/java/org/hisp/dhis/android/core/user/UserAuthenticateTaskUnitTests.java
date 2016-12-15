package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Task;
import org.hisp.dhis.android.models.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Call;

import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

@RunWith(JUnit4.class)
public class UserAuthenticateTaskUnitTests {

    @Mock
    private UserService userService;

    @Mock
    private UserStore userStore;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Call<User> userCall;

    @Mock
    private User user;

    private Task<Long> userAuthenticateTask;
    private String userCredentials;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Executor synchronousExecutor = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };

        userAuthenticateTask = new UserAuthenticateTask(synchronousExecutor,
                userService, userStore, userCredentialsStore, authenticatedUserStore,
                "test_user_name", "test_user_password");
        userCredentials = base64("test_user_name", "test_user_password");
    }

    @Test
    public void test() throws IOException {
        // when(user.uid()).thenReturn("test_user_uid");
        // when(userCall.execute()).thenReturn(Response.success(user));

        // when(userCall.execute().body()).thenReturn(user);
        // when(userService.authenticate("Basic " + userCredentials, User.uid)).thenReturn(userCall);
        // when(userService.authenticate(any(String.class), any(Property.class))).thenReturn(userCall);
        // when(authenticatedUserStore.save("test_user_uid", userCredentials)).thenReturn(1L);

        // Long userId = userAuthenticateTask.execute();
        // assertThat(userId).isEqualTo(1L);
    }
}
