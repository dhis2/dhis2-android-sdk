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
package org.hisp.dhis.android.core.arch.helpers

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.common.Coordinates
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import java.io.IOException

object CoordinateHelper {
    /**
     * Converts a [Geometry] object of type point in a [Coordinates] object.
     *
     * @param geometry Geometry of type point with non null coordinates.
     * @return The converted [Coordinates] object.
     */
    @JvmStatic
    fun getCoordinatesFromGeometry(geometry: Geometry): Coordinates? {
        val coordinateTokens: List<Double>
        return if (geometry.type() == FeatureType.POINT && geometry.coordinates() != null) {
            try {
                coordinateTokens = geometry.coordinates()
                    ?.let { KotlinxJsonParser.instance.decodeFromString(ListSerializer(Double.serializer()), it) }
                    ?: emptyList()
                Coordinates.create(coordinateTokens[1], coordinateTokens[0])
            } catch (e: IOException) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Converts a [Coordinates] object in a [Geometry] object of type point.
     *
     * @param coordinates Coordinates to convert.
     * @return The converted [Geometry] object.
     */
    @JvmStatic
    fun getGeometryFromCoordinates(coordinates: Coordinates?): Geometry? {
        return if (coordinates?.longitude() == null || coordinates.latitude() == null) {
            null
        } else {
            val coordinatesList = listOf(coordinates.longitude(), coordinates.latitude())

            Geometry.builder()
                .type(FeatureType.POINT)
                .coordinates(coordinatesList.toString())
                .build()
        }
    }
}
