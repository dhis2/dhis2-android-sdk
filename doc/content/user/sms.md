# SMS module

<!--DHIS2-SECTION-ID:sms_module-->

SMS Module can be used as a fallback method to upload data when an Internet connection is not available. It requires an additional setup in the server: an SMS gateway must be configured in the server to be able to receive SMS; optionally, the server might have the capability to send SMS back to the clients with the response.

Depending on the mobile provider, sending SMS might imply an extra cost. For this reason, SMS Module is only intended for **granular data upload**. It is not used for metadata download or bulk data download/upload. Additionally, the data is compressed by using the [SMS compression library](https://github.com/dhis2/sms-compression) so the content can fit in a lower number of messages. This library is shared with the backend.

For testing purposes you can use the [DHIS2 Android SMS Gateway](https://github.com/dhis2/dhis2-sms-android-gateway).

In the SDK, the SMS module can be accessed from `D2`.

```java
d2.smsModule()
```

This module is **disabled by default** and it must be explicitly enabled and configured. It includes three components that give access to module features.

- ConfigCase: it is used to set initial data that is common for all sms sending tasks like gateway numbers, timeout, execute downloading
of metadata ids object.
- SmsSubmitCase. it is used to convert the *DHIS2* data that will be sent by the Sdk, to send it by SMS and to check the progress of the submission and his result.
- QrCodeCase: it is used to convert *DHIS2* data to String. This String is a compressed representation of the *DHIS2* data. This is useful to avoid send large content on SMSes.

A typical workflow to use the SMS Module would be like:

- Enable the SMS module.
- Sync metadata. The SMS Module downloads additional metadata from the server, so this step must be done while Internet connection is available and **after** the module is enabled.
- Send data using SMS Module.

This a code example of a typical workflow (it used blocking methods for code simplicity):

```java
// Enable SMS Module
d2.smsModule().configCase().setModuleEnabled(true).blockingAwait();

// Sync SMS Module metadata using SMS Module
d2.smsModule().configCase().refreshMetadataIds().blockingAwait();
// or using metadata module
d2.metadataModule().blockingDownload();

// Configure, at least, the gateway number. See ConfigCase for more parameters
d2.smsModule().configCase().setGatewayNumber("gateway-number").blockingAwait();

// Send data. For example a tracker event:
SmsSubmitCase case = d2.smsModule().smsSubmitCase();
Integer numSMSs = case.convertTrackerEvent("event-uid").blockingGet();

case.send().blockingSubscribe();
```

> *Important*: the app is responsible for asking the user for permissions (READ_PHONE_STATE, SEND_SMS, READ_SMS, RECEIVE_SMS). Otherwise, SMS module will fail.

## ConfigCase

```java
d2.smsModule().configCase()
```

`ConfigCase` contains the next methods:

- `setModuleEnabled()`. Enable the sms module. 
- `getSmsModuleConfig()`. Returns a single which contains a the
  `SmsConfig` object with the next properties:
  - ModuleEnabled.
  - Gateway
  - WaitingForResult
  - ResultSender
  - ResultWaitingTimeout
- `setMetadataDownloadConfig()`. Configure the metadata download. This
  method accepts a `GetMetadataIdsConfig` object which contains a the
  next list of booleans:
  - DataElements.
  - CategoryOptionCombos.
  - OrganisationUnits.
  - Users.
  - TrackedEntityTypes.
  - TrackedEntityAttributes.
  - Programs.
- `getMetadataDownloadConfig()`. Returns the `GetMetadataIdsConfig`
  object.
- `getDefaultMetadataDownloadConfig()`. Returns the default
  `GetMetadataIdsConfig` object. By default all booleans are true.
- `refreshMetadataIds()`. If the sms module is enabled, the Sdk tries to
  set the `SMSMetadata` with the actual configuration, if there is no
  configuration, the Sdk uses the default configuration.
- `refreshMetadataIdsCallable()`. Encapsulates the method
  `refreshMetadataIds()` above in a `Callable` object.
- `setWaitingForResultEnabled()`. Configure the Sdk to wait for the
  result.
- `setConfirmationSenderNumber()`. Configure the sender number to which
  the confirmation will be sent.  
- `setWaitingResultTimeout()`. Configure the maximum time in seconds to
  wait for the result.
- `setGatewayNumber()`. Configure the gateway number.

## SmsSubmitCase

```java
d2.smsModule().smsSubmitCase()
```

The next methods can be used to set the *DHIS2* data to send:

- `convertSimpleEvent()´. To set a simple event.
- `convertTrackerEvent()´. To set a tracker event.
- `convertEnrollment()´. To set an enrollment.
- `convertDataSet()´. To set a data set.
- `convertRelationship()´. To set a relationship.
- `convertDeletion()´. To delete an identifiable *DHIS2* object.

The methods above returns a single with the number of messages that the
items takes up. An example of the use of these methods is shown in the
next snippet.

```java
Single<Integer> convertTask = d2.smsModule().smsSubmitCase()
    .convertEnrollment("enrollment_uid")
```

To send the data converted earlier the Sdk provides a `send()` method
that returns a stream of the current states. Also it is possible to get
the submission id by calling the method `getSubmissionId()`.

```java
d2.smsModule().smsSubmitCase().send()
```

It is also possible to wait for the SMS result by calling the
`checkConfirmationSms()` method. It returns a `Completable` object where
completion means that the SMS was received successfully. In case that
the result cannot be found, it returns an error. The date accepted is
the minimum date for which confirmation is going to be checked, this is
used to skip old messages that may have the same submission id.

```java
d2.smsModule().smsSubmitCase().checkConfirmationSms(new Date());
```

These methods can fail and return a `PreconditionFailed` object if some
conditions are not satisfied. The preconditions errors are:

- `NO_NETWORK`.
- `NO_CHECK_NETWORK_PERMISSION`.
- `NO_RECEIVE_SMS_PERMISSION`.
- `NO_SEND_SMS_PERMISSION`.
- `NO_GATEWAY_NUMBER_SET`.
- `NO_USER_LOGGED_IN`.
- `NO_METADATA_DOWNLOADED`.
- `SMS_MODULE_DISABLED`.

## QrCodeCase

```java
d2.smsModule().qrCodeCase()
```

`QrCodeCase` can convert the next type of *DHIS2* objects:

- **Simple events**. Using the `generateSimpleEventCode()` method and
  passing an event uid.
- **Tracker events**. Using the `generateTrackerEventCode()` method and
  passing an event uid. 
- **Enrollments**. Using the `generateEnrollmentCode()` method and
  passing an enrollment uid.
- **Relationships**. Using the `generateRelationshipCode()` method and
  passing a relationship uid.
- **Data sets**. Using the `generateDataSetCode()` method and passing a
  data set uid, an organisation unit uid, an attribute option combo and
  a period id.
  
Also it is possible to get compressed strings that can be used to delete
identifiable objects:

- **Deletions**. Using the `generateDeletionCode()` method and passing
  the uid of the identifiable object to delete.
  
These methods returns a `Single` with the compressed data. The next code
snippet shows an example of how it can be used.

```java
Single<String> convertTask = d2.smsModule().qrCodeCase().generateEnrollmentCode(enrollmentUid);
```
