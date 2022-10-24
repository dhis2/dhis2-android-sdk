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

package org.hisp.dhis.android.core.fileresource;

import android.content.Context;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.LongFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static org.hisp.dhis.android.core.fileresource.FileResourceTableInfo.Columns;

@Reusable
public final class FileResourceCollectionRepository
        extends ReadWriteWithUidCollectionRepositoryImpl<FileResource, File, FileResourceCollectionRepository>
        implements ReadWriteWithUidCollectionRepository<FileResource, File> {

    private final IdentifiableDataObjectStore<FileResource> store;
    private final Context context;

    @Inject
    FileResourceCollectionRepository(final IdentifiableDataObjectStore<FileResource> store,
                                     final Map<String, ChildrenAppender<FileResource>> childrenAppenders,
                                     final RepositoryScope scope,
                                     final Transformer<File, FileResource> transformer,
                                     final DataStatePropagator dataStatePropagator,
                                     final Context context) {
        super(store, childrenAppenders, scope, transformer,
                new FilterConnectorFactory<>(scope, s -> new FileResourceCollectionRepository(
                        store, childrenAppenders, s, transformer, dataStatePropagator, context)));
        this.store = store;
        this.context = context;
    }

    /**
     * @deprecated FileResources are automatically uploaded when the parent object is uploaded. There is no need to
     * manually upload the fileResources. Actually, it is risky to upload the fileResources independently: if the
     * parent objects are not uploaded within two hours, the server will remove the orphan fileResources.
     * @return Progress
     */
    @Deprecated
    public Observable<D2Progress> upload() {
        return Observable.empty();
    }

    /**
     * @deprecated Check {@link #upload()}.
     */
    @Deprecated
    public void blockingUpload() {
        upload().blockingSubscribe();
    }

    @Override
    public Single<String> add(File file) {
        return Single.fromCallable(() -> blockingAdd(file));
    }

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    @Override
    public String blockingAdd(File file) throws D2Error {
        try {
            String generatedUid = new UidGeneratorImpl().generate();
            File dstFile = FileResourceUtil.saveFile(file, generatedUid, context);
            FileResource fileResource = transformer.transform(dstFile).toBuilder().uid(generatedUid).build();
            store.insert(fileResource);
            return fileResource.uid();
        } catch (Exception e) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.UNEXPECTED)
                    .errorDescription("Unexpected exception adding file")
                    .originalException(e)
                    .build();
        }
    }

    @Override
    public FileResourceObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new FileResourceObjectRepository(store, uid, childrenAppenders, updatedScope);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byUid() {
        return cf.string(Columns.UID);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byName() {
        return cf.string(Columns.NAME);
    }

    public DateFilterConnector<FileResourceCollectionRepository> byLastUpdated() {
        return cf.date(Columns.LAST_UPDATED);
    }

    public EnumFilterConnector<FileResourceCollectionRepository, FileResourceDomain> byDomain() {
        return cf.enumC(Columns.DOMAIN);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byContentType() {
        return cf.string(Columns.CONTENT_TYPE);
    }

    public LongFilterConnector<FileResourceCollectionRepository> byContentLength() {
        return cf.longC(Columns.CONTENT_LENGTH);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byPath() {
        return cf.string(Columns.PATH);
    }

    /**
     * @deprecated Use {@link #bySyncState()} instead.
     *
     * @return
     */
    @Deprecated
    public EnumFilterConnector<FileResourceCollectionRepository, State> byState() {
        return bySyncState();
    }

    public EnumFilterConnector<FileResourceCollectionRepository, State> bySyncState() {
        return cf.enumC(Columns.SYNC_STATE);
    }
}