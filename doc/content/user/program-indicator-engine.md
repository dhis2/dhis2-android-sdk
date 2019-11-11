## Program indicator engine

<!--DHIS2-SECTION-ID:program_indicator_engine-->

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```java
d2.programModule()
    .programIndicatorEngine
    .getProgramIndicatorValue(<enrollment-uid>, <event-uid>, <program-indicator-uid>);
```

Compatibility table:

| Function (d2:)(doc)| Supported |
|--------------------|-----------|
| ceil              |   Yes     |
| floor             |   Yes     |
| round             |   Yes     |
| modulus           |   Yes     |
| zing              |   Yes     |
| oizp              |   Yes     |
| concatenate       |   Yes     |
| condition         |   Yes     |
| minutesBetween    |   No      |
| daysBetween       |   Yes     |
| monthsBetween     |   Yes     |
| yearsBetween      |   Yes     |
| relationshipCount |   No      |
| count             |   No      |
| countIfValue      |   No      |
| countIfZeroPos    |   No doc  |
| hasValue          |   No      |
| zpvc              |   Yes     |
| validatePatten    |   Yes     |
| left              |   Yes     |
| right             |   Yes     |
| substring         |   Yes     |
| split             |   Yes     |
| length            |   Yes     |
| inOrgUnitGroup    |   No doc  |
| hasUserRole       |   No doc  |

| Variables (doc)       | Supported |
|-----------------------|-----------|
| current_date          | Yes       |
| event_date            | Yes       |
| due_date              | Yes       |
| event_count           | Yes       |
| enrollment_date       | Yes       |
| incident_date         | Yes       |
| program_stage_id      | No        |
| program_stage_name    | No        |
| enrollment_status     | Yes       |
| value_count           | Yes       |
| zero_pos_value_count  | Yes       |
| reporting_period_start| N/A       |
| reporting_period_end  | N/A       |
| tei_count             | N/A       |
| enrollment_count      | N/A       |
| organisationunit_count| N/A       |
