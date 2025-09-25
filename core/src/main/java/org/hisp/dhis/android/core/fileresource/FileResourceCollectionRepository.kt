/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.fileresource

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import kotlinx.coroutines.withContext
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.LongFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.fileresource.internal.FileResourceProjectionTransformer
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.saveFile
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.persistence.fileresource.FileResourceTableInfo
import org.koin.core.annotation.Singleton
import java.io.File

@Singleton
@Suppress("TooManyFunctions", "TooGenericExceptionCaught")
class FileResourceCollectionRepository internal constructor(
    private val fileResourceStore: FileResourceStore,
    scope: RepositoryScope,
    transformer: FileResourceProjectionTransformer,
    dataStatePropagator: DataStatePropagator,
    private val context: Context,
) : ReadWriteWithUidCollectionRepositoryImpl<FileResource, File, FileResourceCollectionRepository>(
    fileResourceStore,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        FileResourceCollectionRepository(
            fileResourceStore,
            s,
            transformer,
            dataStatePropagator,
            context,
        )
    },
),
    ReadWriteWithUidCollectionRepository<FileResource, File> {

    /**
     * @return Progress
     */
    @Deprecated(
        """FileResources are automatically uploaded when the parent object is uploaded. There is no need to
      manually upload the fileResources. Actually, it is risky to upload the fileResources independently: if the
      parent objects are not uploaded within two hours, the server will remove the orphan fileResources.
      """,
    )
    fun upload(): Observable<D2Progress> {
        return Observable.empty()
    }

    @Deprecated("Check {@link #upload()}.")
    fun blockingUpload() {
        upload().blockingSubscribe()
    }

    override fun add(o: File): Single<String> {
        return rxSingle { addInternal(o) }
    }

    @Throws(D2Error::class)
    override fun blockingAdd(o: File): String {
        return runBlocking { addInternal(o) }
    }

    override suspend fun addInternal(o: File): String = withContext(Dispatchers.IO) {
        try {
            val generatedUid = UidGeneratorImpl().generate()
            val dstFile = saveFile(o, generatedUid, context)
            val fileResource = transformer.transform(dstFile).toBuilder().uid(generatedUid).name(o.name).build()
            store.insert(fileResource)
            fileResource.uid()!!
        } catch (e: Exception) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected exception adding file")
                .originalException(e)
                .build()
        }
    }

    override fun uid(uid: String?): FileResourceObjectRepository {
        val updatedScope = withUidFilterItem(scope, uid)
        return FileResourceObjectRepository(fileResourceStore, uid, childrenAppenders, updatedScope)
    }

    fun byUid(): StringFilterConnector<FileResourceCollectionRepository> {
        return cf.string(FileResourceTableInfo.Columns.UID)
    }

    fun byName(): StringFilterConnector<FileResourceCollectionRepository> {
        return cf.string(FileResourceTableInfo.Columns.NAME)
    }

    fun byLastUpdated(): DateFilterConnector<FileResourceCollectionRepository> {
        return cf.date(FileResourceTableInfo.Columns.LAST_UPDATED)
    }

    fun byDomain(): EnumFilterConnector<FileResourceCollectionRepository, FileResourceDomain> {
        return cf.enumC(FileResourceTableInfo.Columns.DOMAIN)
    }

    fun byContentType(): StringFilterConnector<FileResourceCollectionRepository> {
        return cf.string(FileResourceTableInfo.Columns.CONTENT_TYPE)
    }

    fun byContentLength(): LongFilterConnector<FileResourceCollectionRepository> {
        return cf.longC(FileResourceTableInfo.Columns.CONTENT_LENGTH)
    }

    fun byPath(): StringFilterConnector<FileResourceCollectionRepository> {
        return cf.string(FileResourceTableInfo.Columns.PATH)
    }

    /**
     * @return
     */
    @Deprecated("Use {@link #bySyncState()} instead.", ReplaceWith("bySyncState()"))
    fun byState(): EnumFilterConnector<FileResourceCollectionRepository, State> {
        return bySyncState()
    }

    fun bySyncState(): EnumFilterConnector<FileResourceCollectionRepository, State> {
        return cf.enumC(FileResourceTableInfo.Columns.SYNC_STATE)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<FileResource> = emptyMap()
    }
}
