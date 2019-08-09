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
 * DISCLAIMED. IN NO FileResource SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.fileresource;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;

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
        implements ReadWriteWithUploadWithUidCollectionRepository<FileResource, File> {

    private final FileResourcePostCall postCall;
    private final FileResourceStore store;
    private final Context context;

    @Inject
    FileResourceCollectionRepository(final FileResourceStore store,
                                     final Map<String, ChildrenAppender<FileResource>> childrenAppenders,
                                     final RepositoryScope scope,
                                     final FileResourcePostCall postCall,
                                     final Transformer<File, FileResource> transformer,
                                     final DataStatePropagator dataStatePropagator,
                                     final Context context) {
        super(store, childrenAppenders, scope, transformer,
                new FilterConnectorFactory<>(scope, s -> new FileResourceCollectionRepository(
                        store, childrenAppenders, s, postCall, transformer, dataStatePropagator, context)));
        this.store = store;
        this.postCall = postCall;
        this.context = context;
    }

    @Override
    public Observable<D2Progress> upload() {
        return Observable.fromCallable(() -> byState().in(State.TO_POST, State.TO_UPDATE)
                .getWithoutChildren())
                .flatMap(postCall::uploadFileResources);
    }

    @Override
    public Single<String> add(File file) {
        return Single.fromCallable(() -> addFile(file));
    }

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    private String addFile(File file) throws D2Error {
        try {
            String generatedUid = new CodeGeneratorImpl().generate();
            File dstFile = FileResourceUtil.saveFile(file, generatedUid, context);
            FileResource fileResource = transformer.transform(dstFile).toBuilder().uid(generatedUid).build();
            store.insert(fileResource);
            return fileResource.uid();
        } catch (SQLiteConstraintException e) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.OBJECT_CANT_BE_INSERTED)
                    .errorDescription("File resource can't be inserted")
                    .originalException(e)
                    .build();
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

    public StringFilterConnector<FileResourceCollectionRepository> byLastUpdated() {
        return cf.string(Columns.LAST_UPDATED);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byContentType() {
        return cf.string(Columns.CONTENT_TYPE);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byContentLength() {
        return cf.string(Columns.CONTENT_LENGTH);
    }

    public StringFilterConnector<FileResourceCollectionRepository> byPath() {
        return cf.string(Columns.PATH);
    }

    public EnumFilterConnector<FileResourceCollectionRepository, State> byState() {
        return cf.enumC(Columns.STATE);
    }
}