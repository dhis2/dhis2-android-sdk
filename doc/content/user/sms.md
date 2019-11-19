# SMS module

<!--DHIS2-SECTION-ID:sms_module-->

To get SMS module object.

```java
d2.smsModule()
```

There are 3 classes that give access to modules features.

- ConfigCase
- QrCodeCase
- SmsSubmitCase

## ConfigCase

The `ConfigCase` class is used to set initial data that is common for
all sms sending tasks like gateway numbers, timeout, execute downloading
of metadata ids object.

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

## QrCodeCase

The `QrCodeCase` is used to convert *DHIS2* data to String. This String
is a compressed representation of the *DHIS2* data. This is useful to
avoid send large content on SMSes.

The next code snippet shows how to access to the `QrCodeCase` object:

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

## SmsSubmitCase

```java
d2.smsModule().smsSubmitCase()
```

Used to send SMS and to check result response.

```java
// get sender
SmsSubmitCase smsSender = d2.smsModule().smsSubmitCase();

// convert data, returns a number of messages, so app can continue or not
Single<Integer> convertTask = smsSender.convertEnrollment(inputArguments.getEnrollmentId())

// send data converted earlier, returns stream of current states
Observable<SmsRepository.SmsSendingState> sendingTask = smsSender.send();

// wait for result sms, completion means it was received successfully
// in case if result not found, returns error
// given date is a minimum date, when message should be received, used to skip old messages that may have the same submission id
Completable checkResultTask = smsSender.checkConfirmationSms(new Date());
```
