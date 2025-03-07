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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

@Suppress("TooManyFunctions")
object GeometryHelper {
    /**
     * Checks if the geometry contains a point.
     *
     * @param geometry The [Geometry] to check.
     * @return A boolean that indicates if the geometry contains a point.
     */
    @JvmStatic
    fun containsAPoint(geometry: Geometry?): Boolean {
        return geometry != null && geometry.type() == FeatureType.POINT
    }

    /**
     * Checks if the geometry contains a polygon.
     *
     * @param geometry The [Geometry] to check.
     * @return A boolean that indicates if the geometry contains a polygon.
     */
    fun containsAPolygon(geometry: Geometry?): Boolean {
        return geometry != null && geometry.type() == FeatureType.POLYGON
    }

    /**
     * Checks if the geometry contains a multi polygon.
     *
     * @param geometry The [Geometry] to check.
     * @return A boolean that indicates if the geometry contains a multi polygon.
     */
    fun containsAMultiPolygon(geometry: Geometry?): Boolean {
        return geometry != null && geometry.type() == FeatureType.MULTI_POLYGON
    }

    /**
     * Converts a [Geometry] object of type point in a `List<Double>` object with the point coordinates.
     *
     * @param geometry Geometry of type point.
     * @return The converted `List<Double>` object with format `[longitude, latitude]`.
     */
    @JvmStatic
    @Throws(D2Error::class)
    fun getPoint(geometry: Geometry): List<Double> {
        return getGeometryObject(
            geometry,
            FeatureType.POINT,
            ListSerializer(Double.serializer()),
        )
    }

    /**
     * Converts a [Geometry] object of type polygon in a `List<List<List<Double>>>` object with the
     * polygon coordinates.
     *
     * @param geometry Geometry of type polygon.
     * @return The converted `List<List<List<Double>>>` object with format `[longitude, latitude]`.
     */
    @JvmStatic
    @Throws(D2Error::class)
    fun getPolygon(geometry: Geometry): List<List<List<Double>>> {
        return getGeometryObject(
            geometry,
            FeatureType.POLYGON,
            ListSerializer(ListSerializer(ListSerializer(Double.serializer()))),
        )
    }

    /**
     * Converts a [Geometry] object of type polygon in a `List<List<List<List<Double>>>>` object with the
     * multi polygon coordinates.
     *
     * @param geometry Geometry of type multi polygon.
     * @return The converted `List<List<List<List<Double>>>>` object with format `[longitude, latitude]`.
     */
    @JvmStatic
    @Throws(D2Error::class)
    fun getMultiPolygon(geometry: Geometry): List<List<List<List<Double>>>> {
        return getGeometryObject(
            geometry,
            FeatureType.MULTI_POLYGON,
            ListSerializer(ListSerializer(ListSerializer(ListSerializer(Double.serializer())))),
        )
    }

    /**
     * Build a [Geometry] object of type point from a longitude and a latitude.
     *
     * @param longitude The longitude of a coordinate.
     * @param latitude  The latitude of a coordinate.
     * @return The [Geometry] object of type point created.
     */
    @JvmStatic
    fun createPointGeometry(longitude: Double, latitude: Double): Geometry {
        return createPointGeometry(listOf(longitude, latitude))
    }

    /**
     * Build a [Geometry] object of type point from a `List<Double>` object with the point coordinate.
     *
     * @param point The point coordinate with format `[longitude, latitude]`.
     * @return The [Geometry] object of type point created.
     */
    @JvmStatic
    fun createPointGeometry(point: List<Double>): Geometry {
        return Geometry.builder()
            .type(FeatureType.POINT)
            .coordinates(pointListToCoordinates(point))
            .build()
    }

    /**
     * Build a [Geometry] object of type polygon from a `List<List<List<Double>>>` object with the polygon
     * coordinates.
     *
     * @param polygon The polygon coordinates with format `[longitude, latitude]`.
     * @return The [Geometry] object of type polygon created.
     */
    @JvmStatic
    fun createPolygonGeometry(polygon: List<List<List<Double?>?>?>): Geometry {
        return Geometry.builder()
            .type(FeatureType.POLYGON)
            .coordinates(pointListToCoordinates(polygon))
            .build()
    }

    /**
     * Build a [Geometry] object of type multi polygon from a `List<List<List<List<Double>>>>` object with
     * the multi polygon coordinates.
     *
     * @param multiPolygon The mulit polygon coordinates with format `[longitude, latitude]`.
     * @return The [Geometry] object of type multi polygon created.
     */
    @JvmStatic
    fun createMultiPolygonGeometry(multiPolygon: List<List<List<List<Double?>?>?>?>): Geometry {
        return Geometry.builder()
            .type(FeatureType.MULTI_POLYGON)
            .coordinates(pointListToCoordinates(multiPolygon))
            .build()
    }

    @JvmStatic
    fun isDefinedAndValid(geometry: Geometry?): Boolean {
        var valid = false
        if (geometry?.type() != null && geometry.coordinates() != null) {
            try {
                valid = when (geometry.type()) {
                    FeatureType.POINT -> {
                        getPoint(geometry)
                        true
                    }

                    FeatureType.POLYGON -> {
                        getPolygon(geometry)
                        true
                    }

                    FeatureType.MULTI_POLYGON -> {
                        getMultiPolygon(geometry)
                        true
                    }

                    else -> false
                }
            } catch (error: D2Error) {
                valid = false
            }
        }
        return valid
    }

    @JvmStatic
    fun isValid(geometry: Geometry?): Boolean {
        return geometry == null || isDefinedAndValid(geometry)
    }

    @Throws(D2Error::class)
    fun validateGeometry(geometry: Geometry?) {
        if (!isValid(geometry)) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.INVALID_GEOMETRY_VALUE)
                .errorDescription("Invalid geometry value")
                .build()
        }
    }

    @Suppress("ThrowsCount")
    @Throws(D2Error::class)
    private fun <T> getGeometryObject(
        geometry: Geometry,
        type: FeatureType,
        serializer: KSerializer<T>,
    ): T {
        require(geometry.type() == type) { "The given geometry has not ${type.geometryType} type." }
        val coordinates = geometry.coordinates()
            ?: throw d2Error(null, "The given geometry has no coordinates.")

        return try {
            KotlinxJsonParser.instance.decodeFromString(serializer, coordinates)
        } catch (e: SerializationException) {
            throw d2Error(
                e,
                "It has not been possible to generate a ${type.geometryType} from geometry coordinates: $coordinates.",
            )
        }
    }

    private fun d2Error(e: SerializationException?, errorDescription: String): D2Error {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.SDK)
            .errorCode(D2ErrorCode.IMPOSSIBLE_TO_GENERATE_COORDINATES)
            .errorDescription(errorDescription)
            .originalException(e)
            .build()
    }

    private fun pointListToCoordinates(list: List<*>): String {
        return list.toString().replace(", ", ",")
    }
}
