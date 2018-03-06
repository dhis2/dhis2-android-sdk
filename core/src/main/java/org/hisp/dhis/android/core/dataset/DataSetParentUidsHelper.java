/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.indicator.Indicator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class DataSetParentUidsHelper {

    private DataSetParentUidsHelper() {}

    static Set<String> getAssignedDataSetUids(List<DataSet> dataSetsWithAccess) {
        Set<String> dataSetUids = new HashSet<>();
        for (DataSet dataSet: dataSetsWithAccess) {
            Access access = dataSet.access();
            if (access != null && access.data().read()) {
                dataSetUids.add(dataSet.uid());
            }
        }

        return dataSetUids;
    }

    static Set<String> getDataElementUids(List<DataSet> dataSets) {
        Set<String> uids = new HashSet<>();
        for (DataSet dataSet : dataSets) {
            List<DataElementUids> dataSetElements = dataSet.dataSetElements();
            assert dataSetElements != null;
            for (DataElementUids dataSetElement : dataSetElements) {
                uids.add(dataSetElement.dataElement().uid());
            }
        }
        return uids;
    }

    static Set<String> getIndicatorUids(List<DataSet> dataSets) {
        Set<String> uids = new HashSet<>();
        for (DataSet dataSet : dataSets) {
            for (ObjectWithUid indicator : dataSet.indicators()) {
                uids.add(indicator.uid());
            }
        }
        return uids;
    }

    static Set<String> getIndicatorTypeUids(List<Indicator> indicators) {
        Set<String> uids = new HashSet<>();
        for (Indicator indicator : indicators) {
            ObjectWithUid type = indicator.indicatorType();
            if (type != null) {
                uids.add(type.uid());
            }
        }
        return uids;
    }
}
