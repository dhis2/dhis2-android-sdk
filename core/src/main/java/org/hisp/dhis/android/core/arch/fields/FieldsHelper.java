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

package org.hisp.dhis.android.core.arch.fields;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Property;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.ArrayList;
import java.util.List;


public class FieldsHelper<O> {

    public <T> Property<O, T> field(String fieldName) {
        return Field.create(fieldName);
    }

    public <T> NestedField<O, T> nestedField(String fieldName) {
        return NestedField.create(fieldName);
    }

    public Field<O, String> uid() {
        return Field.create(BaseIdentifiableObject.UID);
    }

    public NestedField<O, ?> nestedFieldWithUid(String fieldName) {
        NestedField<O, ObjectWithUid> nested = this.nestedField(fieldName);
        return nested.with(ObjectWithUid.uid);
    }

    public List<Property<O, String>> getIdentifiableFields() {
        List<Property<O, String>> list = new ArrayList<>(7);
        addIdentifiableFields(list);
        return list;
    }

    private void addIdentifiableFields(List<Property<O, String>> list) {
        list.add(this.uid());
        list.add(this.<String>field(BaseIdentifiableObject.CODE));
        list.add(this.<String>field(BaseIdentifiableObject.NAME));
        list.add(this.<String>field(BaseIdentifiableObject.DISPLAY_NAME));
        list.add(this.<String>field(BaseIdentifiableObject.CREATED));
        list.add(this.<String>field(BaseIdentifiableObject.LAST_UPDATED));
        list.add(this.<String>field(BaseIdentifiableObject.DELETED));
    }

    public List<Property<O, String>> getNameableFields() {
        List<Property<O, String>> list = new ArrayList<>(11);
        addIdentifiableFields(list);
        list.add(this.<String>field(BaseNameableObject.SHORT_NAME));
        list.add(this.<String>field(BaseNameableObject.DISPLAY_SHORT_NAME));
        list.add(this.<String>field(BaseNameableObject.DESCRIPTION));
        list.add(this.<String>field(BaseNameableObject.DISPLAY_DESCRIPTION));
        return list;
    }
}
