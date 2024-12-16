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

package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class CategoryShould extends BaseObjectShould implements ObjectShould {

    public CategoryShould() {
        super("category/category.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Category category = objectMapper.readValue(jsonStream, Category.class);

        assertThat(category.uid()).isEqualTo("KfdsGBcoiCa");
        assertThat(category.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.155"));
        assertThat(category.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-11-19T12:58:52.558"));

        // names
        assertThat(category.name()).isEqualTo("Births attended by");
        assertThat(category.displayName()).isEqualTo("Births attended by");

        // checking options
        assertThat(category.categoryOptions().get(0).uid()).isEqualTo("TNYQzTHdoxL");
        assertThat(category.categoryOptions().get(1).uid()).isEqualTo("TXGfLxZlInA");
        assertThat(category.categoryOptions().get(2).uid()).isEqualTo("QgULqw9YDu2");
        assertThat(category.categoryOptions().get(3).uid()).isEqualTo("OjIOxG7vgna");
        assertThat(category.categoryOptions().get(4).uid()).isEqualTo("uZUnebiT5DI");
        assertThat(category.categoryOptions().get(5).uid()).isEqualTo("HTHvCohKoXt");
    }
}
