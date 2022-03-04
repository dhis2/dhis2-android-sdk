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

package org.hisp.dhis.android.core.configuration.internal.migration

import android.content.Context
import java.io.File
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStoreImpl
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSConfigKey
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSConfigStoreImpl

internal class Migration260(
    private val context: Context,
    private val databaseConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val databaseAdapterFactory: DatabaseAdapterFactory
) {
    fun apply() {
        val configuration = databaseConfigurationStore.get()

        configuration?.let {
            if (configuration.accounts().size == 1) {
                val existingAccount = configuration.accounts().first()
                val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
                databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, existingAccount)

                migrateFileResources260(databaseAdapter)
                migrateSmsSettings(databaseAdapter)

                databaseAdapter.close()
            }
        }
    }

    private fun migrateFileResources260(databaseAdapter: DatabaseAdapter) {
        val accountSubFolder = FileResourceDirectoryHelper.getSubfolderName(databaseAdapter.databaseName)

        val rootResources = FileResourceDirectoryHelper.getRootFileResourceDirectory(context)
        val dstResources = FileResourceDirectoryHelper.getFileResourceDirectory(context, accountSubFolder)

        rootResources.listFiles()
            ?.filter { it.isFile }
            ?.forEach { file -> file.renameTo(File(dstResources, file.name)) }

        val fileResourcesStore = FileResourceStoreImpl.create(databaseAdapter)
        val fileResources = fileResourcesStore.selectAll()
        fileResources.forEach {
            val newPath = it.path()?.replace(
                oldValue = FileResourceDirectoryHelper.FilesDir,
                newValue = FileResourceDirectoryHelper.FilesDir + "/" + accountSubFolder
            ) ?: ""
            val newFileResource = it.toBuilder().path(newPath).build()
            fileResourcesStore.updateOrInsert(newFileResource)
        }
    }

    @Suppress("MagicNumber")
    private fun migrateSmsSettings(databaseAdapter: DatabaseAdapter) {
        val configFile = "smsconfig"

        val keyModuleEnabled = "module_enabled"
        val keyGateway = "gateway"
        val keyConfirmationSender = "confirmationsender"
        val keyWaitingResultTimeout = "reading_timeout"
        val keyMetadataConfig = "metadata_conf"
        val keyWaitForResult = "wait_for_result"

        val smsPrefs = context.getSharedPreferences(configFile, Context.MODE_PRIVATE)
        val smsConfigStore = SMSConfigStoreImpl.create(databaseAdapter)

        smsPrefs.getBoolean(keyModuleEnabled, false).let {
            smsConfigStore.set(SMSConfigKey.MODULE_ENABLED, it.toString())
        }
        smsPrefs.getString(keyGateway, null)?.let {
            smsConfigStore.set(SMSConfigKey.GATEWAY, it)
        }
        smsPrefs.getString(keyConfirmationSender, null)?.let {
            smsConfigStore.set(SMSConfigKey.CONFIRMATION_SENDER, it)
        }
        smsPrefs.getInt(keyWaitingResultTimeout, 120).let {
            smsConfigStore.set(SMSConfigKey.WAITING_RESULT_TIMEOUT, it.toString())
        }
        smsPrefs.getString(keyMetadataConfig, null)?.let {
            smsConfigStore.set(SMSConfigKey.METADATA_CONFIG, it)
        }
        smsPrefs.getBoolean(keyWaitForResult, false).let {
            smsConfigStore.set(SMSConfigKey.WAIT_FOR_RESULT, it.toString())
        }

        smsPrefs.edit().clear().apply()
    }
}
