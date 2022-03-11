# Program indicator engine { #android_sdk_program_indicator_engine }

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment or an event (single or tracker) and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```java
d2.programModule()
    .programIndicatorEngine()
    .getEnrollmentProgramIndicatorValue(<enrollment-uid>, <program-indicator-uid>);

d2.programModule()
    .programIndicatorEngine()
    .getEventProgramIndicatorValue(<event-uid>, <program-indicator-uid>);
```

If the evaluation of the "filter" component returns false, the result is null.


Table: Compatibility

| Type                      | Element               | Web       | Android SDK   |
|---------------------------|-----------------------|-----------|---------------|
|**Mathematical:**          |Parenthesis            | Yes       | Yes           |     
|                           |Plus (+)               | Yes       | Yes           |
|                           |Minus (-)              | Yes       | Yes           |
|                           |Power (^)              | Yes       | Yes           |
|                           |Multiply (*)           | Yes       | Yes           |
|                           |Divide (/)             | Yes       | Yes           |
|                           |Modulus (%)            | Yes       | Yes           |
|**Logical:**               |NOT                    | Yes       | Yes           |
|                           |!                      | Yes       | Yes           |
|                           |AND                    | Yes       | Yes           |
|                           |&&                     | Yes       | Yes           |
|                           |OR                     | Yes       | Yes           |
|                           |&#124;&#124;           | Yes       | Yes           |
|**Comparison:**            |Equal (==)             | Yes       | Yes           |
|                           |NotEqual (!=)          | Yes       | Yes           |
|                           |GT (>)                 | Yes       | Yes           |
|                           |LT (<)                 | Yes       | Yes           |
|                           |GE (>=)                | Yes       | Yes           |
|                           |LE (<=)                | Yes       | Yes           |
|**Functions:**             |FirstNonNull           | Yes       | Yes           |
|                           |Greatest               | Yes       | Yes           |
|                           |If                     | Yes       | Yes           |
|                           |IsNotNull              | Yes       | Yes           |
|                           |IsNull                 | Yes       | Yes           |
|                           |Least                  | Yes       | Yes           |
|                           |Log                    | Yes       | Yes           |
|                           |Log10                  | Yes       | Yes           |
|                           |PeriodOffset           | -         | No            |
|**D2 functions:**          |D2AddDays              | Yes       | Yes           |
|                           |D2Ceil                 | Yes       | Yes           |
|                           |D2Concatenate          | Yes       | Yes           |
|                           |D2Condition            | Yes       | Yes           |
|                           |D2Count                | Yes       | Yes           |
|                           |D2CountIfCondition     | Yes       | Yes           |
|                           |D2CountIfValue         | Yes       | Yes           |
|                           |D2DaysBetween          | Yes       | Yes           |
|                           |D2Floor                | Yes       | Yes           |
|                           |D2HasValue             | Yes       | Yes           |
|                           |D2Left                 | Yes       | Yes           |
|                           |D2Length               | Yes       | Yes           |
|                           |D2MaxValue             | No        | No            |
|                           |D2MinutesBetween       | Yes       | Yes           |
|                           |D2MinValue             | No        | No            |
|                           |D2Modulus              | Yes       | Yes           |
|                           |D2MonthsBetween        | Yes       | Yes           |
|                           |D2Oizp                 | Yes       | Yes           |
|                           |D2RelationshipCount    | Yes       | Yes           |
|                           |D2Right                | Yes       | Yes           |
|                           |D2Round                | Yes       | Yes           |
|                           |D2Split                | Yes       | Yes           |
|                           |D2Substring            | Yes       | Yes           |
|                           |D2ValidatePattern      | Yes       | Yes           |
|                           |D2WeeksBetween         | Yes       | Yes           |
|                           |D2YearsBetween         | Yes       | Yes           |
|                           |D2Zing                 | Yes       | Yes           |
|                           |D2Zpvc                 | Yes       | Yes           |
|                           |D2LastEventDate        | Yes       | No            |
|                           |D2AddControlDigits     | Yes       | No            |
|                           |D2CheckControlDigits   | Yes       | No            |
|                           |D2ZScoreWFA            | Yes       | No            |
|                           |D2ZScoreWFH            | Yes       | No            |
|                           |D2ZScoreHFA            | Yes       | No            |
|                           |D2InOrgUnitGroup       | Yes       | No            |
|                           |D2HasUserRole          | Yes       | No            |
|**Variables:**             |AnalyticsPeriodEnd     | No        | No            |
|                           |AnalyticsPeriodStart   | No        | No            |
|                           |CreationDate           | No        | Yes           |
|                           |CurrentDate            | Yes       | Yes           |
|                           |CompletedDate          | No        | Yes           |
|                           |DueDate                | Yes       | Yes           |
|                           |EnrollmentCount        | Yes       | Yes           |
|                           |EnrollmentDate         | Yes       | Yes           |
|                           |EnrollmentStatus       | No        | Yes           |
|                           |EventStatus            | Yes       | Yes           |
|                           |EventCount             | Yes       | Yes           |
|                           |ExecutionDate          | Yes       | Yes           |
|                           |EventDate              | Yes       | Yes           |
|                           |IncidentDate           | Yes       | Yes           |
|                           |OrgunitCount           | No        | No            |
|                           |ProgramStageId         | Yes       | Yes           |
|                           |ProgramStageName       | Yes       | Yes           |
|                           |SyncDate               | No        | No            |
|                           |TeiCount               | Yes       | Yes           |
|                           |ValueCount             | Yes       | Yes           |
|                           |ZeroPosValueCount      | Yes       | Yes           |
|**Other:**                 |Constant               | Yes       | Yes           |
|                           |ProgramStageElement    | Yes       | Yes           |
|                           |ProgramAttribute       | Yes       | Yes           |
|                           |PS_EVENTDATE           | Yes       | Yes           |
