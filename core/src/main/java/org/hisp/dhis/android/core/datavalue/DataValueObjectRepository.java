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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteValueObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteWithValueObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Date;
import java.util.Map;

public final class DataValueObjectRepository
        extends ReadWriteWithValueObjectRepositoryImpl<DataValue, DataValueObjectRepository>
        implements ReadWriteValueObjectRepository<DataValue> {

    private final String period;
    private final String organisationUnit;
    private final String dataElement;
    private final String categoryOptionCombo;
    private final String attributeOptionCombo;

    DataValueObjectRepository(
            final DataValueStore store,
            final Map<String, ChildrenAppender<DataValue>> childrenAppenders,
            final RepositoryScope scope,
            final String period,
            final String organisationUnit,
            final String dataElement,
            final String categoryOptionCombo,
            final String attributeOptionCombo) {
        super(store, childrenAppenders, scope, s -> new DataValueObjectRepository(store, childrenAppenders, s,
                period, organisationUnit, dataElement, categoryOptionCombo, attributeOptionCombo));
        this.period = period;
        this.organisationUnit = organisationUnit;
        this.dataElement = dataElement;
        this.categoryOptionCombo = categoryOptionCombo;
        this.attributeOptionCombo = attributeOptionCombo;
    }

    public Unit set(String value) throws D2Error {
        objectWithValue = setBuilder().value(value).build();
        return setObject(objectWithValue);
    }

    public Unit setFollowUp(Boolean followUp) throws D2Error {
        return setObject(setBuilder().followUp(followUp).build());
    }

    public Unit setComment(String comment) throws D2Error {
        return setObject(setBuilder().comment(comment).build());
    }

    @Override
    public void delete() throws D2Error {
        DataValue dataValue = getWithoutChildren();
        if (dataValue.state() == State.TO_POST) {
            super.delete(dataValue);
        } else {
            setObject(dataValue.toBuilder().state(State.TO_DELETE).build());
        }
    }

    private DataValue.Builder setBuilder() {
        Date date = new Date();
        if (exists()) {
            return getWithoutChildren().toBuilder()
                    .state(State.TO_UPDATE)
                    .lastUpdated(date);
        } else {
            return DataValue.builder()
                    .state(State.TO_POST)
                    .created(date)
                    .lastUpdated(date)
                    .followUp(Boolean.FALSE)
                    .period(period)
                    .organisationUnit(organisationUnit)
                    .dataElement(dataElement)
                    .categoryOptionCombo(categoryOptionCombo)
                    .attributeOptionCombo(attributeOptionCombo);
        }
    }
}