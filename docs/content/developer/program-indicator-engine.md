# Program indicator engine

<!--DHIS2-SECTION-ID:program_indicator_engine-->

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment or a single event and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```java
d2.programModule()
    .programIndicatorEngine()
    .getProgramIndicatorValue(<enrollment-uid>, <event-uid>, <program-indicator-uid>);
```

Either enrollment-uid or event-uid must be non-null.

Compatibility table:

| Common functions  | Supported |
|-------------------|-----------|
| if                | Yes       |
| isNull            | Yes       |
| isNotNull         | Yes       |
| firstNonNull      | Yes       |
| greatest          | Yes       |
| least             | Yes       |

| Function (d2:)(doc)| Supported |
|--------------------|-----------|
| addDays           |   Yes     |
| ceil              |   Yes     |
| concatenate       |   Yes     |
| condition         |   Yes     |
| count             |   Yes     |
| countIfCondition  |   Yes     |
| countIfValue      |   Yes     |
| countIfZeroPos    |   No doc  |
| daysBetween       |   Yes     |
| floor             |   Yes     |
| hasUserRole       |   No doc  |
| hasValue          |   Yes     |
| inOrgUnitGroup    |   No doc  |
| left              |   Yes     |
| length            |   Yes     |
| minutesBetween    |   Yes     |
| modulus           |   Yes     |
| monthsBetween     |   Yes     |
| oizp              |   Yes     |
| relationshipCount |   No      |
| right             |   Yes     |
| round             |   Yes     |
| split             |   Yes     |
| substring         |   Yes     |
| validatePatten    |   Yes     |
| weeksBetween      |   Yes     |
| yearsBetween      |   Yes     |
| zing              |   Yes     |
| zpvc              |   Yes     |

| Variables (doc)       | Supported |
|-----------------------|-----------|
| completed_date        | Yes       |
| creation_date         | Yes       |
| current_date          | Yes       |
| due_date              | Yes       |
| enrollment_count      | Yes       |
| enrollment_date       | Yes       |
| enrollment_status     | Yes       |
| event_count           | Yes       |
| event_date            | Yes       |
| incident_date         | Yes       |
| organisationunit_count| N/A       |
| program_stage_id      | No        |
| program_stage_name    | No        |
| reporting_period_end  | N/A       |
| reporting_period_start| N/A       |
| sync_date             | No        |
| tei_count             | N/A       |
| value_count           | Yes       |
| zero_pos_value_count  | Yes       |

Other components:

| Component             | Supported |
|-----------------------|-----------|
| PS_EVENTDATE          | Yes       |
