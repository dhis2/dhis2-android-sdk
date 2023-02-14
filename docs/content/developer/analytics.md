# Analytics { #android_sdk_analytics }

Analytics module offers some kind of local analytic data, i.e, some analytic values based on the data stored in the device. Currently, there is no integration with server-analytics although they share basic concepts, like visualizations, relative periods, relative orgunits, etc.

## Aggregated analytics { #android_sdk_aggregated_analytics }

They show analytic values in an aggregated fashion. Values might come from aggregated data or tracker data. If you are familiar with web analytic tools, this module is very similar to Data Visualizer (tables, charts, ...).

### Raw analytics { #android_sdk_raw_analytics }

This module follows similar concepts to analytics endpoint in the web API. They are two basic parameters that must provide to the analytics engine to perform a evaluation: dimensions and filters.

For example, a basic query that get the number of "ANC 1st visit" (DataElement "fbfJHSPpUQD") in the last 3 months (RelativePeriod) and filtered by a the orgunit "Ngelehun CHC" (Absolute OrganisationUnit "DiszpKrYNg8") would be like this:

```java
d2.analyticsModule().analytics()
        .withDimension(new DimensionItem.DataItem.DataElementItem("fbfJHSPpUQD"))
        .withDimension(new DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS))
        .withFilter(new DimensionItem.OrganisationUnitItem.Absolute("DiszpKrYNg8"))
        .evaluate();
```

The engine will return a `Result` object containing either a `DimensionalResponse` or an `AnalyticsException`. Let's take a look at the structure of the `DimensionalResponse`. We use the Kotlin representation for convenience, but the Java representation would be very similar:

```kt
DimensionalResponse(
        metadata = mapOf(
            "fbfJHSPpUQD" to MetadataItem.DataElementItem,
            "DiszpKrYNg8" to MetadataItem.OrganisationUnitItem,
            "202108" to MetadataItem.PeriodItem,
            "202109" to MetadataItem.PeriodItem,
            "202110" to MetadataItem.PeriodItem
        ),
        dimensions = listOf(
            Dimension.Data, 
            Dimension.Period
        ),
        dimensionItems = mapOf(
            Dimension.Data to listOf(
                DimensionItem.DataItem.DataElementItem(uid=fbfJHSPpUQD)
            ),
            Dimension.Period to listOf(
                DimensionItem.PeriodItem.Relative(relative=RelativePeriod.LAST_3_MONTHS)
            ),
            Dimension.OrganisationUnit to listOf(
                DimensionItem.OrganisationUnitItem.Absolute(uid=DiszpKrYNg8)
            )
        ),
        filters = listOf(
            "DiszpKrYNg8"
        ),
        values = listOf(
            DimensionalValue(
                dimensions = listOf("fbfJHSPpUQD", "202108"),
                value = "17"
            ),
            DimensionalValue(
                dimensions = listOf("fbfJHSPpUQD", "202109"),
                value = "112"
            ),
            DimensionalValue(
                dimensions = listOf("fbfJHSPpUQD", "202110"),
                value = "20"
            )
        )
    )
```

Properties included in the `DimensionalResponse` object:

- **Metadata**: it contains a map of ids to `MetadataItem`. The ids are strings that identify dataElements, periods (relative or absolute), orgunits, etc. Some examples of ids are "fbfJHSPpUQD", "202108", "LAST_3_MONTHS", "USER_ORGUNIT",... . The purpose of this map is to quickly obtain a printable representation from any id used in the other properties (dimensionItems, filters or values). The `MetadataItem` interface contains two basic properties, `id` and `displayName` and it can be easily matched to the underlying class to get more information about the DataElement, Indicator, Period, etc.
- **Dimensions**: ordered list of dimensions in which the values are disaggregated. In the example above, the first dimension is `Data`, which means that the first dimension in the `dimensions` property in the values belongs to the `Data` dimension. And the second one is Period.
- **DimensionItems**: it contains a map of items grouped by dimension type. It contains the items originally used to build the query: in the example above, the period was relative, so the Period dimension include the relative period `LAST_3_MONTHS`.
- **Filters**: list of ids that act as a filter in the query.
- **Values**: list of `DimensionalValue`. Each value contains an ordered list of ids that define the value. In the example above, the value "17" corresponds to "ANC 1st visit" ("fbfJHSPpUQD") and "August 2021" ("202108"). You can use the metadata map to get more information from those ids.

