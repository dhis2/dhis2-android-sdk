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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.dataapproval.DataApprovalCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetInstanceCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetInstanceSummaryCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetModule;
import org.hisp.dhis.android.core.dataset.SectionCollectionRepository;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetModuleImpl implements DataSetModule {

    private final DataSetCompleteRegistrationCollectionRepository dataSetCompleteRegistrations;
    private final DataSetCollectionRepository dataSets;
    private final SectionCollectionRepository sections;
    private final DataApprovalCollectionRepository dataApprovals;
    private final DataSetInstanceCollectionRepository dataSetInstances;
    private final DataSetInstanceSummaryCollectionRepository dataSetInstanceSummaries;

    @Inject
    DataSetModuleImpl(DataSetCompleteRegistrationCollectionRepository dataSetCompleteRegistrations,
                      DataSetCollectionRepository dataSets,
                      SectionCollectionRepository sections,
                      DataApprovalCollectionRepository dataApprovalCollectionRepository,
                      DataSetInstanceCollectionRepository dataSetInstanceCollectionRepository,
                      DataSetInstanceSummaryCollectionRepository dataSetInstanceSummaries) {
        this.dataSetCompleteRegistrations = dataSetCompleteRegistrations;
        this.dataSets = dataSets;
        this.sections = sections;
        this.dataApprovals = dataApprovalCollectionRepository;
        this.dataSetInstances = dataSetInstanceCollectionRepository;
        this.dataSetInstanceSummaries = dataSetInstanceSummaries;
    }

    @Override
    public DataSetCompleteRegistrationCollectionRepository dataSetCompleteRegistrations() {
        return dataSetCompleteRegistrations;
    }

    @Override
    public DataSetCollectionRepository dataSets() {
        return dataSets;
    }

    @Override
    public SectionCollectionRepository sections() {
        return sections;
    }

    @Override
    public DataApprovalCollectionRepository dataApprovals() {
        return dataApprovals;
    }

    @Override
    public DataSetInstanceCollectionRepository dataSetInstances() {
        return dataSetInstances;
    }

    @Override
    public DataSetInstanceSummaryCollectionRepository dataSetInstanceSummaries() {
        return dataSetInstanceSummaries;
    }
}
