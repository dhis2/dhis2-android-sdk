/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.settings.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.settings.DataSetSettings
import javax.inject.Inject

@Reusable
internal class DataSetSettingCall @Inject constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val dataSetSettingHandler: Handler<DataSetSetting>,
    private val androidSettingService: SettingService,
    private val apiCallExecutor: RxAPICallExecutor
) : CompletableProvider {

    override fun getCompletable(storeError: Boolean): Completable {
        return Completable
            .fromSingle(downloadAndPersist(storeError))
            .onErrorComplete()
    }

    private fun downloadAndPersist(storeError: Boolean): Single<DataSetSettings> {
        return apiCallExecutor.wrapSingle(androidSettingService.dataSetSettings, storeError)
            .map { dataSetSettings: DataSetSettings ->
                val transaction = databaseAdapter.beginNewTransaction()
                try {
                    val dataSetSettingList = getDataSetSettingList(dataSetSettings)
                    dataSetSettingHandler.handleMany(dataSetSettingList)
                    transaction.setSuccessful()
                } finally {
                    transaction.end()
                }
                dataSetSettings
            }
            .doOnError { throwable: Throwable? ->
                if (throwable is D2Error && throwable.httpErrorCode() == 404) {
                    dataSetSettingHandler.handleMany(emptyList())
                }
            }
    }

    private fun getDataSetSettingList(dataSetSettings: DataSetSettings): List<DataSetSetting> {
        return dataSetSettings.specificSettings().values + dataSetSettings.globalSettings()
    }
}