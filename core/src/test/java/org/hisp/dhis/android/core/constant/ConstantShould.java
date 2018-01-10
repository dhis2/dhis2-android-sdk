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

package org.hisp.dhis.android.core.constant;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConstantShould {

    /**
     * Checks whether the parsing from JSON string works as expected. Payload intentionally
     * contains properties which are not present in the model, in order to check if model
     * explicitly ignores unknown values.
     *
     * @throws IOException if parsing fails.
     */
    @Test
    public void map_json_to_model() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        // parse payload into model instance
        Constant constant = objectMapper.readValue("{" +
                "\"created\":\"2013-03-11T16:39:33.083\"," +
                "\"lastUpdated\":\"2013-03-11T16:39:33.083\"," +
                "\"name\":\"Pi\"," +
                "\"href\":\"https://play.dhis2.org/demo/api/constants/bCqvfPR02Im\"," +
                "\"id\":\"bCqvfPR02Im\"," +
                "\"displayName\":\"Pi\"," +
                "\"externalAccess\":false," +
                "\"value\":3.14," +
                "\"access\":{" +
                "\"read\":true," +
                "\"updateWithSection\":false," +
                "\"externalize\":false," +
                "\"delete\":false," +
                "\"write\":false," +
                "\"manage\":false}," +
                "\"userGroupAccesses\":[]," +
                "\"attributeValues\":[]," +
                "\"translations\":[]" +
                "}", Constant.class);

        // we need to make sure that jackson is parsing dates in correct way
        assertThat(constant.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-03-11T16:39:33.083"));
        assertThat(constant.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-03-11T16:39:33.083"));

        // check if all properties are present and correspond to values in payload
        assertThat(constant.name()).isEqualTo("Pi");
        assertThat(constant.displayName()).isEqualTo("Pi");
        assertThat(constant.value()).isEqualTo(3.14);
        assertThat(constant.uid()).isEqualTo("bCqvfPR02Im");
    }
}
