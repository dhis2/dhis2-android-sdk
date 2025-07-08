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
package org.hisp.dhis.android.core.dataset.internal

import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloaderCoroutines
import org.hisp.dhis.android.core.dataelement.internal.DataElementEndpointCallFactory
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.option.internal.OptionCall
import org.hisp.dhis.android.core.option.internal.OptionSetCall
import org.hisp.dhis.android.core.period.internal.PeriodHandler
import org.hisp.dhis.android.core.validation.internal.ValidationRuleCall
import org.hisp.dhis.android.core.validation.internal.ValidationRuleUidsCallCoroutines
import org.koin.core.annotation.Singleton

@Suppress("LongParameterList")
@Singleton
internal class DataSetModuleDownloader(
    private val dataSetCallFactory: DataSetEndpointCallFactory,
    private val dataElementCallFactory: DataElementEndpointCallFactory,
    private val optionSetCall: OptionSetCall,
    private val optionCall: OptionCall,
    private val validationRuleCall: ValidationRuleCall,
    private val validationRuleUidsCall: ValidationRuleUidsCallCoroutines,
    private val periodHandler: PeriodHandler,
    private val dataSetOrganisationUnitLinkStore: DataSetOrganisationUnitLinkStore,
) : UntypedModuleDownloaderCoroutines {

    override suspend fun downloadMetadata() {
        val orgUnitDataSetUids = dataSetOrganisationUnitLinkStore
            .selectDistinctSlaves(DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET)
        val dataSets = dataSetCallFactory.create(orgUnitDataSetUids)

        val dataElements = dataElementCallFactory.create(
            DataSetParentUidsHelper.getDataElementUids(dataSets),
        )

        val optionSetUids = DataSetParentUidsHelper.getAssignedOptionSetUids(dataElements)
        optionSetCall.download(optionSetUids)
        optionCall.download(optionSetUids)

        val validationRuleUids = validationRuleUidsCall.download(getUids(dataSets))
        validationRuleCall.download(getUids(validationRuleUids))

        periodHandler.generateAndPersist()
    }
}
