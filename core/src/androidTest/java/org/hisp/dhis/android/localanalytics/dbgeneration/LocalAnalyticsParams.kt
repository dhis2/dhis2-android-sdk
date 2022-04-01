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
package org.hisp.dhis.android.localanalytics.dbgeneration

internal data class LocalAnalyticsMetadataParams(
    val organisationUnitChildren: Int,
    val categoryOptionCombos2: Int,
    val categoryOptionCombos3: Int,
    val dataElementsAggregated: Int,
    val dataElementsTracker: Int,
    val programStagesWithRegistration: Int,
    val programStagesWithoutRegistration: Int,
    val trackedEntityAttributes: Int
) {

    companion object LocalAnalyticsMetadataParams {
        val Default = LocalAnalyticsMetadataParams(
            organisationUnitChildren = 3,
            categoryOptionCombos2 = 3,
            categoryOptionCombos3 = 6,
            dataElementsAggregated = 10,
            dataElementsTracker = 10,
            programStagesWithRegistration = 3,
            programStagesWithoutRegistration = 1,
            trackedEntityAttributes = 10
        )
    }
}

internal data class LocalAnalyticsDataParams(
    val dataValues: Int,
    val trackedEntityInstances: Int,
    val eventsWithoutRegistration: Int,
    val eventsWithRegistrationPerEnrollmentAndPS: Int
) {
    companion object LocalAnalyticsDataParams {
        fun get(f: Int) = LocalAnalyticsDataParams(
            dataValues = 3000 * f,
            trackedEntityInstances = 500 * f,
            eventsWithoutRegistration = 500 * f,
            eventsWithRegistrationPerEnrollmentAndPS = 1
        )

        const val DefaultFactor = 1
        const val LargeFactor = 3
        const val SuperLargeFactor = 6
    }
}
