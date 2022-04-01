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
package org.hisp.dhis.android.core.configuration.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfigurationUtil.buildUserConfiguration
import org.junit.Test

class DatabasesConfigurationHelperShould {
    private val nameGenerator = DatabaseNameGenerator()
    private val url1 = "https://url1.org"
    private val url2 = "https://url2.org"
    private val username1 = "u1"
    private val username2 = "u2"
    private val dbName11 = nameGenerator.getDatabaseName(url1, username1, false)
    private val dbName12 = nameGenerator.getDatabaseName(url1, username2, false)
    private val dbName22 = nameGenerator.getDatabaseName(url2, username2, false)

    private val userConfig11 = DatabaseAccount.builder()
        .username(username1)
        .serverUrl(url1)
        .databaseName(dbName11)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val userConfig12 = DatabaseAccount.builder()
        .username(username2)
        .serverUrl(url1)
        .databaseName(dbName12)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val userConfig22 = DatabaseAccount.builder()
        .username(username2)
        .serverUrl(url2)
        .databaseName(dbName22)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val singleServerSingleUserConfig = DatabasesConfiguration.builder()
        .accounts(listOf(userConfig11))
        .build()

    private val singleServer2UserConfig = DatabasesConfiguration.builder()
        .accounts(listOf(userConfig11, userConfig12))
        .build()

    private val twoServerOneUserEach = DatabasesConfiguration.builder()
        .accounts(listOf(userConfig11, userConfig22))
        .build()

    private val twoServerTwoUsersFirst = DatabasesConfiguration.builder()
        .accounts(listOf(userConfig11, userConfig12, userConfig22))
        .build()

    private val helper = DatabaseConfigurationHelper(
        nameGenerator,
        object : DateProvider {
            override val dateStr: String = DATE
        }
    )

    @Test
    fun get_logged_configuration_when_one_server_user() {
        assertThat(
            DatabaseConfigurationHelper.getLoggedAccount(singleServerSingleUserConfig, username1, url1)
        ).isSameInstanceAs(userConfig11)
    }

    @Test
    fun get_logged_configuration_when_one_server_and_2_users() {
        assertThat(DatabaseConfigurationHelper.getLoggedAccount(singleServer2UserConfig, username1, url1))
            .isSameInstanceAs(userConfig11)
        assertThat(DatabaseConfigurationHelper.getLoggedAccount(singleServer2UserConfig, username2, url1))
            .isSameInstanceAs(userConfig12)
    }

    @Test
    fun get_logged_configuration_when_2_servers_with_one_user_on_each_1() {
        assertThat(DatabaseConfigurationHelper.getLoggedAccount(twoServerOneUserEach, username1, url1))
            .isSameInstanceAs(userConfig11)
    }

    @Test(expected = RuntimeException::class)
    fun get_logged_configuration_when_2_servers_with_one_user_on_each_2() {
        DatabaseConfigurationHelper.getLoggedAccount(twoServerOneUserEach, username2, url1)
    }

    @Test
    fun get_logged_configuration_when_2_servers_with_two_users_in_first() {
        assertThat(DatabaseConfigurationHelper.getLoggedAccount(twoServerTwoUsersFirst, username1, url1))
            .isSameInstanceAs(userConfig11)
        assertThat(DatabaseConfigurationHelper.getLoggedAccount(twoServerTwoUsersFirst, username2, url1))
            .isSameInstanceAs(userConfig12)
    }

    @Test
    fun add_new_configuration_to_empty() {
        val config = helper.addAccount(null, url1, username1, false)
        assertThat(config).isEqualTo(singleServerSingleUserConfig)
    }

    @Test
    fun add_new_configuration_to_single_server_single_user_in_same_server() {
        val config = helper.addAccount(singleServerSingleUserConfig, url1, username2, false)
        assertThat(config).isEqualTo(singleServer2UserConfig)
    }

    @Test
    fun add_new_configuration_to_single_server_single_user_in_other_server() {
        val config = helper.addAccount(singleServerSingleUserConfig, url2, username2, false)
        assertThat(
            DatabaseConfigurationHelper
                .getLoggedAccount(config, username2, url2)
        ).isEqualTo(userConfig22)
    }

    @Test
    fun get_oldest_configuration() {
        val accounts = listOf(
            buildUserConfiguration("user1", "2021-06-01T00:01:04.000"),
            buildUserConfiguration("user2", "2021-09-02T00:01:04.000"),
            buildUserConfiguration("user3", "2020-08-05T00:01:04.000"),
            buildUserConfiguration("user4", "2020-08-09T00:01:04.000")
        )

        val keepFiveAccounts = DatabaseConfigurationHelper.getOldestAccounts(accounts, 5)
        assertThat(keepFiveAccounts).isEmpty()

        val keepFourAccounts = DatabaseConfigurationHelper.getOldestAccounts(accounts, 4)
        assertThat(keepFourAccounts).isEmpty()

        val keepThreeAccounts = DatabaseConfigurationHelper.getOldestAccounts(accounts, 3)
        assertThat(keepThreeAccounts.size).isEqualTo(1)
        assertThat(keepThreeAccounts.first().username()).isEqualTo("user3")

        val keepOneAccount = DatabaseConfigurationHelper.getOldestAccounts(accounts, 1)
        assertThat(keepOneAccount.size).isEqualTo(3)
        assertThat(keepOneAccount.map { it.username() }).containsExactlyElementsIn(listOf("user1", "user3", "user4"))

        val keepZeroAccounts = DatabaseConfigurationHelper.getOldestAccounts(accounts, 0)
        assertThat(keepZeroAccounts.size).isEqualTo(4)
    }

    @Test
    fun get_null_oldest_configuration_if_empty() {
        assertThat(DatabaseConfigurationHelper.getOldestAccounts(emptyList(), 1)).isEmpty()
    }

    companion object {
        private const val DATE = "2014-06-06T20:44:21.375"
    }
}
