/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.truth.ComparableSubject;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.junit.Ignore;
import org.junit.Test;

import io.reactivex.observers.TestObserver;

@Ignore("Tests with real servers. Depend on server state and network connection.")
public class LoginErrorHandlingRealIntegrationShould extends BaseRealIntegrationTest {

    @Test
    public void succeed_for_android_current() {
        assertSuccess(RealServerMother.android_current);
    }

    @Test
    public void succeed_for_android_previous1() {
        assertSuccess(RealServerMother.android_previous1);
    }

    @Test
    public void succeed_for_android_previous2() {
        assertSuccess(RealServerMother.android_previous2);
    }

    @Test
    public void succeed_for_2_30() {
        assertSuccess(RealServerMother.url2_30);
    }

    @Test
    public void succeed_for_2_31() {
        assertSuccess(RealServerMother.url2_31);
    }

    @Test
    public void succeed_for_2_32() {
        assertSuccess(RealServerMother.url2_32);
    }

    @Test
    public void succeed_for_2_33() {
        assertSuccess(RealServerMother.url2_33);
    }
    @Test
    public void succeed_for_2_34() {
        assertSuccess(RealServerMother.url2_34);
    }

    @Test
    public void fail_with_bad_credentials_for_android_current() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.android_current).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_android_previous1() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.android_previous1).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_android_previous2() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.android_previous2).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_2_30() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.url2_30).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_2_31() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.url2_31).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_2_32() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.url2_32).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_2_33() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.url2_33).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_bad_credentials_for_2_34() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.url2_34).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_no_dhis_server_for_android_current_with_http() {
        assertThatErrorCode("http://play.dhis2.org/android-current/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_with_https() {
        assertThatErrorCode("https://test.hiskenya.org").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_with_http() {
        assertThatErrorCode("https://test.hiskenya.org").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_google() {
        assertThatErrorCode("https://www.google.com").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    /**
     * Unfortunately it's not possible to differentiate a server that can't be resolved from an actual one
     * when we are offline
     */
    @Test
    public void fail_with_no_authenticated_user_offline_non_existent_server() {
        assertThatErrorCode("https://www.ddkfwefwefkwefowgwekfnwefwefjwe.com").isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE);
    }

    @Test
    public void fail_with_no_dhis_server_for_play_base_url() {
        assertThatErrorCode("https://play.dhis2.org/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_dhiske_with_http() {
        assertThatErrorCode("http://test.hiskenya.org/dhiske/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_bad_credentials_for_his_kenya_dhiske() {
        assertThatErrorCode("https://test.hiskenya.org/dhiske/").isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_url_maloformed_for_malformed_url() {
        assertThatErrorCode("asdasdwueihfew").isEqualTo(D2ErrorCode.SERVER_URL_MALFORMED);
    }

    @Test
    public void fail_with_url_maloformed_for_url_without_protocol() {
        assertThatErrorCode("play.dhis2.org/android-current/").isEqualTo(D2ErrorCode.SERVER_URL_MALFORMED);
    }

    private ComparableSubject<D2ErrorCode> assertThatErrorCode(String serverUrl) {
        return assertThatErrorCode(username, password, serverUrl);
    }

    private void assertSuccess(String serverUrl) {
        TestObserver<User> testObserver = d2.userModule().logIn(username, password, serverUrl).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertComplete();
    }

    private ComparableSubject<D2ErrorCode> assertThatErrorCode(String username, String password, String serverUrl) {
        TestObserver<User> testObserver = d2.userModule().logIn(username, password, serverUrl).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertError(D2Error.class);
        D2Error d2Error = (D2Error) testObserver.errors().get(0);
        return assertThat(d2Error.errorCode());
    }
}