DimensionItems can be used either as dimensions or as filters. And multiple items of the same dimension can be combined in the same query. For example, this query gets "ANC 1st visit" (DataElement "fbfJHSPpUQD") and "ANC 1-3 Dropout Rate" (Indicator "ReUHfIn0pTQ") disaggregated by the category "Location: Fixed/Outreach" (Category "fMZEcRHuamy") using the options "Fixed" (CategoryOption "qkPbeWaFsnU") and "Outreach" (CategoryOption "wbrDrL2aYEc") within the last 3 months (Relative Period) in the UserOrganisationUnit (Relative OrganisationUnit), classifying the values by data item legendSet and overriding the aggregation type:

```java
d2.analyticsModule().analytics()
        .withDimension(new DimensionItem.DataItem.DataElementItem("fbfJHSPpUQD"))
        .withDimension(new DimensionItem.DataItem.IndicatorItem("ReUHfIn0pTQ"))
        .withDimension(new DimensionItem.CategoryItem("fMZEcRHuamy", "qkPbeWaFsnU"))
        .withDimension(new DimensionItem.CategoryItem("fMZEcRHuamy", "wbrDrL2aYEc"))
        
        .withFilter(new DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS))
        .withFilter(new DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT))

        .withLegendStrategy(AnalyticsLegendStrategy.ByDataItem.INSTANCE)
        
        .withAggregationType(AggregationType.LAST)
        
        .evaluate();
```

The evaluator imposes some restrictions to the parameters passed as dimensions or filters (they are similar to those imposed by the analtyics web api):

1. At least one dimension item must be included as dimension property.
2. At least one **data** dimension item must be included either as a dimension or as a filter.

Additionally, the query is evaluated against the local metadata and data, which imposes additional restrictions:

1. DimensionItems (DataElement, Indicator, OrganisationUnit, ...) must be downloaded in the device. By default, the SDK downloads all the dataSets and programs accessible to the user and the related metadata.
2. Data must be downloaded in the device. The evaluation only takes into account the data stored in the local database.

There are three options to define the legendSet strategy. The class `AnalyticsLegendStrategy` is a sealed class in Kotlin, so the keyword `INSTANCE` must be appended at the end of the object values when coding in Java. Code examples:

```java
d2.analyticsModule().analytics()
        .withLegendStrategy(AnalyticsLegendStrategy.ByDataItem.INSTANCE)       // Data items use their own LegendSet
        .withLegendStrategy(AnalyticsLegendStrategy.None.INSTANCE)             // LegendSets are not used
        .withLegendStrategy(new AnalyticsLegendStrategy.Fixed("fqs276KXCXi"))  // The provided LegendSet will be used for all data items
```

### Visualization analytics { #android_sdk_visualization_analytics }

The visualization analtyics engine offers handy methods to evaluate Visualization objects. This engine is implemented on top of the raw analtyics engine and it basically fulfill two objectives:

1. To translate a Visualization object into a query to Analtyics engine.
2. To return a formatted result following the rows, columns and filters defined in the Visualization.

In order to use the Visualization objects, they must be set in the "Analytics" section of the Android Settings webapp. Currently, it is not possible to download on-demand visualizations from the server, just to downloaded through the Android Settings webapp.

As an example, let's suppose we have a Visualization with the following parameters:

- Visualization ("SwtkWZFhrFQ").
- Columns:
    - Data: two DataElements (ANC 1st Visit "fbfJHSPpUQD", ANC 2nd Visit "cYeuwXTCPkU").
    - Category: Location Fixed/Outreach ("fMZEcRHuamy").
- Rows:
    - Period: LAST_3_MONTHS.
- Filter:
    - Organisation Unit: Badja ("YuQRtpLP10I").

The expected representation of the visualization would be something like this:

![](resources/images/analytics_visualization.png){ width=50% }

