package org.hisp.dhis.rules.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class RuleActionScheduleMessageShould {

    @Test
    public void substitute_empty_strings_when_create_with_null_arguments() {
        RuleActionScheduleMessage ruleActionScheduleMessage = RuleActionScheduleMessage
                .create("notification", "data");
        RuleActionScheduleMessage ruleActionScheduleMessageNoData = RuleActionScheduleMessage
                .create("notification", null);
        RuleActionScheduleMessage ruleActionScheduleMessageNoNotification = RuleActionScheduleMessage
                .create(null, "data");

        assertThat(ruleActionScheduleMessage.notification()).isEqualTo("notification");
        assertThat(ruleActionScheduleMessage.data()).isEqualTo("data");

        assertThat(ruleActionScheduleMessageNoData.notification()).isEqualTo("notification");
        assertThat(ruleActionScheduleMessageNoData.data()).isEqualTo("");

        assertThat(ruleActionScheduleMessageNoNotification.notification()).isEqualTo("");
        assertThat(ruleActionScheduleMessageNoNotification.data()).isEqualTo("data");
    }

}
