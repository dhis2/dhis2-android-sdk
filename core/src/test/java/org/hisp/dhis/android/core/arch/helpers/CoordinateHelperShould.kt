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
package org.hisp.dhis.android.core.arch.helpers

import com.google.common.collect.Lists
import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper.getCoordinatesFromGeometry
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper.getGeometryFromCoordinates
import org.hisp.dhis.android.core.common.Coordinates
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.junit.Test

class CoordinateHelperShould {
    @Test
    fun get_coordinates_from_geometry() {
        val geometry = Geometry.builder()
            .type(FeatureType.POINT)
            .coordinates(Lists.newArrayList(longitude, latitude).toString())
            .build()

        val coordinates = getCoordinatesFromGeometry(geometry)

        Truth.assertThat(coordinates!!.longitude()).isEqualTo(longitude)
        Truth.assertThat(coordinates.latitude()).isEqualTo(latitude)
    }

    @Test
    fun get_geometry_from_coordinates() {
        val coordinates = Coordinates.create(latitude, longitude)

        val geometry = getGeometryFromCoordinates(coordinates)

        Truth.assertThat(geometry!!.type()).isEqualTo(FeatureType.POINT)
        Truth.assertThat(geometry.coordinates()).isEqualTo("[43.34532, -23.98234]")
    }

    companion object {
        private const val longitude = 43.34532
        private const val latitude = -23.98234
    }
}