We can get the result of the visualization by calling the "visualizations" repository within the analtyics module. Optionally, we can override the values for Period and OrganisationUnit. This is useful to expose filters in the UI to allow easy modifications of the results.

```java
d2.analyticsModule().visualizations()
        .withVisualization("SwtkWZFhrFQ")
        [.withPeriods()]
        [.withOrganisationUnits()]
        .evaluate();
```

The method will return a `Result` with two possible values: a `GridAnalyticsResponse` and an `AnalyticsResponse`. Let's take a look at the structure of the `GridAnalyticsResponse`. We use the Kotlin representation for convenience:

```kt
GridAnalyticsResponse(
        metadata = mapOf(
            "fbfJHSPpUQD" to MetadataItem.DataElementItem,
            "cYeuwXTCPkU" to MetadataItem.DataElementItem,
            "fMZEcRHuamy" to MetadataItem.CategoryItem,
            "qkPbeWaFsnU" to MetadataItem.CategoryOptionItem,
            "wbrDrL2aYEc" to MetadataItem.CategoryOptionItem,
            "YuQRtpLP10I" to MetadataItem.OrganisationUnitItem,
            "202108" to MetadataItem.PeriodItem,
            "202109" to MetadataItem.PeriodItem,
            "202110" to MetadataItem.PeriodItem
        ),
        headers = GridHeader(
            columns = listOf(
                listOf(
                    GridHeaderItem(id=fbfJHSPpUQD, weight=2),
                    GridHeaderItem(id=cYeuwXTCPkU, weight=2)
                ),
                listOf(
                    GridHeaderItem(id=qkPbeWaFsnU, weight=1),
                    GridHeaderItem(id=wbrDrL2aYEc, weight=1),
                    GridHeaderItem(id=qkPbeWaFsnU, weight=1),
                    GridHeaderItem(id=wbrDrL2aYEc, weight=1)
                )
            ),
            rows = listOf(
                listOf(
                    GridHeaderItem(id=202108, weight=1),
                    GridHeaderItem(id=202109, weight=1),
                    GridHeaderItem(id=202110, weight=1)
                )
            )
        ),
        dimensions = GridDimension(
            columns = listOf(
                Dimension.Data,
                Dimension.Category(uid=fMZEcRHuamy)
            ),
            rows = listOf(
                Dimension.Period
            )
        ),
        dimensionItems = mapOf(
            Dimension.Data to listOf(
                DimensionItem.DataItem.DataElementItem(uid=fbfJHSPpUQD),
                DimensionItem.DataItem.DataElementItem(uid=cYeuwXTCPkU)
            ),
            Dimension.Period to listOf(
                DimensionItem.PeriodItem.Relative(relative=LAST_3_MONTHS)
            ),
            Dimension.Category(uid=fMZEcRHuamy) to listOf(
                DimensionItem.CategoryItem(uid=fMZEcRHuamy, categoryOption=qkPbeWaFsnU),
                DimensionItem.CategoryItem(uid=fMZEcRHuamy, categoryOption=wbrDrL2aYEc)
            ),
            Dimension.OrganisationUnit to listOf(
                DimensionItem.OrganisationUnitItem.Absolute(uid=YuQRtpLP10I)
            )
        ),
        filters = listOf(
            "YuQRtpLP10I"
        ),
        values = listOf(
            listOf(
                GridResponseValue(
                    columns=[fbfJHSPpUQD, qkPbeWaFsnU], 
                    rows=[202108], 
                    value=23
                ),
                GridResponseValue(
                    columns=[fbfJHSPpUQD, wbrDrL2aYEc], 
                    rows=[202108], 
                    value=3
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, qkPbeWaFsnU], 
                    rows=[202108], 
                    value=46
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, wbrDrL2aYEc], 
                    rows=[202108], 
                    value=3
                )
            ),
            listOf(
                GridResponseValue(
                    columns=[fbfJHSPpUQD, qkPbeWaFsnU], 
                    rows=[202109], 
                    value=21
                ),
                GridResponseValue(
                    columns=[fbfJHSPpUQD, wbrDrL2aYEc], 
                    rows=[202109], 
                    value=10
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, qkPbeWaFsnU], 
                    rows=[202109], 
                    value=23
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, wbrDrL2aYEc], 
                    rows=[202109], 
                    value=8
                )
            ),
            listOf(
                GridResponseValue(
                    columns=[fbfJHSPpUQD, qkPbeWaFsnU], 
                    rows=[202110], 
                    value=24
                ),
                GridResponseValue(
                    columns=[fbfJHSPpUQD, wbrDrL2aYEc], 
                    rows=[202110], 
                    value=1
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, qkPbeWaFsnU], 
                    rows=[202110], 
                    value=47
                ),
                GridResponseValue(
                    columns=[cYeuwXTCPkU, wbrDrL2aYEc], 
                    rows=[202110], 
                    value=2
                )
            )
        )
)
```

