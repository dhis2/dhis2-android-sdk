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

package org.hisp.dhis.android.core.dataset.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CallFetcher;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

abstract class DataSetCompleteRegistrationCallFetcher implements CallFetcher<DataSetCompleteRegistration> {

    private final static int MAX_ALLOWED_QUERY_LENGTH = 2500;
    private final static int SERVER_URL_LENGTH = 200;

    /*
    completeDataSetRegistrations?fields=period,dataSet,organisationUnit,
    attributeOptionCombo,date,storedBy&dataSet=&period=&orgUnit&children=
    true&paging=false
     */
    private final static int QUERY_WITHOUT_UIDS_LENGTH = 154;
    private final static int SERVER_URL_AND_QUERY_WITHOUT_UIDS_LENGTH
            = SERVER_URL_LENGTH + QUERY_WITHOUT_UIDS_LENGTH;
    private final static int QUERY_LENTGH_AVAILABLE_FOR_UIDS
            = MAX_ALLOWED_QUERY_LENGTH - SERVER_URL_AND_QUERY_WITHOUT_UIDS_LENGTH;

    private final static int UID_WITH_COMMA_LENGTH = 12;

    private final int queryLengthAvailableAfterIncludingPeriodIds;

    private final Collection<String> totalDataSetUids;
    private final Collection<String> totalPeriodIds;
    private final Collection<String> totalRootOrganisationUnitsUids;
    private final String lastUpdatedStr;

    private List<Set<String>> splitDataSetUids;
    private List<Set<String>> splitRootOrganisationUnitsUids;

    private final APICallExecutor apiCallExecutor;


    DataSetCompleteRegistrationCallFetcher(@NonNull Collection<String> dataSetUids,
                                           @NonNull Collection<String> periodIds,
                                           @NonNull Collection<String> rootOrganisationUnitsUids,
                                           @Nullable String lastUpdatedStr,
                                           @NonNull APICallExecutor apiCallExecutor) {
        this.totalDataSetUids = dataSetUids;
        this.totalPeriodIds = periodIds;
        this.totalRootOrganisationUnitsUids = rootOrganisationUnitsUids;
        this.lastUpdatedStr = lastUpdatedStr;
        this.apiCallExecutor = apiCallExecutor;

        this.queryLengthAvailableAfterIncludingPeriodIds =
                QUERY_LENTGH_AVAILABLE_FOR_UIDS - getTotalPeriodIdsLengthWithCommas();
    }

    protected abstract retrofit2.Call<DataSetCompleteRegistrationPayload> getCall(
            DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery);

    @Override
    public List<DataSetCompleteRegistration> fetch() throws D2Error {

        checkTooManyPeriodIds();

        if (!checkRequiredUidsArePresent()) {
            return Collections.emptyList();
        }

        splitUidCollections();

        return downloadAllDataSetCompleteRegistrations();
    }

    private void checkTooManyPeriodIds() throws D2Error {

        if (queryLengthAvailableAfterIncludingPeriodIds < 2 * UID_WITH_COMMA_LENGTH) {
            throw D2Error.builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.TOO_MANY_PERIODS)
                    .errorDescription("Too many period ids attached: "
                            + totalPeriodIds.size()
                    + ". Please, consider decreasing the ammout of " +
                            "periods you are requesting data for.")
                    .build();
        }
    }

    private boolean checkRequiredUidsArePresent() {
        return !totalDataSetUids.isEmpty() &&
                !totalPeriodIds.isEmpty() &&
                !totalRootOrganisationUnitsUids.isEmpty();
    }

    private void splitUidCollections() {

        int organisationUnitUidsPerSplit =
                getHowMuchUidsFitInStringWithLength(queryLengthAvailableAfterIncludingPeriodIds) - 1;

        organisationUnitUidsPerSplit = Math.min(organisationUnitUidsPerSplit, totalRootOrganisationUnitsUids.size());

        int dataSetUidsPerSplit = getHowMuchUidsFitInStringWithLength(
                queryLengthAvailableAfterIncludingPeriodIds - getUidsLength(organisationUnitUidsPerSplit));

        this.splitRootOrganisationUnitsUids = splitUids(totalRootOrganisationUnitsUids, organisationUnitUidsPerSplit);
        this.splitDataSetUids = splitUids(totalDataSetUids, dataSetUidsPerSplit);
    }

    private List<Set<String>> splitUids(Collection<String> allUids, int maxUidsPerSplit) {
        return CollectionsHelper.setPartition(allUids, maxUidsPerSplit);
    }

    @NonNull
    private List<DataSetCompleteRegistration> downloadAllDataSetCompleteRegistrations() throws D2Error {

        List<DataSetCompleteRegistration> dataSetCompleteRegistrations = new ArrayList<>();

        for (Set<String> organisationUnitUids : splitRootOrganisationUnitsUids) {

            for (Set<String> dataSetUids : splitDataSetUids) {

                List<DataSetCompleteRegistration> fetchedDataSetCompleteRegistrations =
                        downloadDataSetCompleteRegistrationsFor(dataSetUids, totalPeriodIds, organisationUnitUids);

                dataSetCompleteRegistrations.addAll(fetchedDataSetCompleteRegistrations);
            }
        }

        return dataSetCompleteRegistrations;
    }

    private List<DataSetCompleteRegistration> downloadDataSetCompleteRegistrationsFor(
            Collection<String> dataSetUids,
            Collection<String> periodUids,
            Collection<String> organisationUnitUids) throws D2Error {

        DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery =
                DataSetCompleteRegistrationQuery.create(dataSetUids, periodUids, organisationUnitUids, lastUpdatedStr);

        DataSetCompleteRegistrationPayload dataSetCompleteRegistrationPayload =
                apiCallExecutor.executeObjectCall(getCall(dataSetCompleteRegistrationQuery));

        return dataSetCompleteRegistrationPayload.dataSetCompleteRegistrations;
    }

    private int getTotalPeriodIdsLengthWithCommas() {

        int commaLength = 1;
        int totalPeriodIdsWithCommaLength = 0;

        for (String periodId : totalPeriodIds) {
            totalPeriodIdsWithCommaLength += periodId.length() + commaLength;
        }

        return totalPeriodIdsWithCommaLength;
    }

    private int getUidsLength(int uidsCount) {
        return UID_WITH_COMMA_LENGTH * uidsCount;
    }

    private int getHowMuchUidsFitInStringWithLength(int stringLength) {
        return stringLength / UID_WITH_COMMA_LENGTH;
    }
}

