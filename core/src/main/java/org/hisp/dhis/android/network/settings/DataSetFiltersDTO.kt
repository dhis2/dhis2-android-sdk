/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.settings

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.settings.DataSetFilter
import org.hisp.dhis.android.core.settings.DataSetFilters
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.network.settings.FilterSettingDTO.Companion.FILTERSETTING_GLOBAL_ID

@Serializable
internal data class DataSetFiltersDTO(
    val globalSettings: Map<String, FilterSettingDTO> = emptyMap(),
    val specificSettings: Map<String, Map<String, FilterSettingDTO>> = emptyMap(),
) {
    fun toDomain(): DataSetFilters {
        return DataSetFilters.builder()
            .globalSettings(globalSettings.toDomainMap())
            .specificSettings(specificSettings.map { (uidKey, value) -> uidKey to value.toDomainMap(uidKey) }.toMap())
            .build()
    }

    private fun Map<String, FilterSettingDTO>.toDomainMap(uidKey: String = FILTERSETTING_GLOBAL_ID):
        Map<DataSetFilter, FilterSetting> {
        return map { (key, value) -> DataSetFilter.Companion.from(key) to value.toDomain(uidKey) }.toMap()
    }
}
