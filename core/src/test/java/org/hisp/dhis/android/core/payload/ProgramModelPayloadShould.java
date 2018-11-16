package org.hisp.dhis.android.core.payload;

import com.fasterxml.jackson.core.type.TypeReference;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.program.Program;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramModelPayloadShould extends BaseObjectShould implements ObjectShould {

    public ProgramModelPayloadShould() {
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
        assertThat(program.programStages()).isNotNull();
        assertThat(program.programStages()).isNotEmpty();

        Program program1 = programs.get(1);
        assertThat(program1.uid()).isEqualTo("q04UBOqq3rp");
        assertThat(program1.version()).isEqualTo(1);
        assertThat(program1.programStages()).isNotNull();
        assertThat(program1.programStages()).isNotEmpty();

        ObjectWithUid programStage1 = program1.programStages().get(0);
        assertThat(programStage1.uid()).isEqualTo("pSllsjpfLH2");
    }
}
