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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OrganisationUnitTreeShould {
    //Assigned uid's:
    private final String ASSIGNED_L11 = "Level11";
    //Not assigned uid's:
    private final String UNASSIGNED_L12 = "Level12";
    private final String UNASSIGNED_L23 = "Level23";
    private final String UNASIGNED_ROOT = "RootOrtUnit";

    private String[] paths = {
            "/RootOrgUnit/Level11/",
            "/RootOrgUnit/Level11/Level21",
            "/RootOrgUnit/Level11/Level22",
            "/RootOrgUnit/Level12/Level24",
            "/RootOrgUnit/Level13"
    };
    private String[] uids = {"Level11", "Level21", "Level22", "Level24", "Level13"};
    private String[] expectedResult = {"Level11", "Level13", "Level24"};

    @Test
    public void return_all_root_uids_when_find_roots() {
        //create a bunch of dummy Organisation units from the strings:
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        for (int i = 0, size = uids.length; i < size; i++) {
            orgUnits.add(OrganisationUnit.create(
                    uids[i],
                    null, null, null, null, null, null, null, null, null, null,
                    paths[i],
                    null, null, null, null, false
            ));
        }
        Set<String> rootUids = OrganisationUnitTree.findRoots(orgUnits);
        //assert that: returned uid list does not contain unassigned & root.
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse();
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse();
        assertThat(rootUids.contains(UNASIGNED_ROOT)).isFalse();
        //assert that: returned uid list contains what it should: expectedResult.
        assertThat(rootUids.size()).isEqualTo(expectedResult.length);

        assertThat(rootUids.containsAll(Arrays.asList(expectedResult))).isTrue();
    }

    @Test
    public void return_all_root_uids_missing_slashes_when_find_roots() {
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        orgUnits.add(OrganisationUnit.create(
                uids[0],
                null, null, null, null, null, null, null, null, null, null,
                "RootOrgUnit/Level11",
                null, null, null, null, false));

        Set<String> rootUids = OrganisationUnitTree.findRoots(orgUnits);
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse();
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse();
        assertThat(rootUids.contains(UNASIGNED_ROOT)).isFalse();
        assertThat(rootUids.size()).isEqualTo(1);

        assertThat(rootUids.contains(ASSIGNED_L11)).isTrue();
    }

    @Test
    public void return_all_root_uids_double_slashes_when_find_roots() {
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        orgUnits.add(OrganisationUnit.create(
                uids[0],
                null, null, null, null, null, null, null, null, null, null,
                "//RootOrgUnit//Level11//",
                null, null, null, null, false));

        Set<String> rootUids = OrganisationUnitTree.findRoots(orgUnits);
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse();
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse();
        assertThat(rootUids.contains(UNASIGNED_ROOT)).isFalse();
        assertThat(rootUids.size()).isEqualTo(1);

        assertThat(rootUids.contains(ASSIGNED_L11)).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void return_all_root_uids_null_paths_when_find_roots() {
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        orgUnits.add(OrganisationUnit.create(
                uids[0],
                null, null, null, null, null, null, null, null, null, null,
                null, //<--passing null path
                null, null, null, null, false));

        OrganisationUnitTree.findRoots(orgUnits);
    }

    @Test
    public void return_all_root_empty_uids_list_when_find_roots_with_empty_list() {
        Set<String> rootUids = OrganisationUnitTree.findRoots(new ArrayList<OrganisationUnit>());
        assertThat(rootUids.isEmpty()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void thrown_illegal_argument_exception_when_find_roots_uids_with_empty_paths() {
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        orgUnits.add(OrganisationUnit.create(
                uids[0],
                null, null, null, null, null, null, null, null, null, null,
                "", //<--passing empty path
                null, null, null, null, false));

        OrganisationUnitTree.findRoots(orgUnits);
    }

    @Test
    public void return_root_uids_when_have_not_assigned() {
        List<OrganisationUnit> orgUnits = new ArrayList<>(uids.length);
        orgUnits.add(OrganisationUnit.create(
                uids[0],
                null, null, null, null, null, null, null, null, null, null,
                "/RootOrgUnit//Level11/",
                null, null, null, null, false));
        OrganisationUnitTree.findRoots(orgUnits);
    }
    @Test
    public void findRoots_shouldReturnRootUids_NullList() {
        Set<String> rootUids = OrganisationUnitTree.findRoots(null);
        assertThat(rootUids.isEmpty()).isTrue();
    }
}
