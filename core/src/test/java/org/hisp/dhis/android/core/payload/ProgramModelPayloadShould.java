package org.hisp.dhis.android.core.payload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramModelPayloadShould {
    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        Payload<Program> payload = objectMapper.readValue("{\n" +
                        "\n" +
                        "    \"programs\": [\n" +
                        "        {\n" +
                        "            \"id\": \"IpHINAT79UW\",\n" +
                        "            \"version\": 3,\n" +
                        "            \"programStages\": [\n" +
                        "                {\n" +
                        "                    \"id\": \"A03MvHHogjR\",\n" +
                        "                    \"programStageDataElements\": [\n" +
                        "                        {\n" +
                        "                            \"id\": \"LBNxoXdMnkv\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"a3kGcGDCuk6\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"yYMGxXpfl0Z\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"H6uSAMO5WLD\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"u2FvnCDCBcD\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"UXz7xuGCEhU\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"XLFc6tTftb5\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"wQLfBvPrXqq\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"f38bstJioPs\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"p8eX3rSkKN0\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"bx6fsa0t90x\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"O4dwFWakvGO\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"ebaJjqltK5N\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"kzgQRhOCadd\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"xtjAxBGQNNV\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"X8zyunlgUfM\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"x31y45jvIQL\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"JYyXbTmBBls\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"uf3svrmp8Oj\"\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"id\": \"ZzYYXq4fJie\",\n" +
                        "                    \"programStageDataElements\": [\n" +
                        "                        {\n" +
                        "                            \"id\": \"ztoQtbuXzsI\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"GQY2lXrypjO\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"vdc1saaN2ma\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"X8zyunlgUfM\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"x31y45jvIQL\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"Vpx18GqyLcK\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"FqlgKAG8HOu\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"WlYechRHVo3\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"vTUhAUZFoys\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"kzgQRhOCadd\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"WucAVPYvcEO\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"rxBfISxXS2U\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"EL5dr5x0WbZ\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"lNNb3truQoi\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"nH8Y04zS7UV\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"IpPWDRlHJSe\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"pOe0ogW4OWd\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"udkr3ihaeD3\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"xSTVGEIbarb\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"HLmTEmupdX0\"\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"YCO2FVT0wXL\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"cYGaxwK615G\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"oXR37f2wOb1\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"VlOvjLKnoyw\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"hDZbpskhqDd\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"OGmE3wUMEzu\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"rqmcdr07fxQ\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"sj3j9Hwc7so\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"dgsftM0rXu2\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"LfgZNmadu4W\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"aei1xRjSU2l\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"XdI8KRJiRoZ\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"sfYk4rKw18B\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"BeynU4L6VCQ\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"XdI8KRJiRoZ\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"LiV2YoatDud\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"OuJ6sgPyAbC\"\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"programTrackedEntityAttributes\": [\n" +
                        "                {\n" +
                        "                    \"id\": \"K6BlNQdIxd1\",\n" +
                        "                    \"trackedEntityAttribute\": {\n" +
                        "                        \"id\": \"w75KJ2mc4zz\"\n" +
                        "                    }\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"id\": \"oGv9KGJpqkl\",\n" +
                        "                    \"trackedEntityAttribute\": {\n" +
                        "                        \"id\": \"zDhUuAYrxNC\"\n" +
                        "                    }\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"id\": \"YhqgQ6Iy4c4\",\n" +
                        "                    \"trackedEntityAttribute\": {\n" +
                        "                        \"id\": \"cejWyOfXge6\",\n" +
                        "                        \"optionSet\": {\n" +
                        "                            \"id\": \"pC3N9N77UmT\"\n" +
                        "                        }\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": \"q04UBOqq3rp\",\n" +
                        "            \"version\": 1,\n" +
                        "            \"programStages\": [\n" +
                        "                {\n" +
                        "                    \"id\": \"pSllsjpfLH2\",\n" +
                        "                    \"programStageDataElements\": [\n" +
                        "                        {\n" +
                        "                            \"id\": \"apYCBRwEc44\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"f3Rn9XPEQuv\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"s3GoZHCmXL2\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"OOGft1qHHnN\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"Dv7iIitX44Y\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"VQ2lai3OfVG\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"ulVsVS36Zpm\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"UuL3eX8KJHY\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"PmE3Ev4ZcHX\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"o5M0cNMVKY3\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"deQEw93Vr4j\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"s3GoZHCmXL2\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"cLhp2K7tqAP\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"DvrjjquRrvF\",\n" +
                        "                                \"optionSet\": {\n" +
                        "                                    \"id\": \"Da7byW1wGzq\"\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"id\": \"BAZe68syer0\",\n" +
                        "                            \"dataElement\": {\n" +
                        "                                \"id\": \"lsJCUffec9h\"\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"programTrackedEntityAttributes\": [ ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "\n" +
                        "}",
                new TypeReference<Payload<Program>>() {
                });

        List<Program> programs = payload.items();

        assertThat(programs).isNotNull();
        assertThat(programs).isNotEmpty();
        assertThat(programs.size()).isEqualTo(2);

        Program program = programs.get(0);
        assertThat(program.uid()).isEqualTo("IpHINAT79UW");
        assertThat(program.version()).isEqualTo(3);
        assertThat(program.programStages()).isNotNull();
        assertThat(program.programStages()).isNotEmpty();

        ProgramStage programStage = program.programStages().get(0);
        assertThat(programStage.programStageDataElements()).isNotNull();
        assertThat(programStage.programStageDataElements()).isNotEmpty();

        ProgramStageDataElement programStageDataElement = programStage.programStageDataElements().get(0);
        assertThat(programStageDataElement.uid()).isEqualTo("LBNxoXdMnkv");
        DataElement dataElement = programStageDataElement.dataElement();
        assertThat(dataElement.uid()).isEqualTo("a3kGcGDCuk6");
        assertThat(dataElement.optionSet()).isNull();

        Program program1 = programs.get(1);
        assertThat(program1.uid()).isEqualTo("q04UBOqq3rp");
        assertThat(program1.version()).isEqualTo(1);
        assertThat(program1.programStages()).isNotNull();
        assertThat(program1.programStages()).isNotEmpty();

        ProgramStage programStage1 = program1.programStages().get(0);
        assertThat(programStage1.uid()).isEqualTo("pSllsjpfLH2");
        assertThat(programStage1.programStageDataElements()).isNotNull();
        assertThat(programStage1.programStageDataElements()).isNotEmpty();

        ProgramStageDataElement programStageDataElement1 = programStage1.programStageDataElements().get(0);
        assertThat(programStageDataElement1.uid()).isEqualTo("apYCBRwEc44");

        DataElement dataElement1 = programStageDataElement1.dataElement();
        assertThat(dataElement1.uid()).isEqualTo("f3Rn9XPEQuv");

        OptionSet optionSet1 = dataElement1.optionSet();
        assertThat(optionSet1.uid()).isEqualTo("s3GoZHCmXL2");

    }
}
