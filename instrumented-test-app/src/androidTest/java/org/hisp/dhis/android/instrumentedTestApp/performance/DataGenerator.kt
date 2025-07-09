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

package org.hisp.dhis.android.instrumentedTestApp.performance

import kotlin.math.round
import kotlin.random.Random

internal data class RandomChild(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val weight: Int,
    val coordinates: List<Double>,
) {
    companion object {
        val firstNameUid = "w75KJ2mc4zz"
        val lastNameUid = "zDhUuAYrxNC"
        val genderUid = "cejWyOfXge6"
        val birthStageUid = "A03MvHHogjR"
        val weightDeUid = "UXz7xuGCEhU"

        private val firstNameList =
            listOf("John", "Jane", "Bob", "Alice", "Tom", "Mary", "David", "Linda", "Mike", "Karen")
        private val lastNameList = listOf(
            "Smith",
            "Johnson",
            "Williams",
            "Brown",
            "Jones",
            "Garcia",
            "Miller",
            "Davis",
            "Rodriguez",
            "Martinez",
        )
        private val genderList = listOf("Male", "Female")

        fun generateChild(): RandomChild {
            return RandomChild(
                firstName = firstNameList.random(),
                lastName = lastNameList.random(),
                gender = genderList.random(),
                weight = Random.nextInt(1000, 4000),
                coordinates = listOf(randomCoordinate(-180.0, 180.0), randomCoordinate(-90.0, 90.0)),
            )
        }
    }
}

fun randomCoordinate(min: Double, max: Double): Double {
    val value = Random.nextDouble(min, max)
    return round(value * 100) / 100
}
