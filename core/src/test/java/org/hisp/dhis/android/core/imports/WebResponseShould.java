package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class WebResponseShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        String responseStr = new ResourcesFileReader().getStringFromFile("imports/web_response.json");
        WebResponse webResponse = objectMapper.readValue(responseStr, WebResponse.class);

        assertThat(webResponse.message()).isEqualTo("Import was successful.");
        assertThat(webResponse.importSummaries()).isNotNull();
    }

    @Test
    public void map_from_json_string_with_import_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        String webResponseStr = new ResourcesFileReader().getStringFromFile(
                "imports/web_response_with_import_conflicts.json");
        objectMapper.readValue(webResponseStr, WebResponse.class);
    }
}
