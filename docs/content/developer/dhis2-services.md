# DHIS2 services { #android_sdk_dhis2_services }

## DHIS2 Business Logic

SDK repositories give access to metadata and allow to create and modify data, but they are lazy when it comes to validation of data. For example, in case an enrollment is COMPLETED, the user should be not allowed to create new events. SDK repositories do not validate this and will just create the new event; the app is responsible for blocking the creation of new events.

In order to make this task easier, the SDK includes some services that evaluate pure DHIS2 business logic. They are located in their related module and usually are suffixed by `Service`. Some examples of this:

```java
// Event services
d2.eventModule().eventService()
    | .canAddEventToEnrollment("enrollment_uid", "program_stage_uid")
    | .getEditableStatus("event_uid")
    | .isEditable("event_uid") 
    | .hasDataWriteAccess("event_uid")

// Enrollment services
d2.enrollmentModule().enrollmentService()
    | .getEnrollmentAccess("tracked_entity_instance_uid", "program_uid")
    | .isOpen("enrollment_uid")

// Tracked entity instance services
d2.trackedEntityModule().trackedEntityInstanceService()
    | .inheritAttributes("from_tei_uid", "to_tei_uid", "program_uid")
```

Check the javadoc documentation in the IDE to know more details about each method.

## Value Type helper

The SDK include a enum helper class called `ValueType`. This class defines all the different types of values that you can find in DHIS2. In addition, for each of them you can find a validator for the type of value and information about the type of value it is, whether it is a date, a string, a boolean or other type.
The ValueTypes offered by the SDK are listed below:

- `TEXT`
- `LONG_TEXT`
- `LETTER`
- `BOOLEAN`
- `TRUE_ONLY`
- `DATE`
- `DATETIME`
- `TIME`
- `NUMBER`
- `UNIT_INTERVAL`
- `PERCENTAGE`
- `INTEGER`
- `INTEGER_POSITIVE`
- `INTEGER_NEGATIVE`
- `INTEGER_ZERO_OR_POSITIVE`
- `FILE_RESOURCE`
- `COORDINATE`
- `PHONE_NUMBER`
- `EMAIL`
- `USERNAME`
- `ORGANISATION_UNIT`
- `TRACKER_ASSOCIATE`
- `AGE`
- `URL`
- `IMAGE(String::class.java, UidValidator)`

To access the type of value you can simply access it through the methods of the valueType.

```java
    valueType.isDate();
    valueType.isCoordinate();
    valueType.isNumeric();
    valueType.isText();
    valueType.isBoolean();
    valueType.isDate();
    valueType.isFile();
    valueType.isInteger();
```

To validate a value starting from its valueType it can be done in the following way:

```java
    valueType.getValidator().validate("value");
```

This validator will return a `Result` which can be `Success` or `Failure`. In addition each `ValueType` will return different types of errors making it easier to identify what the problem is if the value does not pass validation.