# DHIS2 services { #android_sdk_dhis2_services }

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