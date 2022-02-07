/*
 *  Copyright (c) 2004-2021, University of Oslo
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

    private val userConfig11 = DatabaseUserConfiguration.builder()
        .username(username1)
        .serverUrl(url1)
        .databaseName(dbName11)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val userConfig12 = DatabaseUserConfiguration.builder()
        .username(username2)
        .serverUrl(url1)
        .databaseName(dbName12)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val userConfig22 = DatabaseUserConfiguration.builder()
        .username(username2)
        .serverUrl(url2)
        .databaseName(dbName22)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val singleServerSingleUserConfig = DatabasesConfiguration.builder()
        .users(listOf(userConfig11))
        .build()

    private val singleServer2UserConfig = DatabasesConfiguration.builder()
        .users(listOf(userConfig11, userConfig12))
        .build()

    private val twoServerOneUserEach = DatabasesConfiguration.builder()
        .users(listOf(userConfig11, userConfig22))
        .build()

    private val twoServerTwoUsersFirst = DatabasesConfiguration.builder()
        .users(listOf(userConfig11, userConfig12, userConfig22))
        .build()

    private val helper = DatabaseConfigurationHelper(
        nameGenerator,
        object : DateProvider {
            override val dateStr: String = DATE
        }
    )

    @Test
    fun get_logged_configuration_when_one_server_user() {
        assertThat(helper.getLoggedUserConfiguration(singleServerSingleUserConfig, username1, url1))
            .isSameInstanceAs(userConfig11)
    }

    @Test
    fun get_logged_configuration_when_one_server_and_2_users() {
        assertThat(helper.getLoggedUserConfiguration(singleServer2UserConfig, username1, url1))
            .isSameInstanceAs(userConfig11)
        assertThat(helper.getLoggedUserConfiguration(singleServer2UserConfig, username2, url1))
            .isSameInstanceAs(userConfig12)
    }

    @Test
    fun get_logged_configuration_when_2_servers_with_one_user_on_each_1() {
        assertThat(helper.getLoggedUserConfiguration(twoServerOneUserEach, username1, url1))
            .isSameInstanceAs(userConfig11)
    }

    @Test(expected = RuntimeException::class)
    fun get_logged_configuration_when_2_servers_with_one_user_on_each_2() {
        helper.getLoggedUserConfiguration(twoServerOneUserEach, username2, url1)
    }

    @Test
    fun get_logged_configuration_when_2_servers_with_two_users_in_first() {
        assertThat(helper.getLoggedUserConfiguration(twoServerTwoUsersFirst, username1, url1))
            .isSameInstanceAs(userConfig11)
        assertThat(helper.getLoggedUserConfiguration(twoServerTwoUsersFirst, username2, url1))
            .isSameInstanceAs(userConfig12)
    }

    @Test
    fun add_new_configuration_to_empty() {
        val config = helper.setConfiguration(null, url1, username1, false)
        assertThat(config).isEqualTo(singleServerSingleUserConfig)
    }

    @Test
    fun add_new_configuration_to_single_server_single_user_in_same_server() {
        val config = helper.setConfiguration(singleServerSingleUserConfig, url1, username2, false)
        assertThat(config).isEqualTo(singleServer2UserConfig)
    }

    @Test
    fun add_new_configuration_to_single_server_single_user_in_other_server() {
        val config = helper.setConfiguration(singleServerSingleUserConfig, url2, username2, false)
        assertThat(helper.getLoggedUserConfiguration(config, username2, url2)).isEqualTo(userConfig22)
    }

    @Test
    fun get_oldest_configuration() {
        val configuration = DatabasesConfiguration.builder()
            .users(
                listOf(
                    buildUserConfiguration("user1", "2021-06-01T00:01:04.000"),
                    buildUserConfiguration("user2", "2021-09-02T00:01:04.000"),
                    buildUserConfiguration("user3", "2020-08-05T00:01:04.000"),
                    buildUserConfiguration("user4", "2020-08-09T00:01:04.000")
                )
            )
            .build()

        assertThat(helper.getOldestServerUser(configuration)!!.username()).isEqualTo("user3")
    }

    @Test
    fun get_null_oldest_configuration_if_empty() {
        val configuration = DatabasesConfiguration.builder()
            .users(emptyList())
            .build()
        assertThat(helper.getOldestServerUser(configuration)).isNull()
    }

    private fun buildUserConfiguration(username: String, creationDate: String): DatabaseUserConfiguration {
        return DatabaseUserConfiguration.builder()
            .username(username)
            .serverUrl("server")
            .databaseName("database$username")
            .encrypted(false)
            .databaseCreationDate(creationDate)
            .build()
    }

    companion object {
        private const val DATE = "2014-06-06T20:44:21.375"
    }
}
