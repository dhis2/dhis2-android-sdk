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

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.rxSingle
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.ResourceContext
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.arch.helpers.internal.runBlockingOnIO
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
import org.hisp.dhis.android.core.fileresource.internal.FileResourceFiles
import org.hisp.dhis.android.core.fileresource.internal.FileResourceProjectionTransformer
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.fileresource.internal.UploadQuality
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository
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
    private val fileResourceFiles: FileResourceFiles,
    private val programSettingsRepository: Lazy<ProgramSettingsObjectRepository>,
    private val dataSetSettingsRepository: Lazy<DataSetSettingsObjectRepository>,
    databaseAdapter: DatabaseAdapter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ReadWriteWithUidCollectionRepositoryImpl<FileResource, File, FileResourceCollectionRepository>(
    fileResourceStore,
    databaseAdapter,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        FileResourceCollectionRepository(
            fileResourceStore,
            s,
            transformer,
            dataStatePropagator,
            fileResourceFiles,
            programSettingsRepository,
            dataSetSettingsRepository,
            databaseAdapter,
            dispatcher,
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
    fun blockingUpload() = Unit

    @Deprecated(USE_PATH, ReplaceWith("rxAdd(o.toOkioPath())", "okio.Path.Companion.toOkioPath"))
    override fun rxAdd(o: File): Single<String> = rxAdd(o.toOkioPath())

    @Deprecated(USE_PATH, ReplaceWith("blockingAdd(o.toOkioPath())", "okio.Path.Companion.toOkioPath"))
    override fun blockingAdd(o: File): String = blockingAdd(o.toOkioPath())

    @Deprecated(USE_PATH, ReplaceWith("suspendAdd(o.toOkioPath())", "okio.Path.Companion.toOkioPath"))
    override suspend fun suspendAdd(o: File): String = suspendAdd(o.toOkioPath())

    fun rxAdd(file: Path): Single<String> = rxSingle { suspendAdd(file) }

    @Throws(D2Error::class)
    fun blockingAdd(file: Path): String = runBlockingOnIO { suspendAdd(file) }

    @Throws(D2Error::class)
    suspend fun suspendAdd(file: Path): String = addFile(file, file.name)

    suspend fun suspendProcessAndAdd(file: Path, resourceContext: ResourceContext): String = withContext(dispatcher) {
        val processedFile = when (resourceContext) {
            is ResourceContext.ImageContext -> compressImageIfNeeded(file, getQuality(resourceContext))
            ResourceContext.FileContext -> file
        }
        try {
            addFile(processedFile, resourceName(file, processedFile))
        } finally {
            if (processedFile != file) {
                fileResourceFiles.deleteFile(processedFile)
            }
        }
    }

    fun rxProcessAndAdd(file: Path, resourceContext: ResourceContext): Single<String> =
        rxSingle { suspendProcessAndAdd(file, resourceContext) }

    fun blockingProcessAndAdd(file: Path, resourceContext: ResourceContext): String =
        runBlockingOnIO { suspendProcessAndAdd(file, resourceContext) }

    @Deprecated(
        USE_PATH,
        ReplaceWith("suspendProcessAndAdd(o.toOkioPath(), resourceContext)", "okio.Path.Companion.toOkioPath"),
    )
    suspend fun suspendProcessAndAdd(o: File, resourceContext: ResourceContext): String =
        suspendProcessAndAdd(o.toOkioPath(), resourceContext)

    @Deprecated(
        USE_PATH,
        ReplaceWith("rxProcessAndAdd(o.toOkioPath(), resourceContext)", "okio.Path.Companion.toOkioPath"),
    )
    fun rxProcessAndAdd(o: File, resourceContext: ResourceContext): Single<String> =
        rxProcessAndAdd(o.toOkioPath(), resourceContext)

    @Deprecated(
        USE_PATH,
        ReplaceWith("blockingProcessAndAdd(o.toOkioPath(), resourceContext)", "okio.Path.Companion.toOkioPath"),
    )
    fun blockingProcessAndAdd(o: File, resourceContext: ResourceContext): String =
        blockingProcessAndAdd(o.toOkioPath(), resourceContext)

    private suspend fun addFile(file: Path, name: String): String = withContext(dispatcher) {
        try {
            val generatedUid = UidGeneratorImpl().generate()
            val dstFile = fileResourceFiles.saveFile(file, generatedUid).toFile()
            val fileResource = transformer.transform(dstFile).toBuilder().uid(generatedUid).name(name).build()
            store.insert(fileResource)
            fileResource.uid()
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

    /**
     * The name is taken from the original file, so that the temporary name of the processed one is not exposed to the
     * server. The extension is taken from the processed file, because the compression may have changed the image
     * format: the content type is derived from the name, so both must agree with the actual bytes.
     */
    private fun resourceName(originalFile: Path, processedFile: Path): String {
        val originalName = originalFile.name
        val processedExtension = processedFile.name.substringAfterLast('.', "")
        return if (processedExtension.equals(originalName.substringAfterLast('.', ""), ignoreCase = true)) {
            originalName
        } else {
            "${originalName.substringBeforeLast('.')}.$processedExtension"
        }
    }

    private fun compressImageIfNeeded(file: Path, quality: UploadQuality): Path {
        return if (quality == UploadQuality.DEFAULT) fileResourceFiles.compressImage(file) else file
    }

    private suspend fun getQuality(resourceContext: ResourceContext.ImageContext): UploadQuality {
        return when (resourceContext) {
            is ResourceContext.ImageContext.ProgramImageContext ->
                programSettingsRepository.value
                    .getImageQualityFromProgramSettings(resourceContext.programUid, resourceContext.resourceUid)

            is ResourceContext.ImageContext.DatasetImageContext ->
                dataSetSettingsRepository.value
                    .getImageQualityFromDataSetSetting(resourceContext.datasetUid, resourceContext.resourceUid)
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
        private const val USE_PATH = "Use the okio.Path overload."
        val childrenAppenders: ChildrenAppenderGetter<FileResource> = emptyMap()
        const val DEFAULT_QUALITY = "default"
    }
}
