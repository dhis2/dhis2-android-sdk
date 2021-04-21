# Analytics { #android_sdk_analytics }

Analytics module offers some kind of local analytic data, i.e, some analytic values based on the data stored in the device. Currently, there is no integration with server-analytics although they share basic concepts.

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
