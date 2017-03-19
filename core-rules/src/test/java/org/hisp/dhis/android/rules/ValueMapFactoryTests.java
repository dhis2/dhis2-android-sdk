package org.hisp.dhis.android.rules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ValueMapFactoryTests {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Mock
    private Event event;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        Date eventDate = dateFormat.parse("1994-02-03");
        when(event.eventDate()).thenReturn(eventDate);
    }

    @Test
    public void buildShouldReturnImmutableMap() {
        ValueMapFactory valueMapFactory = new ValueMapFactory(
                new ArrayList<ProgramRuleVariable>(),
                new ArrayList<Event>()
        );

        try {
            valueMapFactory.build(event).clear();
            fail("UnsupportedOperationException expected, but nothing was thrown");
        } catch (UnsupportedOperationException exception) {
            // noop
        }
    }

    @Test
    public void buildShouldReturnMapWithEnvVariables() throws ParseException {
        ValueMapFactory valueMapFactory = new ValueMapFactory(
                new ArrayList<ProgramRuleVariable>(),
                new ArrayList<Event>()
        );

        Map<String, ProgramRuleVariableValue> valueMap = valueMapFactory.build(event);

        assertThat(valueMap.get("event_date").hasValue()).isTrue();
        assertThat(valueMap.get("event_date").value()).isEqualTo("1994-02-03");

        // ToDo: inject current date through constructor in order to achieve testability?
        // assertThat(valueMap.get("current_date")).isEqualTo();
    }
}
