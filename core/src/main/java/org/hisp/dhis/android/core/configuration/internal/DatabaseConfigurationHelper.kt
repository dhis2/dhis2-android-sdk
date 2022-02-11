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

import dagger.Reusable
import javax.inject.Inject

@Reusable
internal class DatabaseConfigurationHelper @Inject constructor(
    private val databaseNameGenerator: DatabaseNameGenerator,
    private val dateProvider: DateProvider
) {

    fun changeEncryption(serverUrl: String?, userConfiguration: DatabaseUserConfiguration): DatabaseUserConfiguration {
        return userConfiguration.toBuilder()
            .encrypted(!userConfiguration.encrypted())
            .databaseName(
                databaseNameGenerator.getDatabaseName(
                    serverUrl!!, userConfiguration.username(),
                    !userConfiguration.encrypted()
                )
            )
            .build()
    }

    fun setConfiguration(
        configuration: DatabasesConfiguration?,
        serverUrl: String,
        username: String,
        encrypt: Boolean
    ): DatabasesConfiguration {
        val newUserConf = DatabaseUserConfiguration.builder()
            .username(username)
            .serverUrl(serverUrl)
            .databaseName(databaseNameGenerator.getDatabaseName(serverUrl, username, encrypt))
            .encrypted(encrypt)
            .databaseCreationDate(dateProvider.dateStr)
            .build()

        val otherUsers = configuration?.users()?.filterNot {
            equalsIgnoreProtocol(it.serverUrl(), serverUrl) && it.username() == username
        } ?: emptyList()

        return DatabasesConfiguration.builder()
            .users(otherUsers + newUserConf)
            .build()
    }

    companion object {
        fun getUserConfiguration(
            configuration: DatabasesConfiguration?,
            serverUrl: String,
            username: String
        ): DatabaseUserConfiguration? {
            return configuration?.users()?.find {
                equalsIgnoreProtocol(it.serverUrl(), serverUrl) && it.username() == username
            }
        }

        fun removeUserConfigurations(
            configuration: DatabasesConfiguration,
            userToRemove: List<DatabaseUserConfiguration>
        ): DatabasesConfiguration {
            val users = configuration.users().filterNot { user ->
                userToRemove.any { it.databaseName() == user.databaseName() }
            }

            return configuration.toBuilder().users(users).build()
        }

        @Suppress("TooGenericExceptionThrown")
        fun getLoggedUserConfiguration(
            configuration: DatabasesConfiguration,
            username: String,
            serverUrl: String
        ): DatabaseUserConfiguration {
            val userConfiguration = getUserConfiguration(configuration, serverUrl, username)
            return userConfiguration
                ?: throw RuntimeException("Malformed configuration: user configuration not found for logged server")
        }

        fun getOldestAccounts(configuration: DatabasesConfiguration, keepAccounts: Int): List<DatabaseUserConfiguration> {
            val listSize = configuration.users().size
            return if (listSize > keepAccounts) {
                configuration.users()
                    .sortedByDescending { it.databaseCreationDate() }
                    .subList(keepAccounts, listSize)
            } else {
                emptyList()
            }
        }

        private fun equalsIgnoreProtocol(s1: String, s2: String): Boolean {
            return removeProtocol(s1) == removeProtocol(s2)
        }

        private fun removeProtocol(s: String): String {
            return s.replace("https://", "").replace("http://", "")
        }
    }
}
