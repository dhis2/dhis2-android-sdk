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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import java.util.ArrayList
import javax.inject.Inject
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository

@Reusable
internal class TrackedEntityInstanceQueryBuilderFactory @Inject constructor(
    private val programStore: ProgramStoreInterface,
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager,
    private val globalHelper: TrackedEntityInstanceQueryGlobalHelper,
    private val perProgramHelper: TrackedEntityInstanceQueryPerProgramHelper
) {

    @Suppress("NestedBlockDepth")
    fun getTeiQueryBuilders(params: ProgramDataDownloadParams): List<TeiQuery.Builder> {
        val programSettings = programSettingsObjectRepository.blockingGet()
        lastUpdatedManager.prepare(programSettings, params)
        val builders: MutableList<TeiQuery.Builder> = ArrayList()
        if (params.program() == null) {
            val trackerPrograms = programStore.getUidsByProgramType(ProgramType.WITH_REGISTRATION)
            if (hasLimitByProgram(params, programSettings)) {
                for (programUid in trackerPrograms) {
                    builders.addAll(perProgramHelper.queryPerProgram(params, programSettings, programUid))
                }
            } else {
                val specificSettings = if (programSettings == null) emptyMap() else programSettings.specificSettings()
                for ((programUid) in specificSettings) {
                    if (trackerPrograms.contains(programUid)) {
                        builders.addAll(perProgramHelper.queryPerProgram(params, programSettings, programUid))
                    }
                }
                builders.addAll(globalHelper.queryGlobal(params, programSettings))
            }
        } else {
            builders.addAll(perProgramHelper.queryPerProgram(params, programSettings, params.program()))
        }
        return builders
    }

    @Suppress("ReturnCount")
    private fun hasLimitByProgram(params: ProgramDataDownloadParams, programSettings: ProgramSettings?): Boolean {
        if (params.limitByProgram() != null) {
            return params.limitByProgram()!!
        }
        if (programSettings?.globalSettings() != null) {
            val scope = programSettings.globalSettings()!!.settingDownload()
            if (scope != null) {
                return scope == LimitScope.PER_OU_AND_PROGRAM || scope == LimitScope.PER_PROGRAM
            }
        }
        return false
    }
}