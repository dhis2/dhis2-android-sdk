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
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * The grant algebra, and the one piece of SQL behaviour it leans on.
 *
 * The distinction that matters throughout: `null` from [UidScope.uidsOrNull] means "apply no
 * filter", and an empty set means "apply a filter that matches nothing". Conflating the two would
 * turn a grant of nothing into a grant of everything, which is the worst possible direction for this
 * mistake to go.
 */
@RunWith(JUnit4::class)
class UidScopeShould {

    @Test
    fun `distinguish no restriction from an empty restriction`() {
        assertThat(UidScope.All.uidsOrNull()).isNull()
        assertThat(UidScope.None.uidsOrNull()).isEmpty()
        assertThat(UidScope.Only(setOf("a")).uidsOrNull()).containsExactly("a")
    }

    @Test
    fun `render an empty grant as a filter that cannot match`() {
        // ScopedD2 turns an empty grant into `.in(emptyList())`. SQLite — unlike most engines —
        // accepts an empty IN list and evaluates it to false, which is what makes "granted nothing"
        // mean "sees nothing" rather than being a syntax error or, worse, no filter at all.
        val condition = FilterItemOperator.IN.getSqlCondition("dataElement", "()")

        assertThat(condition).contains("IN")
        assertThat(condition).contains("()")
    }

    @Test
    fun `treat a program as in scope only when it was granted`() {
        val scope = UidScope.of("granted")

        assertThat(scope.allows("granted")).isTrue()
        assertThat(scope.allows("other")).isFalse()
        assertThat(scope.allows(null)).isFalse()
    }

    @Test
    fun `let All allow anything except null`() {
        assertThat(UidScope.All.allows("anything")).isTrue()
        assertThat(UidScope.All.allows(null)).isFalse()
    }

    @Test
    fun `let None allow nothing`() {
        assertThat(UidScope.None.allows("anything")).isFalse()
    }

    @Test
    fun `collapse an empty collection to None rather than an empty Only`() {
        assertThat(UidScope.of(emptyList())).isEqualTo(UidScope.None)
        assertThat(UidScope.of(listOf("a"))).isEqualTo(UidScope.Only(setOf("a")))
    }

    @Test
    fun `intersect so a grant can only ever shrink`() {
        val granted = UidScope.of("a", "b")

        assertThat(granted.intersect(UidScope.All)).isEqualTo(granted)
        assertThat(UidScope.All.intersect(granted)).isEqualTo(granted)
        assertThat(granted.intersect(UidScope.of("b", "c"))).isEqualTo(UidScope.Only(setOf("b")))
        assertThat(granted.intersect(UidScope.of("c"))).isEqualTo(UidScope.Only(emptySet()))
        assertThat(granted.intersect(UidScope.None)).isEqualTo(UidScope.None)
    }

    @Test
    fun `report emptiness for both None and an empty Only`() {
        assertThat(UidScope.None.isEmpty()).isTrue()
        assertThat(UidScope.Only(emptySet()).isEmpty()).isTrue()
        assertThat(UidScope.Only(setOf("a")).isEmpty()).isFalse()
        assertThat(UidScope.All.isEmpty()).isFalse()
    }

    @Test
    fun `grant nothing by default`() {
        // A D2DataScope() built with no arguments must not be a back door.
        val scope = D2DataScope()

        assertThat(scope.programs).isEqualTo(UidScope.None)
        assertThat(scope.dataSets).isEqualTo(UidScope.None)
        assertThat(scope.orgUnits).isEqualTo(OrgUnitScope.None)
        assertThat(scope.capabilities).isEmpty()
        assertThat(scope.hasAnyWrite()).isFalse()
        assertThat(scope.writablePrograms().isEmpty()).isTrue()
    }

    @Test
    fun `never make something writable that is not readable`() {
        val scope = D2DataScope(
            programs = UidScope.of("readable"),
            writable = WritableScope(programs = UidScope.of("readable", "sneaky")),
        )

        assertThat(scope.writablePrograms()).isEqualTo(UidScope.Only(setOf("readable")))
    }
}
