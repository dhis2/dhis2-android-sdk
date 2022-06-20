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
package org.hisp.dhis.android.core.trackedentity.internal;

import static org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams.QueryParams;

import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.UnwrappedEqInFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.settings.EnrollmentScope;
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class TrackedEntityInstanceDownloader extends BaseRepositoryImpl<TrackedEntityInstanceDownloader> {

    private final RepositoryScope scope;

    private final TrackedEntityInstanceDownloadCall downloadCall;

    @Inject
    TrackedEntityInstanceDownloader(final RepositoryScope scope,
                                    final TrackedEntityInstanceDownloadCall downloadCall) {
        super(scope, new FilterConnectorFactory<>(scope, s -> new TrackedEntityInstanceDownloader(s, downloadCall)));
        this.scope = scope;
        this.downloadCall = downloadCall;
    }

    /**
     * Downloads and persists TrackedEntityInstances from the server. Only instances in capture scope are downloaded.
     * This method keeps track of the latest successful download in order to void downloading unmodified data.
     *
     * It makes use of paging with a best effort strategy: in case a page fails to be downloaded or persisted, it is
     * skipped and the rest of pages are persisted.
     *
     * @return An Observable that notifies about the progress.
     */
    public Observable<TrackerD2Progress> download() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.fromRepositoryScope(scope);
        return this.downloadCall.download(params);
    }

    public void blockingDownload() {
        download().blockingSubscribe();
    }

    public UnwrappedEqInFilterConnector<TrackedEntityInstanceDownloader> byUid() {
        return cf.unwrappedEqIn(QueryParams.UID);
    }

    public TrackedEntityInstanceDownloader byProgramUid(String programUid) {
        return cf.baseString(QueryParams.PROGRAM).eq(programUid);
    }

    /*public TrackedEntityInstanceDownloader byOrgunitUids(String... uids) {
        return cf.string(QueryParams.ORG_UNITS).in(uids);
    }*/

    public TrackedEntityInstanceDownloader limitByOrgunit(Boolean limitByOrgunit) {
        return cf.bool(QueryParams.LIMIT_BY_ORGUNIT).eq(limitByOrgunit);
    }

    public TrackedEntityInstanceDownloader limitByProgram(Boolean limitByProgram) {
        return cf.bool(QueryParams.LIMIT_BY_PROGRAM).eq(limitByProgram);
    }

    public TrackedEntityInstanceDownloader limit(Integer limit) {
        return cf.integer(QueryParams.LIMIT).eq(limit);
    }

    public TrackedEntityInstanceDownloader byProgramStatus(EnrollmentScope status) {
        return cf.baseString(QueryParams.PROGRAM_STATUS).eq(status.toString());
    }

    /**
     * If true, it overwrites existing TEIs in the device with the TEIs returned from the server. It does not modify
     * TEIs that are not returned from the server.
     *
     * @param overwrite True to overwrite
     * @return the new repository
     */
    public TrackedEntityInstanceDownloader overwrite(Boolean overwrite) {
        return cf.bool(QueryParams.OVERWRITE).eq(overwrite);
    }

}
