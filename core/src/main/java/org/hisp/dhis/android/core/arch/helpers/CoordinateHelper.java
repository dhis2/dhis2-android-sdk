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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class CoordinateHelper {

    private CoordinateHelper() {}

    /**
     * Converts a {@link Geometry} object of type point in a {@link Coordinates} object.
     *
     * @param geometry Geometry of type point with non null coordinates.
     * @return The converted {@link Coordinates} object.
     */
    public static Coordinates getCoordinatesFromGeometry(Geometry geometry) {
        if (geometry.type() == FeatureType.POINT && geometry.coordinates() != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<Double> coordinateTokens;
            try {
                coordinateTokens = mapper.readValue(geometry.coordinates(),
                        new TypeReference<List<Double>>(){});
            } catch (IOException e) {
                return null;
            }

            return Coordinates.create(coordinateTokens.get(1), coordinateTokens.get(0));
        } else {
            return null;
        }
    }

    /**
     * Converts a {@link Coordinates} object in a {@link Geometry} object of type point.
     *
     * @param coordinates Coordinates to convert.
     * @return The converted {@link Geometry} object.
     */
    public static Geometry getGeometryFromCoordinates(Coordinates coordinates) {
        if (coordinates == null || coordinates.longitude() == null || coordinates.latitude() == null) {
            return null;
        } else {
            List<Double> coordinatesList = Arrays.asList(coordinates.longitude(), coordinates.latitude());

            return Geometry.builder()
                    .type(FeatureType.POINT)
                    .coordinates(coordinatesList.toString())
                    .build();
        }
    }
}