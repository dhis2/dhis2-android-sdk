/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.helpers;

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

    public static boolean conatainsAPoint(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.POINT;
    }

    public static boolean containsAPolygon(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.POLYGON;
    }

    public static boolean containtsAMultiPolygon(Geometry geometry) {
        return geometry != null && geometry.type() == FeatureType.MULTI_POLYGON;
    }

    public static List<Double> getPoint(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.POINT, new TypeReference<List<Double>>(){});
    }

    public static List<List<List<Double>>> getPolygon(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.POLYGON, new TypeReference<List<List<List<Double>>>>(){});
    }

    public static List<List<List<List<Double>>>> getMultiPolygon(Geometry geometry) throws D2Error {
        return getGeometryObject(geometry, FeatureType.MULTI_POLYGON,
                new TypeReference<List<List<List<List<Double>>>>>(){});
    }

    public static Geometry createPointGeometry(Double longitude, Double latitude) {
        ArrayList<Double> point = new ArrayList<>();
        Collections.addAll(point, longitude, latitude);
        return createPointGeometry(point);
    }

    public static Geometry createPointGeometry(List<Double> point) {
        return Geometry.builder()
                .type(FeatureType.POINT)
                .coordinates(point.toString())
                .build();
    }

    public static Geometry createPolygonGeometry(List<List<List<Double>>> polygon) {
        return Geometry.builder()
                .type(FeatureType.POLYGON)
                .coordinates(polygon.toString())
                .build();
    }

    public static Geometry createMultiPolygonGeometry(List<List<List<List<Double>>>> multiPolygon) {
        return Geometry.builder()
                .type(FeatureType.MULTI_POLYGON)
                .coordinates(multiPolygon.toString())
                .build();
    }

    private static <T> T getGeometryObject(Geometry geometry, FeatureType type, TypeReference typeReference)
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
}