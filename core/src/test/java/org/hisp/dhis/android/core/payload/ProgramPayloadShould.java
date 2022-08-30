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

package org.hisp.dhis.android.core.payload;

import com.fasterxml.jackson.core.type.TypeReference;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.program.Program;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ProgramPayloadShould extends BaseObjectShould implements ObjectShould {

    public ProgramPayloadShould() {
        super("program/program_payload.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Payload<Program> payload = objectMapper.readValue(jsonStream, new TypeReference<Payload<Program>>() {});

        List<Program> programs = payload.items();

        assertThat(programs).isNotNull();
        assertThat(programs).isNotEmpty();
        assertThat(programs.size()).isEqualTo(2);

        Program program = programs.get(0);
        assertThat(program.uid()).isEqualTo("IpHINAT79UW");
        assertThat(program.version()).isEqualTo(3);
        assertThat(program.access()).isEqualTo(Access.create(true, false,
                DataAccess.create(false, false)));

        Program program1 = programs.get(1);
        assertThat(program1.uid()).isEqualTo("q04UBOqq3rp");
        assertThat(program1.version()).isEqualTo(1);
        assertThat(program1.access()).isEqualTo(Access.builder().build());
    }
}
