/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.organisationunit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OrganisationUnitTree {

    public static final String DELIMITER = "/";

    private OrganisationUnitTree() {}

    /**
     * Extract a set of root uid's of OrganisationUnits, accessible by the user,
     * from a list of OrganisationUnits and a list of Assigned OrganisationUnits.
     * Based on the paths of the OrganisationUnits from the list.
     *
     * @param organisationUnits
     * @return set of root uid's
     */
    public static Set<String> findRoots(List<OrganisationUnit> organisationUnits) throws IllegalArgumentException {
        Set<String> rootNodes = new HashSet<>();
        if (organisationUnits == null || organisationUnits.isEmpty()) {
            return rootNodes; //no assigned uid's, so don't waste time & quit early
        }
        //extract a list of the uid's:
        List<String> organisationUnitUids = new ArrayList<>(organisationUnits.size());
        for (OrganisationUnit orgUnit : organisationUnits) {
            organisationUnitUids.add(orgUnit.uid());
        }

        for (int i = 0, size = organisationUnits.size(); i < size; i++) {
            String path = organisationUnits.get(i).path();

            if (path == null || path.isEmpty()) { //path shouldn't be empty or null.
                throw new IllegalArgumentException("OrganisationUnit's path should not be null or empty!");
            } else {
                getRootFromPath(rootNodes, organisationUnitUids, path);
            }
        }
        return rootNodes;
    }

    private static void getRootFromPath(Set<String> rootNodes, List<String> organisationUnitUids, String path) {
        String[] result = path.split(DELIMITER);
        for (int j = 0, rSize = result.length; j < rSize; j++) {
            if (rootNodes.contains(result[j])) {
                break; //already in root nodes stop iterating.
            } else if (organisationUnitUids.contains(result[j])) {
                rootNodes.add(result[j]);
                break;
            }
        }
    }
}
