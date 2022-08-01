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

package org.hisp.dhis.android.core.data.organisationunit;

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;

public class OrganisationUnitSamples {

    public static OrganisationUnit getOrganisationUnit(Long id, String uid) {
        OrganisationUnit.Builder builder = OrganisationUnit.builder();

        List<String> displayNamePathArray = new ArrayList<>(3);
        displayNamePathArray.add("grandpa");
        displayNamePathArray.add("dad");
        displayNamePathArray.add("me");

        fillNameableProperties(builder);
        return builder
                .id(id)
                .uid(uid)
                .path("test_path")
                .openingDate(FillPropertiesTestUtils.CREATED)
                .closedDate(FillPropertiesTestUtils.LAST_UPDATED)
                .level(100)
                .geometry(Geometry.builder()
                        .type(FeatureType.POLYGON)
                        .coordinates("[11.0, 11.0]")
                        .build())
                .parent(null)
                .displayNamePath(displayNamePathArray)
                .build();
    }

    public static OrganisationUnit getOrganisationUnit(String uid) {
        return getOrganisationUnit(1L, uid);
    }

    public static OrganisationUnit getOrganisationUnit() {
        return getOrganisationUnit(1L, "UID");
    }

    public static OrganisationUnit getAfroArabClinic() throws ParseException {
        return OrganisationUnit.builder()
                .uid("cDw53Ej8rju")
                .code("OU_278371")
                .name("Afro Arab Clinic")
                .displayName("Afro Arab Clinic")
                .created("2012-02-17T15:54:39.987")
                .lastUpdated("2017-05-22T15:21:48.518")
                .shortName("Afro Arab Clinic")
                .displayShortName("Afro Arab Clinic")
                .description(null)
                .displayDescription(null)
                .path("/ImspTQPwCqd/at6UHUQatSo/qtr8GGlm4gg/cDw53Ej8rju")
                .openingDate("2008-01-01T00:00:00.000")
                .level(4)
                .parent(ObjectWithUid.create("qtr8GGlm4gg"))
                .displayNamePath(Collections.singletonList("Afro Arab Clinic"))
                .build();
    }


    public static OrganisationUnit getAdonkiaCHP() throws ParseException {
        return OrganisationUnit.builder()
                .uid("Rp268JB6Ne4")
                .code("OU_651071")
                .name("Adonkia CHP")
                .displayName("Adonkia CHP")
                .created("2012-02-17T15:54:39.987")
                .lastUpdated("2017-05-22T15:21:48.515")
                .shortName("Adonkia CHP")
                .displayShortName("Adonkia CHP")
                .description(null)
                .displayDescription(null)
                .path("/ImspTQPwCqd/at6UHUQatSo/qtr8GGlm4gg/Rp268JB6Ne4")
                .openingDate("2010-01-01T00:00:00.000")
                .level(4)
                .parent(ObjectWithUid.create("qtr8GGlm4gg"))
                .displayNamePath(Collections.singletonList("Adonkia CHP"))
                .build();
    }

    public static OrganisationUnit getOrganisationUnit(String name, int level, OrganisationUnit parent) throws ParseException {
        String parentPath = parent == null ? "" : parent.path();
        String uid = new UidGeneratorImpl().generate();
        return OrganisationUnit.builder()
                .uid(uid)
                .code("code")
                .name(name)
                .displayName(name)
                .created("2012-02-17T15:54:39.987")
                .lastUpdated("2017-05-22T15:21:48.515")
                .shortName(name)
                .displayShortName(name)
                .description(null)
                .displayDescription(null)
                .path(parentPath + "/" + uid)
                .openingDate("2010-01-01T00:00:00.000")
                .level(level)
                .parent(parent == null ? null : ObjectWithUid.create(parent.uid()))
                .displayNamePath(Collections.singletonList(name))
                .build();
    }
}