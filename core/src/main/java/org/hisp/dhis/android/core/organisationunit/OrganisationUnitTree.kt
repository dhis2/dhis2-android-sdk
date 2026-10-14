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
package org.hisp.dhis.android.core.organisationunit

object OrganisationUnitTree {
    const val DELIMITER: String = "/"

    /**
     * Extract a set of root uid's of OrganisationUnits, accessible by the user,
     * from a list of OrganisationUnits and a list of Assigned OrganisationUnits.
     * Based on the paths of the OrganisationUnits from the list.
     *
     * @param organisationUnits
     * @return set of root uid's
     */
    @Throws(IllegalArgumentException::class)
    fun findRoots(organisationUnits: List<OrganisationUnit>?): Set<OrganisationUnit> {
        if (organisationUnits.isNullOrEmpty()) return emptySet()

        val orgUnitByUid = organisationUnits.associateBy { it.uid() }

        return organisationUnits.mapNotNull { orgUnit ->
            val path = orgUnit.path()
            require(!path.isNullOrEmpty()) { "OrganisationUnit's path should not be null or empty!" }
            path.splitToSequence(DELIMITER)
                .filter { it.isNotEmpty() }
                .firstNotNullOfOrNull { uid -> orgUnitByUid[uid] }
        }.toSet()
    }

    @Throws(IllegalArgumentException::class)
    fun findRootsOutsideSearchScope(
        allRootCaptureOrgUnits: Set<OrganisationUnit>?,
        rootSearchOrgUnits: Set<OrganisationUnit>?,
    ): Set<OrganisationUnit> {
        return if (allRootCaptureOrgUnits.isNullOrEmpty()) {
            emptySet()
        } else if (rootSearchOrgUnits.isNullOrEmpty()) {
            allRootCaptureOrgUnits
        } else {
            allRootCaptureOrgUnits.filter { rootCaptureOrgUnit ->
                !inScope(rootCaptureOrgUnit, rootSearchOrgUnits)
            }.toSet()
        }
    }

    @Throws(IllegalArgumentException::class)
    fun getCaptureOrgUnitsInSearchScope(
        allSearchOrgUnits: List<OrganisationUnit>,
        allRootCaptureOrgUnits: Set<OrganisationUnit>,
        rootCaptureOrgUnitsOutsideSearchScope: Set<OrganisationUnit>,
    ): Set<OrganisationUnit> {
        val rootCaptureOrgUnitsInSearchScope = allRootCaptureOrgUnits.filter { rootCaptureOrgUnit ->
            uidInOrgUnitCollection(
                rootCaptureOrgUnit.uid(),
                rootCaptureOrgUnitsOutsideSearchScope,
            ) == null
        }

        val captureOrgUnitsInSearchScope = allSearchOrgUnits.filter { searchOrgUnit ->
            inScope(searchOrgUnit, rootCaptureOrgUnitsInSearchScope)
        }

        return captureOrgUnitsInSearchScope.toSet()
    }

    private fun inScope(
        orgUnit: OrganisationUnit,
        rootOrgUnits: Collection<OrganisationUnit>,
    ): Boolean {
        require(
            !(orgUnit.path().isNullOrEmpty()),
        ) { "OrganisationUnit's path should not be empty!" }
        return rootOrgUnits.any { rootOrgUnit ->
            orgUnit.path()!!.contains(rootOrgUnit.uid())
        }
    }

    private fun uidInOrgUnitCollection(
        orgUnitUid: String,
        organisationUnits: Collection<OrganisationUnit>,
    ): OrganisationUnit? {
        return organisationUnits.find { organisationUnit ->
            orgUnitUid == organisationUnit.uid()
        }
    }
}
