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
package org.hisp.dhis.android.core.dataset.internal

import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCallFactory
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloader
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.period.internal.PeriodHandler
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.internal.ValidationRuleUidsCall

@Reusable
internal class DataSetModuleDownloader @Inject internal constructor(
    private val dataSetCallFactory: UidsCallFactory<DataSet>,
    private val dataElementCallFactory: UidsCallFactory<DataElement>,
    private val optionSetCall: UidsCall<OptionSet>,
    private val optionCall: UidsCall<Option>,
    private val validationRuleCall: UidsCall<ValidationRule>,
    private val validationRuleUidsCall: ValidationRuleUidsCall,
    private val periodHandler: PeriodHandler,
    private val dataSetOrganisationUnitLinkStore: LinkStore<DataSetOrganisationUnitLink>
) : UntypedModuleDownloader {

    override fun downloadMetadata(): Completable {
        return Completable.fromCallable {
            val orgUnitDataSetUids = dataSetOrganisationUnitLinkStore
                .selectDistinctSlaves(DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET)
            val dataSets = dataSetCallFactory.create(orgUnitDataSetUids).call()

            val dataElements = dataElementCallFactory.create(
                DataSetParentUidsHelper.getDataElementUids(dataSets)
            ).call()

            val optionSetUids = DataSetParentUidsHelper.getAssignedOptionSetUids(dataElements)
            optionSetCall.download(optionSetUids).blockingGet()
            optionCall.download(optionSetUids).blockingGet()

            val validationRuleUids = validationRuleUidsCall.download(getUids(dataSets)).blockingGet()
            validationRuleCall.download(getUids(validationRuleUids)).blockingGet()
            periodHandler.generateAndPersist()
        }
    }
}
