/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.scopedaccess

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule
import org.hisp.dhis.android.core.scopedaccess.internal.ScopeResolver
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Resolution of a [D2DataScope] into the concrete UID sets the scoped repositories filter on.
 *
 * This is where a grant stops being a declaration and becomes a set of rows, so the org unit
 * hierarchy modes matter: an administrator picks `SELECTED`, `CHILDREN` or `DESCENDANTS` expecting
 * three different answers, and a mode that resolves wider than intended is exactly the failure the
 * closed-by-default model is meant to prevent — and exactly the one that would not announce itself.
 */
@RunWith(JUnit4::class)
class ScopeResolverShould {

    private val root = "ImspTQPwCqd"
    private val child = "O6uvpzGd5pu"
    private val grandchild = "DiszpKrYNg8"

    private val descendantsRepo: OrganisationUnitCollectionRepository = mock {
        // `byPath().like(root)` is a LIKE over the slash-separated ancestor chain, so it returns the
        // whole subtree beneath the root.
        on { blockingGetUids() } doReturn listOf(child, grandchild)
    }

    private val childrenRepo: OrganisationUnitCollectionRepository = mock {
        on { blockingGetUids() } doReturn listOf(child)
    }

    private val captureRepo: OrganisationUnitCollectionRepository = mock {
        on { blockingGetUids() } doReturn listOf(grandchild)
    }

    private val pathConnector: StringFilterConnector<OrganisationUnitCollectionRepository> = mock {
        on { like(any()) } doReturn descendantsRepo
    }

    private val parentConnector: StringFilterConnector<OrganisationUnitCollectionRepository> = mock {
        on { `in`(any<List<String>>()) } doReturn childrenRepo
    }

    private val orgUnitRepo: OrganisationUnitCollectionRepository = mock {
        on { byPath() } doReturn pathConnector
        on { byParentUid() } doReturn parentConnector
        on { byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE) } doReturn captureRepo
    }

    private val orgUnitModule: OrganisationUnitModule = mock {
        on { organisationUnits() } doReturn orgUnitRepo
    }

    private val d2: D2 = mock {
        on { organisationUnitModule() } doReturn orgUnitModule
    }

    private fun resolverFor(orgUnits: OrgUnitScope) =
        ScopeResolver(d2, D2DataScope(orgUnits = orgUnits))

    // ── Org unit hierarchy modes ─────────────────────────────────────────────

    @Test
    fun `resolve SELECTED to the named units and nothing beneath them`() {
        val resolved = resolverFor(OrgUnitScope.Only(setOf(root), OrganisationUnitMode.SELECTED))
            .readableOrgUnits()

        assertThat(resolved).containsExactly(root)
    }

    @Test
    fun `answer SELECTED without querying the database at all`() {
        // The units are already named, so touching the database would be pure cost — and worse, it
        // would make the answer depend on what happens to be synced.
        resolverFor(OrgUnitScope.Only(setOf(root), OrganisationUnitMode.SELECTED)).readableOrgUnits()

        verify(orgUnitRepo, never()).byPath()
        verify(orgUnitRepo, never()).byParentUid()
    }

    @Test
    fun `resolve DESCENDANTS to the whole subtree including the root itself`() {
        // The root is added back explicitly: it may not be stored on this device even though units
        // beneath it are, which is the normal shape of a partial sync.
        val resolved = resolverFor(OrgUnitScope.Only(setOf(root), OrganisationUnitMode.DESCENDANTS))
            .readableOrgUnits()

        assertThat(resolved).containsExactly(root, child, grandchild)
    }

    @Test
    fun `resolve CHILDREN to one level only, not the whole subtree`() {
        // The distinction that matters: `grandchild` is a descendant but not a child, so a CHILDREN
        // grant must not reach it even though the descendants query returned it.
        val resolved = resolverFor(OrgUnitScope.Only(setOf(root), OrganisationUnitMode.CHILDREN))
            .readableOrgUnits()

        assertThat(resolved).containsExactly(root, child)
        assertThat(resolved).doesNotContain(grandchild)
    }

    // ── The other org unit grants ────────────────────────────────────────────

    @Test
    fun `treat All as no restriction rather than as every uid`() {
        // Null and "every uid currently on the device" are different: null keeps the filter off
        // entirely, so nothing depends on what happens to be synced.
        assertThat(resolverFor(OrgUnitScope.All).readableOrgUnits()).isNull()
    }

    @Test
    fun `resolve None to a set that can match nothing`() {
        assertThat(resolverFor(OrgUnitScope.None).readableOrgUnits()).isEmpty()
    }

    @Test
    fun `resolve Capture to the user's own capture units`() {
        assertThat(resolverFor(OrgUnitScope.Capture).readableOrgUnits()).containsExactly(grandchild)
    }

    @Test
    fun `resolve an empty root set to nothing rather than to everything`() {
        val resolved = resolverFor(OrgUnitScope.Only(emptySet(), OrganisationUnitMode.DESCENDANTS))
            .readableOrgUnits()

        assertThat(resolved).isEmpty()
    }

    // ── Caching ──────────────────────────────────────────────────────────────

    @Test
    fun `resolve the hierarchy once however often it is asked for`() {
        // A scoped repository consults this per query, so re-expanding the hierarchy every time
        // would put a LIKE scan on a hot path.
        val resolver = resolverFor(OrgUnitScope.Only(setOf(root), OrganisationUnitMode.DESCENDANTS))

        repeat(3) { resolver.readableOrgUnits() }

        verify(orgUnitRepo, org.mockito.kotlin.times(1)).byPath()
    }

    // ── Writable is an intersection ──────────────────────────────────────────

    @Test
    fun `intersect writable org units with the readable ones`() {
        val resolver = ScopeResolver(
            d2,
            D2DataScope(
                orgUnits = OrgUnitScope.Only(setOf(root), OrganisationUnitMode.SELECTED),
                writable = WritableScope(
                    orgUnits = OrgUnitScope.Only(setOf(root, "somewhereElse"), OrganisationUnitMode.SELECTED),
                ),
            ),
        )

        // "somewhereElse" was named writable but never readable, so it grants nothing.
        assertThat(resolver.writableOrgUnits()).containsExactly(root)
    }

    @Test
    fun `grant no writable org units when none were declared`() {
        val resolver = ScopeResolver(
            d2,
            D2DataScope(orgUnits = OrgUnitScope.Only(setOf(root), OrganisationUnitMode.SELECTED)),
        )

        // Closed by default: readable does not imply writable.
        assertThat(resolver.writableOrgUnits()).isEmpty()
    }
}
