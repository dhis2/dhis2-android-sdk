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

package org.hisp.dhis.android.core.settings

internal object AppearanceSettingsHelper {

    fun <O> getGlobal(list: List<O>, getId: (O) -> String?): O? {
        return list.find { getId(it) == null }
    }

    fun <O> getSpecifics(list: List<O>, getId: (O) -> String?): Map<String, O> {
        return list
            .filter { getId(it) != null }
            .associateBy { getId(it)!! }
    }

    // Compatibility methods

    @JvmStatic
    fun completionSpinnerToProgram(completionSpinnerSetting: CompletionSpinnerSetting?): ProgramConfigurationSettings {
        val globalSettings = completionSpinnerSetting?.globalSettings()?.let {
            toProgramConfiguration(it)
        }

        val specificSettings = completionSpinnerSetting?.specificSettings()?.mapNotNull { (key, value) ->
            toProgramConfiguration(value)?.let { key to it }
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

        val specificSettings = programConfiguration?.specificSettings()?.mapNotNull { (key, value) ->
            toCompletionSpinner(value)?.let { key to it }
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
            it.completionSpinner()?.let { completionSpinner ->
                CompletionSpinner.builder()
                    .uid(it.uid())
                    .visible(completionSpinner)
                    .build()
            }
        }
    }
}