As we can see, the `GridDimensionalResponse` is very similar to the `DimensionalResponse` with additional information about the columns and rows defined in the visualization.

Properties included in the `GridDimensionalResponse` object:

- **Metadata**: it has same format and follows the purpose than in `DimensionalResponse`.
- **Headers**: it defines the structure of the headers in the table. Each entry in columns/rows represents a dimension. The weight represents how many columns/rows it applies to. In the example, the first Dimension in the columns (dataElements) has weight 2, whereas the second dimension (categoryOptions) has weight 1. It can be easily understood looking at the table.
- **Dimensions**: it has same purpose than in `DimensionalResponse`, but dimensions are grouped by columns and rows.
- **DimensionItems**: it has same format and follows the purpose than in `DimensionalResponse`.
- **Filters**: it has same format and follows the purpose than in `DimensionalResponse`.
- **Values**: it includes additional information about the representation of the values. Each entry in the "values" array represents a row in the table. This entry is a list of `GridResponseValue`, which represents a cell in the table. Each `GridResponseValue` contains context information about the columns and rows the value belongs to. This format makes easy to print the result in a tabular format without further processing.

### Dimension support { #android_sdk_analytics_dimension_support }

| Dimension                 | Element                           | Support   |
|---------------------------|-----------------------------------|-----------|
| **Data:**                 | Indicators                        | Yes*      |
|                           | DataElements                      | Yes       |
|                           | DataElements details              | Yes       |
|                           | Event data items                  | Yes       |
|                           | Program indicators                | Yes**     |
|                           | DataSets: Reporting rate          | No        |
|                           | DataSets: Reporting rate on time  | No        |
|                           | DataSets: Actual reports          | No        |
|                           | DataSets: Actual reports on time  | No        |
|                           | DataSets: Expected reports        | No        |
| **Period:**               | Fixed                             | Yes       |
|                           | Relative                          | Yes       |
| **Organisation Unit:**    | Fixed                             | Yes       |
|                           | Relative                          | Yes       |
|                           | Level                             | Yes       |
|                           | OU Groups                         | Yes       |
| **Other:**                | Categories (CategoryOptions)      | Yes***    |
|                           | Category Option Group Sets        | No        |
|                           | Organisation Unit Group Sets      | No        |

