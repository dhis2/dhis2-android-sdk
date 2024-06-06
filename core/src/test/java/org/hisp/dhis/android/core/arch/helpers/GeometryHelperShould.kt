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
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.createMultiPolygonGeometry
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.createPointGeometry
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.createPolygonGeometry
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.getMultiPolygon
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.getPoint
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.getPolygon
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.isDefinedAndValid
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.isValid
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.maintenance.D2Error
import org.junit.Test
import java.util.Arrays

class GeometryHelperShould {
    @Test
    @Throws(D2Error::class)
    fun get_point_from_geometry_from_list() {
        val coordinates: List<Double> = Lists.newArrayList(longitude1, latitude1)

        val geometry = createPointGeometry(coordinates)

        val point: List<Double?> = getPoint(geometry)

        Truth.assertThat(point).isEqualTo(coordinates)
    }

    @Test
    @Throws(D2Error::class)
    fun get_point_from_geometry_from_longitude_and_latitude() {
        val geometry = createPointGeometry(longitude1, latitude1)

        val point = getPoint(geometry)

        Truth.assertThat(point[0]).isEqualTo(longitude1)
        Truth.assertThat(point[1]).isEqualTo(latitude1)
    }

    @Test
    @Throws(D2Error::class)
    fun get_polygon_from_geometry() {
        val coordinates: List<List<List<Double?>?>?> = listOf(
            Arrays.asList<List<Double?>?>(
                Lists.newArrayList(longitude1, latitude1),
                Lists.newArrayList(longitude2, latitude2)
            )
        )

        val geometry = createPolygonGeometry(coordinates)

        val polygon: List<List<List<Double>>?> = getPolygon(geometry)

        Truth.assertThat(polygon).isEqualTo(coordinates)
    }

    @Test
    @Throws(D2Error::class)
    fun get_multipolygon_from_geometry() {
        val coordinates =
            Arrays.asList(
                Arrays.asList(
                    Arrays.asList<List<Double?>?>(
                        Lists.newArrayList(longitude1, latitude1),
                        Lists.newArrayList(longitude2, latitude2)
                    ),
                    listOf<List<Double?>>(
                        Lists.newArrayList(longitude1, latitude2)
                    )
                ),
                listOf<List<List<Double?>?>>(
                    listOf<List<Double?>>(
                        Lists.newArrayList(longitude2, latitude1)
                    )
                )
            )

        val geometry = createMultiPolygonGeometry(coordinates)

        val multiPolygon: List<List<List<List<Double>>>?> = getMultiPolygon(geometry)

        Truth.assertThat(multiPolygon).isEqualTo(coordinates)
    }

    @Test
    fun should_build_coordinates_without_spaces() {
        val coordinates: List<Double> = Lists.newArrayList(longitude1, latitude1)

        val geometry = createPointGeometry(coordinates)

        val expectedCoordinates = "[" + longitude1 + "," + latitude1 + "]"
        Truth.assertThat(geometry.coordinates()).isEqualTo(expectedCoordinates)
    }

    @Test
    fun should_return_invalid_or_empty_geometry() {
        Truth.assertThat(isDefinedAndValid(null)).isFalse()

        val empty = Geometry.builder().build()
        Truth.assertThat(isDefinedAndValid(empty)).isFalse()

        val incomplete = Geometry.builder().type(FeatureType.POINT).build()
        Truth.assertThat(isDefinedAndValid(incomplete)).isFalse()

        val invalid =
            Geometry.builder().type(FeatureType.POINT).coordinates("invalid_coordinates").build()
        Truth.assertThat(isDefinedAndValid(invalid)).isFalse()

        val invalid2 = Geometry.builder().type(FeatureType.NONE).coordinates("[2.4, 4.5]").build()
        Truth.assertThat(isDefinedAndValid(invalid2)).isFalse()
    }

    @Test
    fun should_return_valid_empty_geometry() {
        val point = Geometry.builder()
            .type(FeatureType.POINT)
            .coordinates("[2.4, 4.5]").build()
        Truth.assertThat(isDefinedAndValid(point)).isTrue()

        val polygon = Geometry.builder()
            .type(FeatureType.POLYGON)
            .coordinates("[[[2.4, 4.5]],[[4.4, 2.5]]]").build()
        Truth.assertThat(isDefinedAndValid(polygon)).isTrue()

        val multiPolygon = Geometry.builder()
            .type(FeatureType.MULTI_POLYGON)
            .coordinates("[[[[2.4, 4.5]]],[[[4.4, 2.5]]]]").build()
        Truth.assertThat(isDefinedAndValid(multiPolygon)).isTrue()
    }

    @Test
    fun should_return_valid_geometry_if_empty() {
        Truth.assertThat(isValid(null)).isTrue()
    }

    companion object {
        private const val longitude1 = 43.34532
        private const val latitude1 = -23.98234
        private const val longitude2 = -10.02322
        private const val latitude2 = 3.74597
    }
}
