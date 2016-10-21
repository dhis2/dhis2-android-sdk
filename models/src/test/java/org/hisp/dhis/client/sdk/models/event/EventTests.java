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

import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTests {

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_eventNull() {
        Event.validate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_eventEmpty() {
        Event event = new Event();
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnUidNull() {
        Event event = getTestEvent();
        event.setUid(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnUidLengthNot11() {
        Event event = getTestEvent();
        String uidLengthTen = "1234567890";
        event.setUid(uidLengthTen);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnCreatedNull() {
        Event event = getTestEvent();
        event.setCreated(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnLastUpdatedNull() {
        Event event = getTestEvent();
        event.setLastUpdated(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnEventStatusNull() {
        Event event = getTestEvent();
        event.setStatus(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnProgramNull() {
        Event event = getTestEvent();
        event.setProgram(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnProgramStageNull() {
        Event event = getTestEvent();
        event.setProgramStage(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnOrganisationUnitNull() {
        Event event = getTestEvent();
        event.setOrgUnit(null);
        Event.validate(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnEventDateNull() {
        Event event = getTestEvent();
        event.setEventDate(null);
        Event.validate(event);
    }

    @Test
    public void testValidate() {
        Event event = getTestEvent();
        Event.validate(event);
    }

    @Test
    public void testVerifyEquals() {
        EqualsVerifier
                .forClass(Event.class)
                .suppress(Warning.NONFINAL_FIELDS)  // Add to allow class to be mutable
                .withRedefinedSuperclass()          // An instance of this class should not be equal to an instance of its superclass
                .verify();
    }

    private static Event getTestEvent() {
        Date date = new java.util.Date();

        Event event = new Event();
        event.setUid("eventUid001");
        event.setCreated(date);
        event.setLastUpdated(date);
        event.setStatus(EventStatus.ACTIVE);
        event.setProgram("programUid1");
        event.setProgramStage("prgrmStgUid");
        event.setOrgUnit("orgUnitUid1");
        event.setEventDate(date);
        return event;
    }

    @Test
    public void testEquals() {
        Event eventOne = new Event();
        Event eventTwo = new Event();
        Event eventThree = new Event();

        eventOne.setUid("abc");
        eventTwo.setUid("abc");
        eventThree.setUid("def");

        assertThat(eventOne).isEqualTo(eventTwo);
        assertThat(eventTwo).isEqualTo(eventOne);
        assertThat(eventOne).isNotEqualTo(eventThree);




        TrackedEntityDataValue trackedEntityDataValue1 = new TrackedEntityDataValue();
        trackedEntityDataValue1.setDataElement("uid1");
        trackedEntityDataValue1.setValue("1");

        TrackedEntityDataValue trackedEntityDataValue2 = new TrackedEntityDataValue();
        trackedEntityDataValue2.setDataElement("uid2");
        trackedEntityDataValue2.setValue("2");

        TrackedEntityDataValue trackedEntityDataValue3 = new TrackedEntityDataValue();
        trackedEntityDataValue3.setDataElement("uid2");
        trackedEntityDataValue3.setValue("2");

        List<TrackedEntityDataValue> list1 = Arrays.asList(trackedEntityDataValue1, trackedEntityDataValue2);
        List<TrackedEntityDataValue> list2 = Arrays.asList(trackedEntityDataValue1, trackedEntityDataValue2);
        List<TrackedEntityDataValue> list3 = Arrays.asList(trackedEntityDataValue1, trackedEntityDataValue3);

        eventOne.setDataValues(list1);
        eventTwo.setDataValues(list2);

        assertThat(eventOne).isEqualTo(eventTwo);

        // TODO: How should the equals method work for objects with collections? Generate equals and hashCode for TrackedEntityDataValue
        eventTwo.setDataValues(list3);
        assertThat(eventOne).isEqualTo(eventTwo);
    }

//    @Test
//    public void testHashCode() {
//        Event eventOne = new Event();
//        Event eventTwo = new Event();
//        Event eventThree = new Event();
//
//        eventOne.setUid("abc");
//        eventTwo.setUid("abc");
//
//        eventThree.setUid("def");
//
//        // Test that same object has same hashCode
//        assertThat(eventOne.hashCode()).isEqualTo(eventOne.hashCode());
//
//        // Test that different objects with same values have same hashCode
//        assertThat(eventOne.hashCode()).isEqualTo(eventTwo.hashCode());
//
//        // Test that different objetcts with different values don't have same hashCode
//        assertThat(eventOne.hashCode()).isNotEqualTo(eventThree.hashCode());
//    }


}