*Check the table [Indicator support](#android_sdk_analytics_indicator_support) for details.

**Check the table [Program indicator support](#android_sdk_analytics_program_indicator_support) for details.

***In the case of ProgramIndicators, they are only applied in EVENT ProgramIndicators.

### Indicator support { #android_sdk_analytics_indicator_support }

This table shows the functionality supported by the Indicator dimension item compared to the backend analytics.

| Type              | Element               | Backend   | Android SDK   |
|----------         |---------------------- |-----------|---------------|
|**Mathematical:**  |Parenthesis            | Yes       | Yes           |     
|                   |Plus (+)               | Yes       | Yes           |
|                   |Minus (-)              | Yes       | Yes           |
|                   |Power (^)              | Yes       | No            |
|                   |Multiply (*)           | Yes       | Yes           |
|                   |Divide (/)             | Yes       | Yes           |
|                   |Modulus (%)            | Yes       | Yes           |
|**Logical:**       |NOT                    | Yes       | Yes           |
|                   |!                      | Yes       | Yes           |
|                   |AND                    | Yes       | Yes           |
|                   |&&                     | Yes       | Yes           |
|                   |OR                     | Yes       | Yes           |
|                   |&#124;&#124;           | Yes       | Yes           |
|**Comparison:**    |Equal (==)             | Yes       | Yes           |
|                   |NotEqual (!=)          | Yes       | Yes           |
|                   |GT (>)                 | Yes       | Yes           |
|                   |LT (<)                 | Yes       | Yes           |
|                   |GE (>=)                | Yes       | Yes           |
|                   |LE (<=)                | Yes       | Yes           |
|**Functions:**     |FirstNonNull           | Yes       | Yes           |
|                   |Greatest               | Yes       | Yes           |
|                   |If                     | Yes       | Yes           |
|                   |IsNotNull              | Yes       | Yes           |
|                   |IsNull                 | Yes       | Yes           |
|                   |Least                  | Yes       | Yes           |
|                   |Log                    | Yes       | No            |
|                   |Log10                  | Yes       | No            |
|                   |.aggregationType       | Yes       | No            |
|                   |.maxDate               | Yes       | No            |
|                   |.minDate               | Yes       | No            |
|                   |.periodOffset          | Yes       | No            |
|**Dimensions:**    |Constant               | Yes       | Yes           |
|                   |DataElement            | Yes       | Yes           |
|                   |ProgramAttribute       | Yes       | Yes           |
|                   |ProgramDataElement     | Yes       | Yes           |
|                   |ProgramIndicator       | Yes       | Yes           |
|                   |OrgUnitGroup           | Yes       | No            |
|                   |ReportingRate          | Yes       | No            |
|                   |Days                   | Yes       | Yes           |
|                   |N_Brace (indicators)   | Yes       | No            |

### Program indicator support { #android_sdk_analytics_program_indicator_support }

This table shows the functionality supported by the ProgramIndicator dimension item compared to the backend analytics.

| Type                      | Element               | Backend   | Android SDK   |
|---------------------------|-----------------------|-----------|---------------|
|**Mathematical:**          |Parenthesis            | Yes       | Yes           |     
|                           |Plus (+)               | Yes       | Yes           |
|                           |Minus (-)              | Yes       | Yes           |
|                           |Power (^)              | Yes       | No            |
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
|                           |Log                    | Yes       | No            |
|                           |Log10                  | Yes       | No            |
|                           |PeriodOffset           | Yes       | No            |
|**D2 functions:**          |D2AddDays              | No        | No            |
|                           |D2Ceil                 | No        | No            |
|                           |D2Concatenate          | No        | No            |
|                           |D2Condition            | Yes       | Yes           |
|                           |D2Count                | Yes       | Yes           |
|                           |D2CountIfCondition     | Yes       | Yes           |
|                           |D2CountIfValue         | Yes       | Yes           |
|                           |D2DaysBetween          | Yes       | Yes           |
|                           |D2Floor                | No        | No            |
|                           |D2HasValue             | Yes       | Yes           |
|                           |D2Left                 | No        | No            |
|                           |D2Length               | No        | No            |
|                           |D2MaxValue             | Yes       | No            |
|                           |D2MinutesBetween       | Yes       | Yes           |
|                           |D2MinValue             | Yes       | No            |
|                           |D2Modulus              | No        | No            |
|                           |D2MonthsBetween        | Yes       | Yes           |
|                           |D2Oizp                 | Yes       | Yes           |
|                           |D2RelationshipCount    | Yes       | Yes           |
|                           |D2Right                | No        | No            |
|                           |D2Round                | No        | No            |
|                           |D2Split                | No        | No            |
|                           |D2Substring            | No        | No            |
|                           |D2ValidatePattern      | No        | No            |
|                           |D2WeeksBetween         | Yes       | Yes           |
|                           |D2YearsBetween         | Yes       | Yes           |
|                           |D2Zing                 | Yes       | Yes           |
|                           |D2Zpvc                 | Yes       | Yes           |
|                           |D2LastEventDate        | No        | No            |
|                           |D2AddControlDigits     | No        | No            |
|                           |D2CheckControlDigits   | No        | No            |
|                           |D2ZScoreWFA            | No        | No            |
|                           |D2ZScoreWFH            | No        | No            |
|                           |D2ZScoreHFA            | No        | No            |
|                           |D2InOrgUnitGroup       | No        | No            |
|                           |D2HasUserRole          | No        | No            |
|**Aggregation functions:** |avg                    | Yes       | No            |
|                           |count                  | Yes       | No            |
|                           |max                    | Yes       | No            |
|                           |min                    | Yes       | No            |
|                           |percentileCont         | Yes       | No            |
|                           |stddev                 | Yes       | No            |
|                           |stddevPop              | Yes       | No            |
|                           |stddevSamp             | Yes       | No            |
|                           |sum                    | Yes       | No            |
|                           |variance               | Yes       | No            |
|**Variables:**             |AnalyticsPeriodEnd     | Yes       | Yes           |
|                           |AnalyticsPeriodStart   | Yes       | Yes           |
|                           |CreationDate           | Yes       | Yes           |
|                           |CurrentDate            | Yes       | Yes           |
|                           |CompletedDate          | Yes       | Yes           |
|                           |DueDate                | Yes       | Yes           |
|                           |EnrollmentCount        | Yes       | Yes           |
|                           |EnrollmentDate         | Yes       | Yes           |
|                           |EnrollmentStatus       | Yes       | Yes           |
|                           |EventStatus            | Yes       | Yes           |
|                           |EventCount             | Yes       | Yes           |
|                           |ExecutionDate          | Yes       | Yes           |
|                           |EventDate              | Yes       | Yes           |
|                           |IncidentDate           | Yes       | Yes           |
|                           |OrgunitCount           | Yes       | Yes           |
|                           |ProgramStageId         | Yes       | Yes           |
|                           |ProgramStageName       | Yes       | Yes           |
|                           |SyncDate               | Yes       | Yes           |
|                           |TeiCount               | Yes       | Yes           |
|                           |ValueCount             | Yes       | Yes           |
|                           |ZeroPosValueCount      | Yes       | Yes           |
|**Other:**                 |Constant               | Yes       | Yes           |
|                           |ProgramStageElement    | Yes       | Yes           |
|                           |ProgramAttribute       | Yes       | Yes           |
|                           |PS_EVENTDATE           | Yes       | Yes           |

### Aggregation type support { #android_sdk_analytics_aggregation_type_support }

| Type                            | Android SDK |
|---------------------------------|-------------|
| SUM                             | Yes         |
| AVERAGE                         | Yes         |
| AVERAGE_SUM_ORG_UNIT            | Yes         |
| LAST                            | Yes         |
| LAST_AVERAGE_ORG_UNIT           | Yes         |
| LAST_IN_PERIOD                  | Yes         |
| LAST_IN_PERIOD_AVERAGE_ORG_UNIT | Yes         |
| FIRST                           | Yes         |
| FIRST_AVERAGE_ORG_UNIT          | Yes         |
| COUNT                           | Yes         |
| STDDEV                          | No          |
| VARIANCE                        | No          |
| MIN                             | Yes         |
| MAX                             | Yes         |
| NONE                            | No          |
| CUSTOM                          | No          |
| DEFAULT                         | No          |

## Event line list { #android_sdk_event_line_list }

They are event-based analytics. If you are familiar with web analytic tools, it is very similar to Event Reports (line list).

A common use-case it to generate an event line list of a repeatable stage in the context of a particular TEI in order to show the evolution of a particular value across the events. 

For example, let's suppose we have a repeatable stage with two dataElements (height and weight) and one indicator based on those values (BMI, Body Mass Index). We would like to show the evolution of those values across the events

```java
d2.analyticsModule().eventLineList()
        .byProgramStage().eq("stage_id")
        .byTrackedEntityInstance().eq("tei_id")
        .withDataElement("height_id")
        .withDataElement("weight_id")
        .withProgramIndicator("BMI_id")
        .evaluate();
```

The result would be a list of events with the evaluated values (dataelement and indicators) as well as some handy `displayName` properties to display the result in a table or chart.
