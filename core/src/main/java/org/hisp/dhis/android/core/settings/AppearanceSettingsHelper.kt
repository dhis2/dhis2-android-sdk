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

package org.hisp.dhis.android.core.settings

import org.hisp.dhis.android.core.common.ObjectWithUidInterface

internal object AppearanceSettingsHelper {

    @JvmStatic
    fun <O : ObjectWithUidInterface> getGlobal(list: List<O>): O? {
        return list.find { it.uid() == null }
    }

    @JvmStatic
    fun <O : ObjectWithUidInterface> getSpecifics(list: List<O>): Map<String, O> {
        return list
            .filter { it.uid() != null }
            .associateBy { it.uid()!! }
    }

    // Compatibility methods

    @JvmStatic
    fun completionSpinnerToProgram(completionSpinnerSetting: CompletionSpinnerSetting?): ProgramConfigurationSettings {
        val globalSettings = completionSpinnerSetting?.globalSettings()?.let {
            toProgramConfiguration(it)
        }

        val specificSettings = completionSpinnerSetting?.specificSettings()?.map { (key, value) ->
            key to toProgramConfiguration(value)
        }?.toMap()

        return ProgramConfigurationSettings.builder()
            .globalSettings(globalSettings)
            .specificSettings(specificSettings)
            .build()
    }

    @JvmStatic
    fun programToCompletionSpinner(programConfiguration: ProgramConfigurationSettings?): CompletionSpinnerSetting {
        val globalSettings = programConfiguration?.globalSettings()?.let {
            toCompletionSpinner(it)
        }

        val specificSettings = programConfiguration?.specificSettings()?.map { (key, value) ->
            key to toCompletionSpinner(value)
        }?.toMap()

        return CompletionSpinnerSetting.builder()
            .globalSettings(globalSettings)
            .specificSettings(specificSettings)
            .build()
    }

    @JvmStatic
    fun toProgramConfiguration(completionSpinner: CompletionSpinner?): ProgramConfigurationSetting? {
        return completionSpinner?.let {
            ProgramConfigurationSetting.builder()
                .uid(it.uid())
                .completionSpinner(it.visible())
                .build()
        }
    }

    @JvmStatic
    fun toCompletionSpinner(programConfiguration: ProgramConfigurationSetting?): CompletionSpinner? {
        return programConfiguration?.let {
            CompletionSpinner.builder()
                .uid(it.uid())
                .visible(it.completionSpinner())
                .build()
        }
    }
}
