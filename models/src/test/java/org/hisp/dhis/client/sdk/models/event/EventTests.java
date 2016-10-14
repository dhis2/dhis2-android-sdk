/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.models.event;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTests {

    @Test(expected = IllegalArgumentException.class)
    public void testValidate() {
        Event event = new Event();
        event.setUid("abc");

        // TODO: Assert exception inside test
        Event.validate(event);

        // TODO: Assert that null and empty  object fails

        //TODO: Assert that "proper" objects don' fail
    }

    @Test
    public void testEquals() {
        Event eventOne = new Event();
        Event eventTwo = new Event();

        eventOne.setUid("abc");
        eventTwo.setUid("abc");

        assertThat(eventOne).isEqualTo(eventTwo);
        assertThat(eventTwo).isEqualTo(eventOne);

        //TODO: Test notEquals
    }

    @Test
    public void testHashCode() {
        Event eventOne = new Event();
        Event eventTwo = new Event();
        Event eventThree = new Event();

        eventOne.setUid("abc");
        eventTwo.setUid("abc");

        eventThree.setUid("def");

        // Test that same object has same hashCode
        assertThat(eventOne.hashCode()).isEqualTo(eventOne.hashCode());

        // Test that different objects with same values have same hashCode
        assertThat(eventOne.hashCode()).isEqualTo(eventTwo.hashCode());

        // Test that different objetcts with different values don't have same hashCode
        assertThat(eventOne.hashCode()).isNotEqualTo(eventThree.hashCode());
    }

    @Test
    public void crazyTestEquals() {
        // TODO: Add comments
        // TODO: Consider abstracting this further
        EqualsVerifier
                .forClass(Event.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withRedefinedSuperclass()
                .verify();
    }
}
