To get SMS module object.
```java
d2.smsModule()
```

There are 3 classes that give access to modules features.
- ConfigCase
- SmsSubmitCase
- QrCodeCase

##### ConfigCase
Used to configure fields like gateway numbers, timeout, execute downloading of metadata ids object.
```java
d2.smsModule().configCase()
```

##### QrCodeCase
Used to convert data item to String
```java
d2.smsModule().qrCodeCase()
```
```java
Single<String> convertTask = d2.smsModule().qrCodeCase().generateEnrollmentCode(enrollmentUid);
```
##### SmsSubmitCase
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
