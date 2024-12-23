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

package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class OptionGroupShould extends BaseObjectShould implements ObjectShould {

    public OptionGroupShould() {
        super("option/option_group.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        OptionGroup optionGroup = objectMapper.readValue(jsonStream, OptionGroup.class);

        assertThat(optionGroup.uid()).isEqualTo("j3JYGVCIEdz");
        assertThat(optionGroup.name()).isEqualTo("Option group");
        assertThat(optionGroup.displayName()).isEqualTo("Option group");
        assertThat(optionGroup.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2019-02-15T13:55:55.665"));
        assertThat(optionGroup.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2019-02-15T13:55:55.665"));
        assertThat(optionGroup.optionSet().uid()).isEqualTo("VQ2lai3OfVG");
        assertThat(optionGroup.options().get(0).uid()).isEqualTo("Y1ILwhy5VDY");
        assertThat(optionGroup.options().get(1).uid()).isEqualTo("egT1YqFWsVk");
    }
}