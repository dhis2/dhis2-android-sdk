# Analytics { #android_sdk_analytics }

Analytics module offers some kind of local analytic data, i.e, some analytic values based on the data stored in the device. Currently, there is no integration with server-analytics although they share basic concepts, like visualizations, relative periods, relative orgunits, etc.

## Aggregated analytics { #android_sdk_aggregated_analytics }

They show analytic values in an aggregated fashion. Values might come from aggregated data or tracker data. If you are familiar with web analytic tools, this module is very similar to Data Visualizer (tables, charts, ...).

This module follows similar concepts to analytics endpoint in the web API. They are two basic parameters that must provide to the analytics engine to perform a evaluation: dimensions and filters.

For example, a basic query that get the number of "ANC 1st visit" (DataElement "fbfJHSPpUQD") in the last 3 months (RelativePeriod) and filtered by a the orgunit "Ngelehun CHC" (Absolute OrganisationUnit "DiszpKrYNg8") would be like this:

```java
d2.analyticsModule().analytics()
        .withDimension(new DimensionItem.DataItem.DataElementItem("fbfJHSPpUQD"))
        .withDimension(new DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS))
        .withFilter(new DimensionItem.OrganisationUnitItem.Absolute("DiszpKrYNg8"))
        .evaluate();
```

The engine will return a `Result` object containing either a `DimensionalResponse` or an `AnalyticsException`. Let's take a look at the structure of the `DimensionResponse`. We use the Kotlin representation for convenience, but the Java representation would be very similar:

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
                DimensionItem.DataItem.DataElementItem("fbfJHSPpUQD")
            ),
            Dimension.Period to listOf(
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS)
            ),
            Dimension.OrganisationUnit to listOf(
                DimensionItem.OrganisationUnitItem.Absolute("DiszpKrYNg8")
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

DimensionItems can be used either as dimensions or as filters. And multiple items of the same dimension can be combined in the same query. For example, this query gets "ANC 1st visit" (DataElement "fbfJHSPpUQD") and "ANC 1-3 Dropout Rate" (Indicator "ReUHfIn0pTQ") disaggregated by the category "Location: Fixed/Outreach" (Category "fMZEcRHuamy") using the options "Fixed" (CategoryOption "qkPbeWaFsnU") and "Outreach" (CategoryOption "wbrDrL2aYEc") within the last 3 months (Relative Period) in the UserOrganisationUnit (Relative OrganisationUnit).

```java
d2.analyticsModule().analytics()
        .withDimension(new DimensionItem.DataItem.DataElementItem("fbfJHSPpUQD"))
        .withDimension(new DimensionItem.DataItem.IndicatorItem("ReUHfIn0pTQ"))
        .withDimension(new DimensionItem.CategoryItem("fMZEcRHuamy", "qkPbeWaFsnU"))
        .withDimension(new DimensionItem.CategoryItem("fMZEcRHuamy", "wbrDrL2aYEc"))
        
        .withFilter(new DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS))
        .withFilter(new DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT))
        
        .evaluate();
```

The evaluator imposes some restrictions to the parameters passed as dimensions or filters (they are similar to those imposed by the analtyics web api):

1. At least one dimension item must be included as dimension property.
2. At least one **data** dimension item must be included either as a dimension or as a filter.


### Indicator support

### Program indicator support

## Visualization analytics { #android_sdk_visualization_analytics }

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
