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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository

@Suppress("UnnecessaryAbstractClass")
internal abstract class TrackerQueryFactory<T, S : TrackerBaseSync> constructor(
    private val programStore: ProgramStoreInterface,
    private val programSettingsObjectRepository: ProgramSettingsObjectRepository,
    private val lastUpdatedManager: TrackerSyncLastUpdatedManager<S>,
    private val commonHelper: TrackerQueryFactoryCommonHelper,
    private val programType: ProgramType,
    private val internalFactoryCreator: (
        params: ProgramDataDownloadParams,
        programSettings: ProgramSettings?
    ) -> TrackerQueryInternalFactory<T>
) {

    @Suppress("NestedBlockDepth")
    fun getQueries(params: ProgramDataDownloadParams): List<T> {
        val programSettings = programSettingsObjectRepository.blockingGet()
        val internalFactory = internalFactoryCreator.invoke(params, programSettings)
        lastUpdatedManager.prepare(programSettings, params)
        return if (params.program() == null) {
            val programs = programStore.getUidsByProgramType(programType)
            when {
                params.uids().isNotEmpty() ->
                    internalFactory.queryGlobal(programs)
                commonHelper.hasLimitByProgram(params, programSettings) ->
                    programs.flatMap { internalFactory.queryPerProgram(it) }
                else -> {
                    val specificSettings = programSettings?.specificSettings() ?: emptyMap()
                    val globalPrograms = programs.toList() - specificSettings.keys
                    specificSettings
                        .filterKeys { programs.contains(it) }
                        .flatMap { internalFactory.queryPerProgram(it.key) } +
                        internalFactory.queryGlobal(globalPrograms)
                }
            }
        } else {
            internalFactory.queryPerProgram(params.program())
        }
    }
}
