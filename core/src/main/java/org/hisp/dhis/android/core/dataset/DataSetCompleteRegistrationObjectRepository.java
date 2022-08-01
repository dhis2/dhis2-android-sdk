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

package org.hisp.dhis.android.core.dataset;

import android.util.Log;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadOnlyOneObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.user.UserCredentialsObjectRepository;

import java.util.Date;
import java.util.Map;

import io.reactivex.Completable;

public final class DataSetCompleteRegistrationObjectRepository
        extends ReadOnlyOneObjectRepositoryImpl<DataSetCompleteRegistration,
        DataSetCompleteRegistrationObjectRepository>
        implements ReadWriteObjectRepository<DataSetCompleteRegistration> {

    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;
    private final UserCredentialsObjectRepository credentialsRepository;

    private final String period;
    private final String dataSet;
    private final String organisationUnit;
    private final String attributeOptionCombo;

    DataSetCompleteRegistrationObjectRepository(
            final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore,
            final UserCredentialsObjectRepository credentialsRepository,
            final Map<String, ChildrenAppender<DataSetCompleteRegistration>> childrenAppenders,
            final RepositoryScope scope,
            final String period,
            final String organisationUnit,
            final String dataSet,
            final String attributeOptionCombo
            ) {
        super(dataSetCompleteRegistrationStore, childrenAppenders, scope,
                s -> new DataSetCompleteRegistrationObjectRepository(
                        dataSetCompleteRegistrationStore, credentialsRepository, childrenAppenders, s,
                period, organisationUnit, dataSet, attributeOptionCombo));

        this.dataSetCompleteRegistrationStore = dataSetCompleteRegistrationStore;
        this.credentialsRepository = credentialsRepository;

        this.period = period;
        this.dataSet = dataSet;
        this.organisationUnit = organisationUnit;
        this.attributeOptionCombo = attributeOptionCombo;
    }

    public Completable set() {
        return Completable.fromAction(this::blockingSet);
    }

    public void blockingSet() {
        DataSetCompleteRegistration dataSetCompleteRegistration = blockingGetWithoutChildren();

        if (dataSetCompleteRegistration == null) {
            String username = credentialsRepository.blockingGet().username();
            dataSetCompleteRegistrationStore.insert(
                    DataSetCompleteRegistration.builder()
                            .period(period)
                            .dataSet(dataSet)
                            .organisationUnit(organisationUnit)
                            .attributeOptionCombo(attributeOptionCombo)
                            .date(new Date())
                            .storedBy(username)
                            .syncState(State.TO_POST)
                            .deleted(false)
                            .build());
        } else {
            DataSetCompleteRegistration newRecord = dataSetCompleteRegistration.toBuilder()
                    .deleted(false)
                    .syncState(dataSetCompleteRegistration.syncState() == State.TO_POST ?
                            State.TO_POST : State.TO_UPDATE)
                    .build();
            dataSetCompleteRegistrationStore.updateWhere(newRecord);
        }
    }

    @Override
    public Completable delete() {
        return Completable.fromAction(this::blockingDelete);
    }

    @Override
    public void blockingDelete() throws D2Error {
        DataSetCompleteRegistration dataSetCompleteRegistration = blockingGetWithoutChildren();
        if (dataSetCompleteRegistration == null) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.CANT_DELETE_NON_EXISTING_OBJECT)
                    .errorDescription("DataSetCompleteRegistration can't be deleted " +
                            "because no longer exists")
                    .build();
        } else {
            if (dataSetCompleteRegistration.syncState() == State.TO_POST) {
                dataSetCompleteRegistrationStore.deleteWhere(dataSetCompleteRegistration);
            } else {
                DataSetCompleteRegistration deletedRecord = dataSetCompleteRegistration.toBuilder()
                        .deleted(true)
                        .syncState(State.TO_UPDATE)
                        .build();
                dataSetCompleteRegistrationStore.updateWhere(deletedRecord);
            }
        }
    }

    @Override
    public Completable deleteIfExist() {
        return Completable.fromAction(this::blockingDeleteIfExist);
    }

    @Override
    public void blockingDeleteIfExist() {
        try {
            blockingDelete();
        } catch (D2Error d2Error) {
            Log.v(DataSetCompleteRegistrationObjectRepository.class.getCanonicalName(), d2Error.errorDescription());

        }
    }

}
