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
package org.hisp.dhis.android.core.arch.helpers;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GeometryHelper {

    private GeometryHelper() {
    }

    /**
     * Checks if the geometry contains a point.
     *
     * @param geometry The {@link Geometry} to check.
     * @return A boolean that indicates if the geometry contains a point.
     */
    public static boolean containsAPoint(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.POINT;
    }

    /**
     * Checks if the geometry contains a polygon.
     *
     * @param geometry The {@link Geometry} to check.
     * @return A boolean that indicates if the geometry contains a polygon.
     */
    public static boolean containsAPolygon(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.POLYGON;
    }

    /**
     * Checks if the geometry contains a multi polygon.
     *
     * @param geometry The {@link Geometry} to check.
     * @return A boolean that indicates if the geometry contains a multi polygon.
     */
    public static boolean containsAMultiPolygon(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.MULTI_POLYGON;
    }

    /**
     * Converts a {@link Geometry} object of type point in a {@code List<Double>} object with the point coordinates.
     *
     * @param geometry Geometry of type point.
     * @return The converted {@code List<Double>} object with format {@code [longitude, latitude]}.
     */
    public static List<Double> getPoint(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.POINT, new TypeReference<List<Double>>() {
        });
    }

    /**
     * Converts a {@link Geometry} object of type polygon in a {@code List<List<List<Double>>>} object with the
     * polygon coordinates.
     *
     * @param geometry Geometry of type polygon.
     * @return The converted {@code List<List<List<Double>>>} object with format {@code [longitude, latitude]}.
     */
    public static List<List<List<Double>>> getPolygon(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.POLYGON, new TypeReference<List<List<List<Double>>>>() {
        });
    }

    /**
     * Converts a {@link Geometry} object of type polygon in a {@code List<List<List<List<Double>>>>} object with the
     * multi polygon coordinates.
     *
     * @param geometry Geometry of type multi polygon.
     * @return The converted {@code List<List<List<List<Double>>>>} object with format {@code [longitude, latitude]}.
     */
    public static List<List<List<List<Double>>>> getMultiPolygon(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.MULTI_POLYGON,
                new TypeReference<List<List<List<List<Double>>>>>() {
                });
    }

    /**
     * Build a {@link Geometry} object of type point from a longitude and a latitude.
     *
     * @param longitude The longitude of a coordinate.
     * @param latitude  The latitude of a coordinate.
     * @return The {@link Geometry} object of type point created.
     */
    public static Geometry createPointGeometry(Double longitude, Double latitude) {
        ArrayList<Double> point = new ArrayList<>();
        Collections.addAll(point, longitude, latitude);
        return createPointGeometry(point);
    }

    /**
     * Build a {@link Geometry} object of type point from a {@code List<Double>} object with the point coordinate.
     *
     * @param point The point coordinate with format {@code [longitude, latitude]}.
     * @return The {@link Geometry} object of type point created.
     */
    public static Geometry createPointGeometry(List<Double> point) {
        return Geometry.builder()
                .type(FeatureType.POINT)
                .coordinates(pointListToCoordinates(point))
                .build();
    }

    /**
     * Build a {@link Geometry} object of type polygon from a {@code List<List<List<Double>>>} object with the polygon
     * coordinates.
     *
     * @param polygon The polygon coordinates with format {@code [longitude, latitude]}.
     * @return The {@link Geometry} object of type polygon created.
     */
    public static Geometry createPolygonGeometry(List<List<List<Double>>> polygon) {
        return Geometry.builder()
                .type(FeatureType.POLYGON)
                .coordinates(pointListToCoordinates(polygon))
                .build();
    }

    /**
     * Build a {@link Geometry} object of type multi polygon from a {@code List<List<List<List<Double>>>>} object with
     * the multi polygon coordinates.
     *
     * @param multiPolygon The mulit polygon coordinates with format {@code [longitude, latitude]}.
     * @return The {@link Geometry} object of type multi polygon created.
     */
    public static Geometry createMultiPolygonGeometry(List<List<List<List<Double>>>> multiPolygon) {
        return Geometry.builder()
                .type(FeatureType.MULTI_POLYGON)
                .coordinates(pointListToCoordinates(multiPolygon))
                .build();
    }

    public static boolean isDefinedAndValid(Geometry geometry) {
        boolean valid = false;
        if (geometry != null && geometry.type() != null && geometry.coordinates() != null) {
            try {
                switch (geometry.type()) {
                    case POINT:
                        getPoint(geometry);
                        valid = true;
                        break;
                    case POLYGON:
                        getPolygon(geometry);
                        valid = true;
                        break;
                    case MULTI_POLYGON:
                        getMultiPolygon(geometry);
                        valid = true;
                        break;
                    default:
                        break;
                }
            } catch (D2Error error) {
                valid = false;
            }
        }
        return valid;
    }

    public static Boolean isValid(@Nullable Geometry geometry) {
        return geometry == null || isDefinedAndValid(geometry);
    }

    public static void validateGeometry(@Nullable Geometry geometry) throws D2Error {
        if (!isValid(geometry)) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.INVALID_GEOMETRY_VALUE)
                    .errorDescription("Invalid geometry value")
                    .build();
        }
    }

    private static <T> T getGeometryObject(Geometry geometry, FeatureType type, TypeReference<T> typeReference)
            throws D2Error {
        if (geometry.type() != type) {
            throw d2Error(null, "The given geometry has not " + type.getGeometryType() + " type.");
        }

        if (geometry.coordinates() == null) {
            throw d2Error(null, "The given geometry has no coordinates.");
        }

        try {
            return new ObjectMapper().readValue(geometry.coordinates(), typeReference);
        } catch (IOException e) {
            throw d2Error(e, "It has not been possible to generate a " + type.getGeometryType() +
                    " from geometry coordinates: " + geometry.coordinates() + ".");
        }
    }

    private static D2Error d2Error(IOException e, String errorDescription) {
        return D2Error.builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.IMPOSSIBLE_TO_GENERATE_COORDINATES)
                .errorDescription(errorDescription)
                .originalException(e)
                .build();
    }

    private static String pointListToCoordinates(List<?> list) {
        return list.toString().replace(", ", ",");
    }
}