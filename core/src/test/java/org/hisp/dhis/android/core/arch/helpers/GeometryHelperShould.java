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

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class GeometryHelperShould {

    private final static Double longitude1 = 43.34532;
    private final static Double latitude1 = -23.98234;
    private final static Double longitude2 = -10.02322;
    private final static Double latitude2 = 3.74597;

    @Test
    public void get_point_from_geometry_from_list() throws D2Error {
        List<Double> coordinates = Lists.newArrayList(longitude1, latitude1);

        Geometry geometry = GeometryHelper.createPointGeometry(coordinates);

        List<Double> point = GeometryHelper.getPoint(geometry);

        assertThat(point).isEqualTo(coordinates);
    }

    @Test
    public void get_point_from_geometry_from_longitude_and_latitude() throws D2Error {
        Geometry geometry = GeometryHelper.createPointGeometry(longitude1, latitude1);

        List<Double> point = GeometryHelper.getPoint(geometry);

        assertThat(point.get(0)).isEqualTo(longitude1);
        assertThat(point.get(1)).isEqualTo(latitude1);
    }

    @Test
    public void get_polygon_from_geometry() throws D2Error {
        List<List<List<Double>>> coordinates =
                Collections.singletonList(Arrays.asList(
                        Lists.newArrayList(longitude1, latitude1),
                        Lists.newArrayList(longitude2, latitude2)
                ));

        Geometry geometry = GeometryHelper.createPolygonGeometry(coordinates);

        List<List<List<Double>>> polygon = GeometryHelper.getPolygon(geometry);

        assertThat(polygon).isEqualTo(coordinates);
    }

    @Test
    public void get_multipolygon_from_geometry() throws D2Error {
        List<List<List<List<Double>>>> coordinates =
                Arrays.asList(
                        Arrays.asList(
                                Arrays.asList(
                                        Lists.newArrayList(longitude1, latitude1),
                                        Lists.newArrayList(longitude2, latitude2)
                                ),
                                Collections.singletonList(
                                        Lists.newArrayList(longitude1, latitude2)
                                )),
                        Collections.singletonList(Collections.singletonList(
                                Lists.newArrayList(longitude2, latitude1)
                        )));

        Geometry geometry = GeometryHelper.createMultiPolygonGeometry(coordinates);

        List<List<List<List<Double>>>> multiPolygon = GeometryHelper.getMultiPolygon(geometry);

        assertThat(multiPolygon).isEqualTo(coordinates);
    }

    @Test
    public void should_build_coordinates_without_spaces() {
        List<Double> coordinates = Lists.newArrayList(longitude1, latitude1);

        Geometry geometry = GeometryHelper.createPointGeometry(coordinates);

        String expectedCoordinates = "[" + longitude1 + "," + latitude1 + "]";
        assertThat(geometry.coordinates()).isEqualTo(expectedCoordinates);
    }
}
